package li.flurin.organiplus.database

import li.flurin.organiplus.TaskDatabase
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

object DatabaseManager {
    private val queries get() = DatabaseHolder.db.taskDatabaseQueries


    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
     fun insertNonRecurringTask(title: String, description: String? = null):String {
        val newId = Uuid.random().toString()
        val now = Clock.System.now().toEpochMilliseconds()
        queries.insertNonRecurringTask(
            id = newId,
            createdAt = now,
            updatedAt = now,
            title,
            description
        )
        return newId
    }
}