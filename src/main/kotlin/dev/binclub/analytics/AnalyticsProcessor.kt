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

package dev.binclub.analytics

import com.brsanthu.googleanalytics.GoogleAnalytics
import com.brsanthu.googleanalytics.GoogleAnalyticsConfig
import com.brsanthu.googleanalytics.request.DefaultRequest
import com.brsanthu.googleanalytics.request.GoogleAnalyticsRequest
import io.ktor.application.*
import io.ktor.features.origin
import io.ktor.http.Cookie
import io.ktor.http.HttpHeaders
import io.ktor.http.URLBuilder
import io.ktor.request.*
import io.ktor.sessions.Sessions
import io.ktor.util.AttributeKey
import io.ktor.util.InternalAPI
import io.ktor.util.createFromCall
import io.ktor.util.date.GMTDate
import kotlinx.coroutines.coroutineScope
import java.lang.RuntimeException
import java.util.*

/**
 * @author cookiedragon234 22/Jun/2020
 */
class Analytics(config: Configuration) {
	val ga = GoogleAnalytics.builder()
		.withConfig(GoogleAnalyticsConfig().setValidate(true).setBatchingEnabled(true).setBatchSize(4))
		.withTrackingId(config.key)
		.build()
	
	class Configuration {
		lateinit var key: String
	}
	
	companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, Analytics> {
		override val key: AttributeKey<Analytics> = AttributeKey("Analytics")
		
		override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): Analytics {
			val configuration = Configuration().also(configure)
			val analytics = Analytics(configuration)
			
			pipeline.intercept(ApplicationCallPipeline.Monitoring) {
				try {
					coroutineScope {
						proceed()
					}
				} catch (exception: Throwable) {
					analytics.ga.exception()
						.exceptionDescription(exception.message)
						.exceptionFatal(exception !is RuntimeException)
						.fromCall(call)
						.sendAsync()
					throw exception
				}
				
				analytics.ga.pageView()
					.fromCall(call)
					.sendAsync()
			}
			
			return analytics
		}
	}
}

private const val cookieAgeS = (365 * 24 * 3600) // 365 days in seconds
private const val cookieAgeMS = (365L * 24 * 3600 * 1000) // 365 days in seconds

fun <T: GoogleAnalyticsRequest<*>> T.fromCall(call: ApplicationCall): T {
	// We need to identify this user, so assign them a cookie with a random uuid if they dont already have one
	var uuid = call.request.cookies["client-uuid"]
	if (uuid == null) {
		uuid = call.response.cookies["client-uuid"]?.value
		if (uuid == null) {
			uuid = UUID.randomUUID().toString()
			try {
				call.response.cookies.append(Cookie(
					name = "client-uuid",
					value = uuid,
					maxAge = cookieAgeS,
					expires = GMTDate(System.currentTimeMillis() + cookieAgeMS)
				))
			} catch (t: Throwable) {}
		}
	}
	
	var uri = call.request.uri
	if (uri.length > 1) {
		uri = uri.removeSuffix("/")
	}
	
	anonymizeIp(true)
	documentEncoding(call.request.contentCharset()?.name())
	clientId(uuid)
	documentUrl(URLBuilder.createFromCall(call).buildString())
	documentPath(uri)
	documentReferrer(call.request.header(HttpHeaders.Referrer))
	userAgent(call.request.userAgent())
	userIp(call.request.origin.remoteHost)
	userLanguage(call.request.acceptLanguage())
	return this
}
