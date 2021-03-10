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

import dev.binclub.web.utils.redirect
import io.ktor.routing.Route
import io.ktor.routing.get


/**
 * Redirect to discord
 * We use this /discord endpoint that redirects to the actual link because directly linking to the server
 * would result in caching of the link which may become invalid, as well as this providing analytics
 * (Because we only do server side analytics so we cannot get analytics for a client side button click to an external
 * server)
 *
 * @author cookiedragon234 22/Jun/2020
 */
private const val link = "https://discord.gg/fUkXhEu"

fun Route.discordEndpoint() {
	get ("discord") {
		redirect(link, permanent = false)
	}
}
