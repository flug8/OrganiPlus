package li.flurin.organiplus.database

import li.flurin.organiplus.TaskDatabase
object DatabaseHolder {
    private var instance: TaskDatabase? = null

    fun init(driverFactory: SqlDriverFactory) {
        if (instance == null) {
            instance = TaskDatabase(driverFactory.createDriver())
        }
    }

    val db: TaskDatabase
        get() = instance ?: error("Database not initialized!")
}