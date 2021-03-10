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
import org.joda.time.Duration
import org.joda.time.Instant
import org.joda.time.ReadableInstant

/**
 * @author cookiedragon234 15/Sep/2020
 */
object OrderTable : IntIdTable() {
	val user = reference("user", UsersTable)
	val product = reference("product", ProductsTable)
	val datePurchased = datetime("datePurchased")
	val address = varchar("address", 255)
	val amountExpected = long("expected")
	val amountPaid = long("paid")
	val amountUnconfirmed = long("unconfirmed")
	val completed = bool("completed")
	val rate = double("rate")
}

val ORDER_EXPIRE_AFTER = Duration.standardMinutes(25)

class Order(id: EntityID<Int>) : IntEntity(id) {
	companion object : IntEntityClass<Order>(OrderTable)
	
	var user by User referencedOn OrderTable.user
	var product by Product referencedOn OrderTable.product
	var datePurchased by OrderTable.datePurchased
	var address by OrderTable.address
	var amountExpected by OrderTable.amountExpected
	var amountPaid by OrderTable.amountPaid
	var amountUnconfirmed by OrderTable.amountUnconfirmed
	var completed by OrderTable.completed
	var rate by OrderTable.rate
	
	fun hasExpired(): Boolean = Instant.now().isAfter(datePurchased.plus(ORDER_EXPIRE_AFTER))
	
	override fun toString(): String {
		return """
			|Order(
			|   id=${id.value}
			|   product=$product
			|   datePurchased=$datePurchased
			|   address=$address
			|   amountExpected=$amountExpected
			|   amountPaid=$amountPaid
			|   amountUnconfirmed=$amountUnconfirmed
			|   expired=${hasExpired()}
			|   completed=$completed
			|   rate=$rate
			|)
		""".trimMargin()
	}
}
