package li.flurin.organiplus.database

import app.cash.sqldelight.db.SqlDriver

expect class SqlDriverFactory {
    fun createDriver(): SqlDriver
}