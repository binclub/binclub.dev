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

package dev.binclub.web.utils

import dev.binclub.web.httpClient
import io.ktor.http.*
import io.ktor.util.hex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.apache.http.client.methods.HttpGet
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.URI
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

/**
 * @author cookiedragon234 29/Feb/2020
 */

// As you may have been able to tell I have no clue how to use coroutines
suspend fun <T> dbQuery(block: () -> T): T =
	withContext(Dispatchers.IO) {
		transaction { block() }
	}

fun <T> blockingDbQuery(block: () -> T): T =
	runBlocking {
		dbQuery(block)
	}

fun CharSequence?.isPresent() = this?.isNotBlank() ?: false

private const val USER_AGENT = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2"
fun URI.get(headers: Map<String, String> = Collections.emptyMap()): String {
	return httpClient.execute(HttpGet(this).apply {
		addHeader(HttpHeaders.UserAgent, USER_AGENT)
		headers.forEach { (t, u) ->
			addHeader(t, u)
		}
	}).use { response ->
		if (response.statusLine.statusCode != 200) {
			println("Response for url $this")
			println(response.entity.content.bufferedReader().readText())
			error("Bad response ${response.statusLine.statusCode}")
		}
		response.entity.content.bufferedReader().readText()
	}
}
