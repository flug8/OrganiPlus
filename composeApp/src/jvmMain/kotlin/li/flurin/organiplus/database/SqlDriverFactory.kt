package li.flurin.organiplus.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import li.flurin.organiplus.TaskDatabase
import java.io.File

actual class SqlDriverFactory {
    actual fun createDriver(): SqlDriver {
        val os = System.getProperty("os.name").lowercase()
        val userHome = System.getProperty("user.home")

        val appDir = when  {
            os.contains("win") -> {
                val appData = System.getenv("APPDATA") ?: "$userHome\\AppData\\Roaming"
                File(appData, "OrganiPlus")
            }
            os.contains("mac") -> {
                File(userHome, "Library/Application Support/OrganiPlus")
            }
            else -> { //Linux
                File(userHome, ".local/share/OrganiPlus")
            }
        }
        if (!appDir.exists()) {
            appDir.mkdirs()
        }

        val dbFile = File(appDir, "tasks.db")
        val isNewDatabase = !dbFile.exists() || dbFile.length() == 0L

        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")

        if (isNewDatabase){
            TaskDatabase.Schema.create(driver)
        }
        
        return driver
    }
}