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

/**
 * @author cook 17/Nov/2020
 */
object OrdersTable : IntIdTable() {
	val user = reference("user", UsersTable)
	val product = reference("product", ProductsTable)
	val datePurchased = datetime("datePurchased")
	val completed = bool("completed")
	val invoiceId = varchar("invoiceId", 255)
}

class Orders(id: EntityID<Int>) : IntEntity(id) {
	companion object : IntEntityClass<Orders>(OrdersTable)
	
	var user by User referencedOn OrdersTable.user
	var product by Product referencedOn OrdersTable.product
	var datePurchased by OrdersTable.datePurchased
	var completed by OrdersTable.completed
	var invoiceId by OrdersTable.invoiceId
	
	override fun toString(): String {
		return """
			|Order(
			|   id=${id.value}
			|   product=$product
			|   datePurchased=$datePurchased
			|   completed=$completed
			|)
		""".trimMargin()
	}
}
