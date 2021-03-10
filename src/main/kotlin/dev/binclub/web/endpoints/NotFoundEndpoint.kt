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

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.http.HttpStatusCode
import io.ktor.routing.Route
import io.ktor.routing.route
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.delay
import kotlinx.html.*

/**
 * @author cookiedragon234 08/Mar/2020
 */
fun Route.notFoundEndpoint() {
	route("{...}") {
		handle {
			handleNotFound()
		}
	}
}

suspend fun PipelineContext<Unit, ApplicationCall>.handleNotFound() {
	delay(2000)
	call.respondHtml(HttpStatusCode.NotFound) {
		head {
			title {
				+"404"
			}
		}
		body {
			h1 {
				+"404 Not Found"
			}
			p {
				code { +"Couldn't find ${call.request.local.uri}" } // Note: ktor escapes this for us ;)
			}
		}
	}
}
