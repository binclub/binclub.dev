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

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import dev.binclub.web.authentication.getUserSession
import dev.binclub.web.storage.*
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * @author cookiedragon234 15/Sep/2020
 */
fun Route.paymentEndpoint() {
	val gson = GsonBuilder().create()
	
	route("payments") {
		get {
			call.respondText("Secret")
		}
		
		post("status") {
		}
		
		post("new") {
			val user = getUserSession()
			if (user == null) {
				call.respondText("loggedout")
				return@post
			}
			
			val params = gson.fromJson(call.receiveText(), JsonObject::class.java)
			val productCode = params.getAsJsonPrimitive("product").asDouble.toInt() // dont ask
			
			val alreadyOwns = transaction {
				user.licenses.any {
					it.product.id.value == productCode
				}
			}
			
			if (alreadyOwns) {
				call.respondText("You already own this product")
				return@post
			}
			
			val product = transaction {
				Product.findById(productCode)
			}
			
			if (product == null) {
				call.respondText("Product $productCode could not be found")
				return@post
			}
			
			if (!product.available) {
				call.respondText("This product is not available for purchase at this time")
				return@post
			}
			
			// TODO
			call.respondText("ok")
			return@post
		}
	}
}
