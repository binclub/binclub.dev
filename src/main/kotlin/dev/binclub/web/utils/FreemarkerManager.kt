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

import freemarker.cache.FileTemplateLoader
import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateMethodModelEx
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.ContentType
import java.io.File
import java.io.StringWriter
import java.text.SimpleDateFormat

/**
 * @author cookiedragon234 01/Apr/2020
 */
val configuration = Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS).apply {
	templateLoader = FileTemplateLoader(File("html/templates"))
}

/**
 * Directly compile a free marker template to a string
 */
fun FreeMarkerContent.compileAot(): String = processFreemarkerAOT(this)

fun processFreemarkerAOT(freeMarkerContent: FreeMarkerContent): String {
	return createOutgoing(freeMarkerContent).let {
		StringWriter().also { pr ->
			it.template.process(it.model, pr)
		}.toString()
	}
}

private fun createOutgoing(content: FreeMarkerContent): FreeMarkerOutgoingContent {
	return FreeMarkerOutgoingContent(
		configuration.getTemplate(content.template),
		content.model,
	)
}

private class FreeMarkerOutgoingContent(
	val template: Template,
	val model: Any?,
)

/**
 * Method that can be invoked within a freemarker template
 * Formats a date time stamp
 */
object FreeMarkerDateFormatterMethod : TemplateMethodModelEx {
	// Maybe use a better format than just the default?
	override fun exec(arguments: MutableList<Any?>): String = arguments.first().toString()
}

/**
 * Formats a block using some pseudo markdown stuff
 */
object FreemarkerFormattingTransformerMethod: TemplateMethodModelEx {
	override fun exec(arguments: MutableList<Any?>): String {
		val str = arguments.first().toString()
		return buildString (str.length) {
			var codeIndex = 0
			for (char in str) {
				when (char) {
					'\n' -> append("<br>")
					'`' -> {
						append(if (codeIndex == 0) "<code>" else "</code>")
						codeIndex = (codeIndex + 1) % 2
					}
					else -> append(char)
				}
			}
		}
	}
}
