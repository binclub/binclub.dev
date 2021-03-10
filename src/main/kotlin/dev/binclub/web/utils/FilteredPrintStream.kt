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

import java.io.PrintStream

/**
 * @author cookiedragon234 16/Mar/2020
 */
class FilteredPrintStream(
	val printStream: PrintStream,
	val filters: Array<String>
) : PrintStream(printStream) {
	private fun isFiltered(s: String?): Boolean {
		if (s == null) return false
		
		for (filter in filters) {
			if (s.contains(filter)) return true
		}
		return false
	}
	
	override fun print(s: String?) {
		if (!isFiltered(s)) {
			super.print(s)
		}
	}
	
	override fun println(x: String?) {
		if (!isFiltered(x)) {
			super.println(x)
		}
	}
}
