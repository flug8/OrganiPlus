package li.flurin.organiplus.viewmodel

import androidx.compose.runtime.getValue
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

class NewTaskViewModel : ViewModel() {
    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var isSmartSchedule by mutableStateOf(false)
    var isDrop by mutableStateOf(false)
    var priorityState by mutableStateOf(Priority.NONE)
    var energyState by mutableStateOf(EnergyLevel.LOW)

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