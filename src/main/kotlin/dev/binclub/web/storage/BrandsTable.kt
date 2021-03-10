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

import dev.binclub.web.storage.Product.Companion.referrersOn
import dev.binclub.web.storage.ProductsTable.uniqueIndex
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.jodatime.datetime

/**
 * @author cookiedragon234 10/May/2020
 */
object BrandsTable : IntIdTable() {
	val name = varchar("name", 255).uniqueIndex()
	val discountExistingUsers = bool("discountExistingUsers")
}

class Brand(id: EntityID<Int>) : IntEntity(id) {
	companion object : IntEntityClass<Brand>(BrandsTable)
	
	var name by ProductsTable.name
	var dateReleased by ProductsTable.dateReleased
	var price by ProductsTable.price
	var available by ProductsTable.available
	val downloads by Download referrersOn DownloadsTable.product
	
	override fun toString(): String {
		return """
			|Product(
			|   name=$name
			|   dateReleased=$dateReleased
			|   price=$price
			|   available=$available
			|)
		""".trimMargin()
	}
}
