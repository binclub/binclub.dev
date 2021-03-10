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

import dev.binclub.analytics.fromCall
import dev.binclub.web.analytics
import dev.binclub.web.authentication.getUserSession
import dev.binclub.web.storage.Download
import dev.binclub.web.utils.redirect
import io.ktor.application.call
import io.ktor.http.*
import io.ktor.request.receive
import io.ktor.response.*
import io.ktor.routing.Route
import io.ktor.routing.post
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

/**
 * @author cookiedragon234 02/Apr/2020
 */
fun Route.downloadEndpoints() {
	post("download.jar") {
		val user = getUserSession()
			?: redirect("/login?type=err&msg=${"Please login to access this page".encodeURLQueryComponent()}")
		val params = call.receive<Parameters>()
		val id = (params["id"] ?: error("Invalid request")).toInt()
		
		val download = transaction {
			(Download.findById(id) ?: error("No matching download")).also { download ->
				user.licenses.firstOrNull { it.product.downloads.contains(download) }
					?: error("You do not have not purchased this download")
			}
		}
		
		val file = File(download.file)
		if (!file.exists()) {
			IllegalStateException("Internal file not found ${file.absoluteFile}").printStackTrace()
			call.respondText("File ${file.name} not found", status = HttpStatusCode.InternalServerError)
			return@post
		}
		
		analytics?.event()
			?.eventCategory("Download")
			?.eventAction("Download")
			?.eventLabel(file.path)
			?.fromCall(call)
			?.sendAsync()
		
		call.response.header(
			HttpHeaders.ContentDisposition,
			ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, file.name).toString()
		)
		
		val bytes = file.readBytes()
		call.respondBytesWriter {
			writeDownload(this, bytes, user.id.value)
		}
	}
}
