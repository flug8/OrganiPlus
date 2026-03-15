package li.flurin.organiplus.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import li.flurin.organiplus.database.DatabaseManager
import li.flurin.organiplus.models.EnergyLevel
import li.flurin.organiplus.models.Priority
import kotlin.reflect.KClass


enum class PopupStep {
    TYPE_SELECTION,
    DATETIME_SELECTION,
    PRIORITY_SELECTION,
    ENERGYLEVEL_SELECTION,
    TAG_SELECTION,
    READY_TO_SEND
}

enum class TaskCreationType {
    TASK,
    HABIT,
    DROP
}

class NewTaskViewModel : ViewModel() {
    var title by mutableStateOf("")
    var description by mutableStateOf("")

    var taskType by mutableStateOf(TaskCreationType.TASK)
    var isSmartSchedule by mutableStateOf(false)
    var isDrop by mutableStateOf(false)
    var priorityState by mutableStateOf(Priority.NONE)
    var energyState by mutableStateOf(EnergyLevel.LOW)

    var currentStep by mutableStateOf(PopupStep.TYPE_SELECTION)
        private set

    var lastStepOrdinal by mutableIntStateOf(0) // to know animation direction
        private set


    fun selectTaskType(type: TaskCreationType) {
        this.taskType = type
        this.isDrop = (type == TaskCreationType.DROP)
        advanceToNextStep()
    }

    fun advanceToNextStep() {
        lastStepOrdinal = currentStep.ordinal
        currentStep = determineNextStep(currentStep)
    }

    fun goBackAStep() {
        // TODO: idk if needed
    }

    private fun determineNextStep(current: PopupStep): PopupStep {
        return when (current) {
            PopupStep.TYPE_SELECTION -> {
                if (isDrop) {
                    PopupStep.PRIORITY_SELECTION
                } else {
                    PopupStep.DATETIME_SELECTION
                }
            }
            PopupStep.DATETIME_SELECTION -> PopupStep.PRIORITY_SELECTION
            PopupStep.PRIORITY_SELECTION -> PopupStep.ENERGYLEVEL_SELECTION
            PopupStep.ENERGYLEVEL_SELECTION -> PopupStep.TAG_SELECTION
            PopupStep.TAG_SELECTION -> PopupStep.READY_TO_SEND
            PopupStep.READY_TO_SEND -> PopupStep.READY_TO_SEND
        }
    }











    private val _navigationEvent = MutableSharedFlow<NavTarget>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    sealed class NavTarget {
        object Back : NavTarget()
        object NewTask : NavTarget()
    }

    fun saveAndExit() {
        executeSave { _navigationEvent.emit(NavTarget.Back) }
    }

    fun saveAndNext() {
        viewModelScope.launch {
            val id = withContext(Dispatchers.IO) {
                DatabaseManager.insertNonRecurringTaskMinimal(title, description)
            }

            if (id.isNotEmpty()) {
                _navigationEvent.emit(NavTarget.NewTask)
            }
        }
    }

    private fun executeSave(onSuccess: suspend () -> Unit) {
        if (title.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            DatabaseManager.insertNonRecurringTaskMinimal(title, description)

            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
                return NewTaskViewModel() as T
            }
        }
    }
}