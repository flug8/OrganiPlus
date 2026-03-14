package li.flurin.organiplus.database

import li.flurin.organiplus.TaskDatabase
import li.flurin.organiplus.Tasks

object DatabaseHolder {
    private var instance: TaskDatabase? = null

    fun init(driverFactory: SqlDriverFactory) {
        if (instance == null) {
            instance = TaskDatabase(
                driverFactory.createDriver(),
                tasksAdapter = Tasks.Adapter(
                    created_atAdapter = instantAdapter,
                    updated_atAdapter = instantAdapter,
                    deleted_atAdapter = instantAdapter,
                    statusAdapter = statusAdapter,
                    completed_atAdapter = instantAdapter,
                    durationAdapter = durationInMinutesAdapter,
                    energy_levelAdapter = energyLevelAdapter,
                    priorityAdapter = priorityAdapter,
                    due_dateAdapter = instantAdapter,
                    scheduled_startAdapter = instantAdapter,
                    original_start_dateAdapter = instantAdapter
                )
            )
        }
    }

    val db: TaskDatabase
        get() = instance ?: error("Database not initialized!")
}