/**
 * binclub.dev - Software for hosting a website to promote Binclub products and accommodate accounts
 * Copyright (C) 2021  x4e
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.binclub.web

import com.brsanthu.googleanalytics.GoogleAnalytics
import dev.binclub.analytics.Analytics
import dev.binclub.web.authentication.AuthenticationFailure
import dev.binclub.web.blog.blogHost
import dev.binclub.web.endpoints.*
import dev.binclub.web.storage.DatabaseManager
import dev.binclub.web.storage.User
import dev.binclub.web.utils.registerRedirections
import freemarker.cache.FileTemplateLoader
import io.ktor.application.Application
import io.ktor.application.ApplicationStarted
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.freemarker.FreeMarker
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.host
import io.ktor.server.netty.*
import io.ktor.sessions.SessionTransportTransformerMessageAuthentication
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.ktor.util.error
import org.apache.http.impl.client.HttpClientBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import kotlin.random.Random

/**
 * @author cookiedragon234 28/Feb/2020
 */

lateinit var arguments: Array<String>
var analytics: GoogleAnalytics? = null

// Used for sessions
// We generate a new key every time the server starts up
// This invalidates sessions every time this happens
val hashKey = Random.nextBytes(25)

val httpClient = HttpClientBuilder.create()
	.build()

fun main(args: Array<String>) {
	arguments = args
	EngineMain.main(args)
}

fun Application.module() {
	val nodb = arguments.contains("nodb") // for testing
	if (!nodb) {
		DatabaseManager.init()
	}
	
	environment.monitor.subscribe(ApplicationStarted) {
		println("Application Ready")
	}
	install(DefaultHeaders)
	install(Compression)
	install(CallLogging)
	install(XForwardedHeaderSupport)
	install(ContentNegotiation)
	install(StatusPages) {
		registerRedirections()
		exception<AuthenticationFailure> { cause ->
			val error = HttpBinError(
				code = HttpStatusCode.Unauthorized,
				request = call.request.local.uri,
				message = cause.message ?: "Unknown Authentication Failure",
			)
			call.respondText(error.toString())
		}
		exception<Throwable> { cause ->
			cause.printStackTrace()
			environment.log.error(cause)
			val error = HttpBinError(
				code = HttpStatusCode.InternalServerError,
				request = call.request.local.uri,
				message = "Internal Server Error"
			)
			call.respondText(error.toString())
		}
	}
	install(Sessions) {
		cookie<UserSession>("SESSION") {
			transform(SessionTransportTransformerMessageAuthentication(hashKey))
		}
	}
	Secrets.GA_ID?.let { GA_ID ->
		analytics = install(Analytics) {
			key = GA_ID
		}.ga
	}
	install(Routing) {
		// We should really be using nginx
		host(Regex("i.binclub.dev")) {
			static {
				files("i")
			}
			notFoundEndpoint()
		}
		blogHost()
		host(listOf("www.binclub.dev", "binclub.dev", "localhost")) {
			staticEndPoints()
			paymentEndpoint()
			licensingEndpoint()
			routeAuthentication()
			downloadEndpoints()
			discordEndpoint()
			changePasswordEndpoint()
			notFoundEndpoint()
		}
	}
	install(FreeMarker) {
		templateLoader = FileTemplateLoader(File("html/templates"))
	}
	
	if (!nodb) {
		transaction {
			val users = User.all()
			println("Num Users: ${users.count()}")
		}
	}
}
