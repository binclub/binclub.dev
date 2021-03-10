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

package dev.binclub.web.storage

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.jodatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

/**
 * @author cookiedragon234 28/Feb/2020
 */
object UsersTable : IntIdTable() {
	val email = varchar("email", 255).uniqueIndex()
	val password = varchar("password", 255)
	val dateCreated = datetime("dateCreated")
}

class User(id: EntityID<Int>) : IntEntity(id) {
	companion object : IntEntityClass<User>(UsersTable)
	
	var email by UsersTable.email
	var password by UsersTable.password
	var dateCreated by UsersTable.dateCreated
	val licenses by License referrersOn LicensesTable.user
	
	override fun toString(): String {
		return """
			|User(
			|   id=${id.value}
			|   email=$email
			|   password=$password
			|   dateCreated=$dateCreated
			|)
		""".trimMargin()
	}
}

fun userById(userId: Int): User? = transaction {
	User.find {
		UsersTable.id eq userId
	}.singleOrNull()
}

fun userByEmail(email: String): User? = transaction {
	User.find {
		UsersTable.email eq email
	}.singleOrNull()
}

fun registerUser(
	email: String,
	password: String,
	dateCreated: DateTime = DateTime.now()
): User = transaction {
	User.new {
		this.email = email
		this.password = password
		this.dateCreated = dateCreated
	}
}
