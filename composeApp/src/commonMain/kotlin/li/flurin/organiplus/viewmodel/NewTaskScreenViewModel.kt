package li.flurin.organiplus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import li.flurin.organiplus.getPlatform
import li.flurin.organiplus.models.EnergyLevel
import li.flurin.organiplus.models.Priority
import java.time.LocalDate
import java.time.LocalTime

data class TaskDraft(
    val title: String = "",
    val type: TaskCreationType = TaskCreationType.TASK,
    val date: LocalDate? = null,
    val time: LocalTime? = null,
    val priority: Priority = Priority.NONE,
    val energyLevel: EnergyLevel = EnergyLevel.MEDIUM
)

class NewTaskScreenViewModel : ViewModel() {

    private val _draft = MutableStateFlow(TaskDraft())
    val draft: StateFlow<TaskDraft> = _draft.asStateFlow()

    private val _showPopup = MutableStateFlow(true)
    val showPopup: StateFlow<Boolean> = _showPopup.asStateFlow()

    fun initScreen(initialDraft: TaskDraft?) {
        if (initialDraft != null) {
            _draft.value = initialDraft
            _showPopup.value = false
        } else if (getPlatform().isMobile) {
            _showPopup.value = true
        } else {
            _draft.value = TaskDraft()
            _showPopup.value = false
        }
    }

    fun updateDraft(newDraft: TaskDraft) {
        _draft.value = newDraft
    }

    fun onExpandFromPopup(draftFromPopup: TaskDraft) {
        _draft.value = draftFromPopup
        _showPopup.value = false
    }

    fun saveTask(onComplete: () -> Unit) {
        viewModelScope.launch {
            // TODO: Save to database
            onComplete() //save to go back
        }
    }
}