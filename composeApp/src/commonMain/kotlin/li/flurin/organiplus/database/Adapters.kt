package li.flurin.organiplus.database

import app.cash.sqldelight.ColumnAdapter
import li.flurin.organiplus.models.EnergyLevel
import li.flurin.organiplus.models.Priority
import li.flurin.organiplus.models.TaskStatus
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

val instantAdapter = object : ColumnAdapter<Instant, Long> {
    override fun decode(databaseValue: Long): Instant = Instant.fromEpochMilliseconds(databaseValue)
    override fun encode(value: Instant): Long = value.toEpochMilliseconds()
}

val durationInMinutesAdapter = object : ColumnAdapter<Duration, Long> {
    override fun decode(databaseValue: Long): Duration = databaseValue.minutes
    override fun encode(value: Duration): Long = value.inWholeMinutes
}

val statusAdapter = object : ColumnAdapter<TaskStatus, Long> {
    override fun decode(databaseValue: Long): TaskStatus = TaskStatus.entries.first { it.value == databaseValue }
    override fun encode(value: TaskStatus): Long = value.value
}

val energyLevelAdapter = object : ColumnAdapter<EnergyLevel, Long> {
    override fun decode(databaseValue: Long): EnergyLevel = EnergyLevel.entries.first { it.value == databaseValue }
    override fun encode(value: EnergyLevel): Long = value.value
}

val priorityAdapter = object : ColumnAdapter<Priority, Long> {
    override fun decode(databaseValue: Long): Priority = Priority.entries.first { it.value == databaseValue }
    override fun encode(value: Priority): Long = value.value
}