package li.flurin.organiplus.database

import li.flurin.organiplus.models.EnergyLevel
import li.flurin.organiplus.models.Priority
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

object DatabaseManager {
    private val queries get() = DatabaseHolder.db.taskDatabaseQueries


    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    fun insertNonRecurringTaskMinimal(title: String, description: String? = null):String {
        val newId = Uuid.random().toString()
        val now = Clock.System.now()
        queries.transaction {
            queries.insertNonRecurringTaskMinimal(
                id = newId,
                createdAt = now,
                updatedAt = now,
                title,
                description
            )
        }
        return newId
    }
    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    fun insertNonRecurringTask(
        title: String,
        description: String? = null,
        duration: Duration = 15.minutes,
        energyLevel: EnergyLevel = EnergyLevel.LOW,
        priority: Priority = Priority.NONE,
        dueDate: Instant? = null,
        scheduledStart: Instant? = null,
        isAutoScheduled: Boolean = false
    ):String {
        val newId = Uuid.random().toString()
        val now = Clock.System.now()
        queries.transaction {
            queries.insertNonRecurringTask(
                id = newId,
                createdAt = now,
                updatedAt = now,
                title,
                description,
                duration,
                energyLevel,
                priority,
                dueDate,
                scheduledStart,
                isAutoScheduled
            )
        }
        return newId
    }
}