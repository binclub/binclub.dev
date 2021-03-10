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

package dev.binclub.web.blog

import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.utils.io.*

/**
 * What do we do when we don't want to setup nginx?
 * Make a horrible custom proxy implementation
 *
 * @author cookiedragon234 24/Aug/2020
 */
const val BLOG_ADDR = "localhost:8080"

fun Route.blogHost() {
	val client = HttpClient()
	host(listOf("blog.binclub.dev", "blog.localhost")) {
		route("{path-param...}") {
			handle {
				// locally proxy to the writefreely instance
				// http is fine for local connection
				val proxyUrl = "http://$BLOG_ADDR/${call.request.uri}"
				
				val req = HttpRequestBuilder().apply {
					url(proxyUrl)
					call.request.headers[HttpHeaders.Cookie]?.let {
						headers.append(HttpHeaders.Cookie, it)
					}
					method = call.request.local.method
					body = object : OutgoingContent.WriteChannelContent() {
						override val contentType: ContentType? = call.request.contentType()
						
						override suspend fun writeTo(channel: ByteWriteChannel) {
							call.request.receiveChannel().copyAndClose(channel)
						}
					}
				}
				val response = client.request<HttpResponse>(req)
				val proxiedHeaders = response.headers
				
				// Most headers can be ignored since they will conflict with ktor's own added headers
				// At the moment I think only Location and SetCookie will actually be needed to be forwarded
				proxiedHeaders[HttpHeaders.Location]?.let {
					call.response.headers.append(HttpHeaders.Location, it)
				}
				proxiedHeaders[HttpHeaders.SetCookie]?.let {
					call.response.headers.append(HttpHeaders.SetCookie, it)
				}
				
				call.respond(object : OutgoingContent.WriteChannelContent() {
					override val contentType: ContentType? = (response.contentType() ?: ContentType.Text.Html)
					override val status: HttpStatusCode? = response.status
					
					override suspend fun writeTo(channel: ByteWriteChannel) {
						response.content.copyAndClose(channel)
					}
				})
			}
		}
	}
}
