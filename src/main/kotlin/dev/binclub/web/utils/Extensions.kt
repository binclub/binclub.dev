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

import io.ktor.application.ApplicationCall
import io.ktor.http.ContentType
import io.ktor.http.withCharset
import io.ktor.response.respondText
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.QueryBuilder
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.net.HttpURLConnection
import kotlin.collections.ArrayList
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance

/**
 * @author cookiedragon234 01/Mar/2020
 */
inline fun <T : HttpURLConnection?, R> T.use(block: (T) -> R): R {
	var exception: Throwable? = null
	try {
		return block(this)
	} catch (e: Throwable) {
		exception = e
		throw e
	} finally {
		when {
			this == null -> {
			}
			exception == null -> disconnect()
			else ->
				try {
					disconnect()
				} catch (closeException: Throwable) {
					// cause.addSuppressed(closeException) // ignored here
				}
		}
	}
}

fun <T : Entity<*>, ID : Comparable<ID>, R : Entity<ID>> Iterable<T>.loadReference(
	getRefId: (T) -> ID,
	referenceClass: KClass<R>
): List<Pair<T, R>> {
	val reference = referenceClass.companionObjectInstance as EntityClass<ID, R>
	val ids = map { t -> getRefId(t) }
	if (ids.isEmpty())
		return emptyList()
	val op = object : Op<Boolean>() {
		override fun toQueryBuilder(queryBuilder: QueryBuilder) {
			queryBuilder.append(ids.joinToString(" OR ") { "(id = $it)" })
		}
	}
	val references: Map<ID, R> = reference.find(op).groupBy { it.id.value }.mapValues { (_, v) -> v.first() }
	val list = ArrayList<Pair<T, R>>()
	forEach { t ->
		list.add(t to (references[getRefId(t)]!!))
	}
	return list
}

val htmlCharset by lazy {
	ContentType.Text.Html.withCharset(Charsets.UTF_8)
}

suspend fun ApplicationCall.respondHtmlStr(html: String) = respondText(html, htmlCharset)

private val format by lazy {
	DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss")
}

fun DateTime.toFormattedString(): String = format.print(this)

fun StringBuilder.newLine() = append('\n')
