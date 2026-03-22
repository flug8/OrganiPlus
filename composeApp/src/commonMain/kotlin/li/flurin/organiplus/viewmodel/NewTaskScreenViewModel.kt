package li.flurin.organiplus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import li.flurin.organiplus.TaskDraftRepository
import li.flurin.organiplus.getPlatform
import li.flurin.organiplus.models.EnergyLevel
import li.flurin.organiplus.models.Priority
import java.time.LocalDate
import java.time.LocalTime

data class TaskDraft(
    val title: String = "",
    val description: String? = null,
    val type: TaskCreationType = TaskCreationType.TASK,
    val date: LocalDate? = null,
    val time: LocalTime? = null,
    val priority: Priority = Priority.NONE,
    val energyLevel: EnergyLevel = EnergyLevel.MEDIUM
)

class NewTaskScreenViewModel(repository: TaskDraftRepository) : ViewModel() {

    private val _draft = MutableStateFlow(repository.pendingDraft ?: TaskDraft())
    val draft = _draft.asStateFlow()

    private val _showPopup = MutableStateFlow(
        repository.pendingDraft == null && getPlatform().isMobile
    )
    val showPopup = _showPopup.asStateFlow()

    fun updateDraft(newDraft: TaskDraft) {
        _draft.value = newDraft
    }

    init {
        repository.pendingDraft = null
    }

    fun onExpandFromPopup(draftFromPopup: TaskDraft) {
        _draft.value = draftFromPopup
        _showPopup.value = false
    }

    fun onShowPopupChanged(isVisible: Boolean) {
        _showPopup.value = isVisible
    }

    fun saveTask(onComplete: () -> Unit) {
        viewModelScope.launch {
            // TODO: Save to database
            onComplete() //save to go back
        }
    }
}