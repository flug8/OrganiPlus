package li.flurin.organiplus.models

enum class TaskStatus(val value: Long) {
    UNFINISHED(0L),
    COMPLETED(1L),
    CANCELED(2L)
}

enum class EnergyLevel(val value: Long) {
    LOW(1L),
    MEDIUM(2L),
    HIGH(3L)
}

enum class Priority(val value: Long) {
    NONE(1L),
    LOW(2L),
    NORMAL(3L),
    HIGH(4L),
    URGENT(5L)
}