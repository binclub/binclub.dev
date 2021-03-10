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

import io.ktor.application.call
import io.ktor.features.StatusPages
import io.ktor.response.respondRedirect

/**
 * @author cookiedragon234 02/Mar/2020
 */

/**
 * This exception can be thrown by a path handler, then we will catch it and implant a redirection header
 */
class RedirectException(val path: String, val permanent: Boolean): Exception()

/**
 * Utility function to throw the exception
 */
fun redirect(path: String, permanent: Boolean = false): Nothing = throw RedirectException(path, permanent)

/**
 * Registers our exception handler which will then add a Location: header
 */
fun StatusPages.Configuration.registerRedirections() {
	exception<RedirectException> { cause ->
		call.respondRedirect(cause.path, cause.permanent)
	}
}
