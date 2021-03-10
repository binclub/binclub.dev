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
import dev.binclub.web.authentication.*
import dev.binclub.web.storage.User
import dev.binclub.web.storage.UsersTable
import dev.binclub.web.storage.registerUser
import dev.binclub.web.storage.userByEmail
import dev.binclub.web.utils.isPresent
import dev.binclub.web.utils.redirect
import io.ktor.application.call
import io.ktor.auth.UserPasswordCredential
import io.ktor.http.Parameters
import io.ktor.http.encodeURLQueryComponent
import io.ktor.request.receive
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.sessions.clear
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import kotlinx.coroutines.delay
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.io.File
import kotlin.random.Random

/**
 * @author cookiedragon234 04/Jun/2020
 */
fun Route.changePasswordEndpoint() {
	get("reset") {
		val user = getUserSession()
			?: redirect("/login?type=err&msg=${"Please login to access this page".encodeURLQueryComponent()}")
		
		respond(File("html/reset/reset.html"))
	}
	post("reset") {
		val user = getUserSession()
			?: redirect("/login?type=err&msg=${"Please login to access this page".encodeURLQueryComponent()}")
		
		val params = call.receive<Parameters>()
		val oldPass = params["password"]
		val newPass = params["new-password"]
		
		val captcha = validateCaptcha(params)
			?: redirect("/reset?type=err&msg=${"Please complete the captcha".encodeURLQueryComponent()}")
		if (!captcha.success) {
			if (captcha.error == "timeout-or-duplicate" || captcha.error == "invalid-input-response") {
				redirect("/reset?type=err&msg=${"Invalid captcha, please try again".encodeURLQueryComponent()}")
			}
			error("Internal captcha error \"${captcha.error}\"")
		}
		
		if (oldPass.isPresent() && newPass.isPresent()) {
			oldPass!!; newPass!!
			
			val updated = transaction {
				if (BCrypt.checkpw(oldPass, user.password)) {
					val newHash = hashPassword(newPass)
					user.password = newHash
					true
				} else {
					false
				}
			}
			if (updated) {
				call.sessions.clear<UserSession>()
				
				analytics?.event()
					?.eventCategory("Password Reset")
					?.eventAction("Successful reset")
					?.fromCall(call)
					?.sendAsync()
				
				// Shitty timer attack prevention
				delay(Random.nextInt(2000).toLong())
				redirect("/login?type=succ&msg=${"Your password was changed".encodeURLQueryComponent()}")
			} else {
				analytics?.event()
					?.eventCategory("Password Reset")
					?.eventAction("Invalid credentials")
					?.fromCall(call)
					?.sendAsync()
				
				// Shitty timer attack prevention
				delay(Random.nextInt(2000).toLong())
				redirect("/reset?type=err&msg=${"Invalid credentials".encodeURLQueryComponent()}")
			}
		}
		analytics?.event()
			?.eventCategory("Password Reset")
			?.eventAction("Invalid parameters")
			?.fromCall(call)
			?.sendAsync()
		
		delay(200)
		redirect("/reset?type=err&msg=${"Please fill in both email and password".encodeURLQueryComponent()}")
	}
}
