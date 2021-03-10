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

package dev.binclub.web.authentication

import com.google.gson.JsonParser
import dev.binclub.web.Secrets
import dev.binclub.web.utils.use
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.features.origin
import io.ktor.http.Parameters
import io.ktor.http.content.OutgoingContent
import io.ktor.http.formUrlEncode
import io.ktor.util.pipeline.PipelineContext
import io.ktor.utils.io.ByteWriteChannel
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


/**
 * @author cookiedragon234 16/Mar/2020
 */
const val production = true

data class RecaptchaKeyStorage(
	val public: String,
	val private: String
)

val keys = if (production) RecaptchaKeyStorage(
	"6LcSo-EUAAAAAHm3CpnM1uf-8qhg__oBnoNDSgb-",
	Secrets.RECAPTCHA_SECRET!!
) else RecaptchaKeyStorage(
	"6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI",
	"6LeIxAcTAAAAAGG-vFI1TnRWxMZNFuojJ4WifJWe"
)

suspend fun authenticateRecaptcha(
	secretKey: String = keys.private,
	response: String,
	remoteIp: String
): RecaptchaResponse {
	val request = sendPost(
		"https://www.google.com/recaptcha/api/siteverify",
		listOf("secret" to secretKey, "response" to response, "remoteip" to remoteIp)
	)
	
	return JsonParser.parseString(request).asJsonObject.let {
		RecaptchaResponse(it.get("success").asBoolean, it.get("error-codes")?.asJsonArray?.firstOrNull()?.asString)
	}
}

suspend fun sendPost(url: String, params: List<Pair<String, String>>): String {
	val urlParameters = params.formUrlEncode()
	val postData = urlParameters.toByteArray()
	
	return (URL(url).openConnection() as HttpURLConnection).use { con ->
		con.doOutput = true
		con.requestMethod = "POST"
		con.setRequestProperty("User-Agent", "Java client")
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
		
		DataOutputStream(con.outputStream).use { it.write(postData) }
		
		BufferedReader(InputStreamReader(con.inputStream)).readText()
	}
}

suspend fun PipelineContext<*, ApplicationCall>.validateCaptcha(params: Parameters): RecaptchaResponse? {
	val response = params["g-recaptcha-response"] ?: return null
	val ip = call.request.origin.remoteHost
	
	return authenticateRecaptcha(response = response, remoteIp = ip)
}

data class RecaptchaResponse(
	val success: Boolean,
	val error: String?
)
