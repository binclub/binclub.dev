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

import dev.binclub.web.endpoints.UserSession
import dev.binclub.web.storage.User
import dev.binclub.web.storage.UsersTable
import dev.binclub.web.storage.userById
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.UserPasswordCredential
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.util.pipeline.PipelineContext
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.lang.UnsupportedOperationException

/**
 * @author cookiedragon234 01/Mar/2020
 */
fun authenticate(credential: UserPasswordCredential): User? {
	return AuthenticationMap[credential.name]?.let { user ->
		if (BCrypt.checkpw(credential.password, user.password)) {
			user
		} else {
			null
		}
	}
}

fun User.provideSession(): UserSession = UserSession(this.id.value)

private val emailPattern = ("" +
"(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21" +
"\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+" +
"[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?" +
"[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b" +
"\\x0c\\x0e-\\x7f])+)\\])").toRegex()

fun isValidEmail(email: CharSequence): Boolean = emailPattern.matches(email)

private object AuthenticationMap : Map<String, User> {
	override val entries: Set<Map.Entry<String, User>>
		get() = throw UnsupportedOperationException()
	override val keys: Set<String>
		get() = throw UnsupportedOperationException()
	override val size: Int
		get() = throw UnsupportedOperationException()
	override val values: Collection<User>
		get() = throw UnsupportedOperationException()
	
	override fun containsKey(key: String): Boolean = transaction {
		User.find {
			UsersTable.email eq key
		}.empty()
	}
	
	override fun containsValue(value: User): Boolean = transaction {
		User.find {
			UsersTable.id eq value.id
		}.empty()
	}
	
	override fun get(key: String): User? = transaction {
		User.find {
			UsersTable.email eq key
		}.firstOrNull()
	}
	
	override fun isEmpty(): Boolean = transaction {
		User.all().empty()
	}
}

open class AuthenticationFailure(msg: String) : RuntimeException(msg)
class InvalidCredentialsException(msg: String) : AuthenticationFailure(msg)

fun PipelineContext<Unit, ApplicationCall>.getUserSession(): User? {
	return call.sessions.get<UserSession>()?.let {
		userById(it.userId)
	}
}
