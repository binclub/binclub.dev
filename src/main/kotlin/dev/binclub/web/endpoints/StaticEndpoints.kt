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

package dev.binclub.web.endpoints

import dev.binclub.web.authentication.getUserSession
import dev.binclub.web.storage.Download
import dev.binclub.web.storage.License
import dev.binclub.web.storage.Product
import dev.binclub.web.utils.*
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.content.LocalFileContent
import io.ktor.http.content.static
import io.ktor.http.encodeURLQueryComponent
import io.ktor.response.*
import io.ktor.routing.Route
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.util.combineSafe
import io.ktor.util.pipeline.PipelineContext
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.util.*


/**
 * @author cookiedragon234 08/Mar/2020
 */
fun Route.staticEndPoints() {
	// This sorts downloads - reverse order to sort the version numbers high to low
	val downloadComp = Comparator.comparing(Download::name).reversed()
	
	// Yes, I lied! It isn't static content. But you fell for it...
	get("dashboard") {
		val user = getUserSession()
			?: redirect("/login?type=err&msg=${"Please login to access this page".encodeURLQueryComponent()}")
		val out = transaction {
			val licenses = user.licenses.with(License::user, License::product, Product::downloads)
			val downloads = TreeSet(downloadComp).apply {
				licenses.forEach { addAll(it.product.downloads.with(Product::downloads)) }
			}
			FreeMarkerContent(
				"dashboard.ftl",
				mapOf(
					"user" to user,
					"licenses" to licenses,
					"downloads" to downloads,
					"formatDate" to FreeMarkerDateFormatterMethod,
					"formatText" to FreemarkerFormattingTransformerMethod
				)
			).compileAot()
		}
		call.respondHtmlStr(out)
	}
	
	get("login") {
		val isLoggedIn = getUserSession() != null
		if (isLoggedIn) {
			redirect("/dashboard")
		}
		respond(File("html/login/index.html"))
	}
	
	get("signup") {
		redirect("/login", true)
	}
	
	get("register") {
		redirect("/login", true)
	}
	
	get("signin") {
		redirect("/login", true)
	}
	
	get("toc") {
		// yes maybe I should rename the file so it works with the static files stuff
		respond(File("html/toc/toc.html"))
	}
	
	static {
		// If none of these predefined fallback to static html files
		files("html")
	}
}

private const val pathParameterName = "static-content-path-parameter"

fun Route.files(folder: String) {
	// Root folder to serve content from
	val dir = File(folder)
	
	get("{$pathParameterName...}") {
		val relativePath = call.parameters.getAll(pathParameterName)?.joinToString(File.separator) ?: return@get
		var file = dir.combineSafe(relativePath) // this better be safe :<
		if (file.isDirectory) file = File(file, "index.html")
		
		// dont allow indexing for special directories
		when (file.parent) {
			"dashboard" -> call.response.header("x-robots-tag", "noindex, nofollow, nosnippet")
			"reset" -> call.response.header("x-robots-tag", "noindex, nofollow, nosnippet")
			"policy" -> call.response.header("x-robots-tag", "noindex, nofollow, nosnippet")
			"script" -> call.response.header("x-robots-tag", "noindex, nofollow, nosnippet")
			"style" -> call.response.header("x-robots-tag", "noindex, nofollow, nosnippet")
			"toc" -> call.response.header("x-robots-tag", "noindex, nofollow, nosnippet")
		}
		
		respond(file)
	}
}

suspend fun PipelineContext<Unit, ApplicationCall>.respond(file: File) {
	when {
		file.isFile -> call.respond(LocalFileContent(file))
		file.isDirectory -> respond(File(file, "index.html"))
		else -> handleNotFound()
	}
}
