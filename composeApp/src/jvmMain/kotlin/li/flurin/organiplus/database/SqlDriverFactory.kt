package li.flurin.organiplus.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import li.flurin.organiplus.JVMPlatform
import li.flurin.organiplus.OSType
import li.flurin.organiplus.TaskDatabase
import java.io.File

actual class SqlDriverFactory {
    actual fun createDriver(): SqlDriver {
        val userHome = System.getProperty("user.home")

        val appDir = when (JVMPlatform().osType)  {
            OSType.WINDOWS -> {
                val appData = System.getenv("APPDATA") ?: "$userHome\\AppData\\Roaming"
                File(appData, "OrganiPlus")
            }
            OSType.MACOS -> {
                File(userHome, "Library/Application Support/OrganiPlus")
            }
            OSType.LINUX, OSType.UNKNOWN -> {
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