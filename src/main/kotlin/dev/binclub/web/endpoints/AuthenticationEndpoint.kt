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
import dev.binclub.web.authentication.authenticate
import dev.binclub.web.authentication.isValidEmail
import dev.binclub.web.authentication.provideSession
import dev.binclub.web.authentication.validateCaptcha
import dev.binclub.web.storage.registerUser
import dev.binclub.web.storage.userByEmail
import dev.binclub.web.utils.isPresent
import dev.binclub.web.utils.redirect
import io.ktor.application.call
import io.ktor.auth.UserPasswordCredential
import io.ktor.http.Parameters
import io.ktor.http.encodeURLQueryComponent
import io.ktor.request.receive
import io.ktor.routing.*
import io.ktor.sessions.clear
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import kotlinx.coroutines.delay
import org.mindrot.jbcrypt.BCrypt
import java.security.SecureRandom
import kotlin.random.Random

/**
 * @author cookiedragon234 02/Mar/2020
 */
fun Route.routeAuthentication() {
	val nocaptcha = System.getenv("nocaptcha") != null
	
	post("login") {
		val params = call.receive<Parameters>()
		val email = params["email"]
		val password = params["password"]
		val redir = params["redir"]?.let { if (it.isBlank()) null else it }
		
		if (!nocaptcha) {
			val captcha = validateCaptcha(params)
				?: redirect("/login?type=err&msg=${"Please complete the captcha".encodeURLQueryComponent()}&redir=$redir")
			if (!captcha.success) {
				if (captcha.error == "timeout-or-duplicate" || captcha.error == "invalid-input-response") {
					redirect("/login?type=err&msg=${"Invalid captcha, please try again".encodeURLQueryComponent()}&redir=$redir")
				}
				error("Internal captcha error \"${captcha.error}\"")
			}
		}
		
		if (email.isPresent() && password.isPresent()) {
			email!!; password!!
			
			authenticate(UserPasswordCredential(email, password))?.let {
				analytics?.event()
					?.eventCategory("Authentication")
					?.eventAction("Successful login")
					?.fromCall(call)
					?.sendAsync()
				
				call.sessions.set(it.provideSession())
				redirect(redir ?: "/dashboard")
			}
			
			analytics?.event()
				?.eventCategory("Authentication")
				?.eventAction("Invalid login Credentials")
				?.fromCall(call)
				?.sendAsync()
			
			// Shitty timer attack prevention
			delay(Random.nextInt(2000).toLong())
			redirect("/login?type=err&msg=${"Invalid credentials".encodeURLQueryComponent()}&redir=$redir")
		}
		
		analytics?.event()
			?.eventCategory("Authentication")
			?.eventAction("Incorrect login parameters")
			?.fromCall(call)
			?.sendAsync()
		
		delay(200)
		redirect("/login?type=err&msg=${"Please fill in both email and password".encodeURLQueryComponent()}&redir=$redir")
	}
	
	post("register") {
		val params = call.receive<Parameters>()
		val email = params["email"]
		val password = params["password"]
		val redir = params["redir"]?.let { if (it.isBlank()) null else it }
		
		val captcha = validateCaptcha(params)
			?: redirect("/login?type=err&msg=${"Please complete the captcha".encodeURLQueryComponent()}&redir=$redir")
		if (!captcha.success) {
			if (captcha.error == "timeout-or-duplicate" || captcha.error == "invalid-input-response") {
				redirect("/login?type=err&msg=${"Invalid captcha, please try again".encodeURLQueryComponent()}&redir=$redir")
			}
			error("Internal captcha error \"${captcha.error}\"")
		}
		
		if (email.isPresent() && password.isPresent()) {
			email!!; password!!
			
			if (isValidEmail(email)) {
				if (userByEmail(email) == null) {
					analytics?.event()
						?.eventCategory("Authentication")
						?.eventAction("Successful register")
						?.fromCall(call)
						?.sendAsync()
					
					val user = registerUser(email, hashPassword(password))
					call.sessions.set(user.provideSession())
					redirect(redir ?: "/dashboard")
				} else {
					analytics?.event()
						?.eventCategory("Authentication")
						?.eventAction("Email already exists")
						?.fromCall(call)
						?.sendAsync()
					
					delay(1000)
					redirect("/login?type=err&msg=${"A user with this email already exists".encodeURLQueryComponent()}&redir=$redir")
				}
			}
			
			analytics?.event()
				?.eventCategory("Authentication")
				?.eventAction("Invalid signup format")
				?.fromCall(call)
				?.sendAsync()
			
			delay(750)
			redirect("/login?type=err&msg=${"Invalid email/password format".encodeURLQueryComponent()}&redir=$redir")
		}
		
		analytics?.event()
			?.eventCategory("Authentication")
			?.eventAction("Invalid signup parameters")
			?.fromCall(call)
			?.sendAsync()
		
		delay(200)
		redirect("/login?type=err&msg=${"Please fill in both email and password".encodeURLQueryComponent()}&redir=$redir")
	}
	
	get("/logout") {
		analytics?.event()
			?.eventCategory("Authentication")
			?.eventAction("Successful logout")
			?.fromCall(call)
			?.sendAsync()
		
		call.sessions.clear<UserSession>()
		redirect("/")
	}
}

private val saltRandom = SecureRandom()

fun hashPassword(password: String) = BCrypt.hashpw(password, BCrypt.gensalt(10, saltRandom))

data class UserSession(val userId: Int)
