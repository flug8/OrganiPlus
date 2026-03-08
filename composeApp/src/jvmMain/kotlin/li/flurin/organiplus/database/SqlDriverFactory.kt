package li.flurin.organiplus.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import li.flurin.organiplus.TaskDatabase
import java.io.File

actual class SqlDriverFactory {
    actual fun createDriver(): SqlDriver {
        val dbFile = File("tasks.db")
        val driver = JdbcSqliteDriver("jdbc:sqlite:tasks.db")

        if (!dbFile.exists()){
            TaskDatabase.Schema.create(driver)
        }
        
        return driver
    }
}