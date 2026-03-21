package li.flurin.organiplus.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import li.flurin.organiplus.getPlatform
import li.flurin.organiplus.viewmodel.NewTaskScreenViewModel
import li.flurin.organiplus.viewmodel.TaskDraft

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskScreen(
    viewModel: NewTaskScreenViewModel = viewModel { NewTaskScreenViewModel() },
    taskDraft: TaskDraft? = null,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(taskDraft) {
        viewModel.initScreen(taskDraft)
    }
    val currentDraft by viewModel.draft.collectAsState()
    val showPopup by viewModel.showPopup.collectAsState()


    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold(
            topBar = {
                LargeTopAppBar(
                    title = { Text(getPlatform().isMobile.toString()) },
                    actions = {
                        FilledTonalButton(
                            onClick = {
                                viewModel.saveTask(onComplete = onNavigateBack)
                            }
                        ) {
                            Text("Save Task")
                        }
                    }
                )
            }
        ) { paddingValues -> /*
            NewTaskScreenContent(
                draft = currentDraft,
                onDraftChange = { viewModel.updateDraft(it) }, // Send updates to VM
                windowWidthSizeClass = windowWidthSizeClass,
                modifier = Modifier.padding(paddingValues).fillMaxSize()
            )*/
        }

        if (showPopup) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))

            AddTaskPopup(
                onDismiss = { onNavigateBack() },
                onDirectSave = { finalDraft ->
                    viewModel.updateDraft(finalDraft)
                    viewModel.saveTask(onComplete = onNavigateBack)
                },
                onExpandToFullEdit = { draftFromPopup ->
                    viewModel.onExpandFromPopup(draftFromPopup)
                }
            )
        }
    }
}