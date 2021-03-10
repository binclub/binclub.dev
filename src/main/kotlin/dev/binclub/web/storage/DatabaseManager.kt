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

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.binclub.web.Secrets
import dev.binclub.web.utils.blockingDbQuery
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import java.io.Closeable
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author cookiedragon234 01/Mar/2020
 */
object DatabaseManager : Closeable {
	private lateinit var db: Database
	private val initialized = AtomicBoolean(false)
	
	fun init() {
		// make sure we only initialize once
		if (initialized.compareAndSet(false, true)) {
			val config = HikariConfig("/hikari.properties")
			config.jdbcUrl = Secrets.DB_URL!!
			config.username = Secrets.DB_USER!!
			config.password = Secrets.DB_PASS!!
			val ds = HikariDataSource(config)
			db = Database.connect(ds, setupConnection = {})
			
			blockingDbQuery {
				create(DownloadsTable)
				create(LicensesTable)
				create(ProductsTable)
				create(UsersTable)
				create(OrderTable)
				create(OrdersTable)
			}
		}
	}
	
	override fun close() {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}
