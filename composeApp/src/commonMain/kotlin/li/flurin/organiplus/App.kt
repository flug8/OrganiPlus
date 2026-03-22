package li.flurin.organiplus


import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import li.flurin.organiplus.composable.AppTooltipProvider
import li.flurin.organiplus.layout.AppLayout
import li.flurin.organiplus.screen.NewTaskScreen
import li.flurin.organiplus.ui.theme.AppTheme
import li.flurin.organiplus.viewmodel.TaskDraft
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


expect fun kmpLog(tag: String, message: String, isError: Boolean = false)

@Serializable object NavLayout
@Serializable object NavNewTask
@Serializable object NavSettings
@Serializable data class TaskDetails @OptIn(ExperimentalUuidApi::class) constructor(val uuid: Uuid)

object Graph {
    val taskDraftRepository = TaskDraftRepository()
}

class TaskDraftRepository {
    var pendingDraft: TaskDraft? = null
}

@Composable
@Preview
fun App(
    navigationSignal: MutableStateFlow<String?> = MutableStateFlow(null)
) {
    AppTheme {
        AppTooltipProvider {
            val navController = rememberNavController()
            val signal by navigationSignal.collectAsState()

            LaunchedEffect(signal) {
                if (signal == "NAV_NEW_TASK") {
                    navController.navigate(NavNewTask)
                    navigationSignal.value = null
                }
            }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                val transitionDuration = 500
                NavHost(
                    navController,
                    startDestination = NavLayout,
                    enterTransition = {
                        slideInHorizontally(
                            animationSpec = tween(transitionDuration),
                            initialOffsetX = { fullWidth -> (fullWidth * 0.15f).toInt() }
                        ) + fadeIn(
                            animationSpec = tween(
                                durationMillis = transitionDuration / 2,
                                delayMillis = transitionDuration / 2
                            )
                        )
                    },
                    exitTransition = {
                        slideOutHorizontally(
                            animationSpec = tween(transitionDuration),
                            targetOffsetX = { fullWidth -> -(fullWidth * 0.15f).toInt() }
                        ) + fadeOut(
                            animationSpec = tween(durationMillis = transitionDuration / 2)
                        )
                    },
                    popEnterTransition = {
                        slideInHorizontally(
                            animationSpec = tween(transitionDuration),
                            initialOffsetX = { fullWidth -> -(fullWidth * 0.15f).toInt() }
                        ) + fadeIn(
                            animationSpec = tween(
                                durationMillis = transitionDuration / 2,
                                delayMillis = transitionDuration / 2
                            )
                        )
                    },
                    popExitTransition = {
                        slideOutHorizontally(
                            animationSpec = tween(transitionDuration),
                            targetOffsetX = { fullWidth -> (fullWidth * 0.15f).toInt() }
                        ) + fadeOut(
                            animationSpec = tween(durationMillis = transitionDuration / 2)
                        )
                    }
                ) {
                    composable<NavLayout> {
                        AppLayout(
                            onNavigate = { route ->
                                navController.navigate(route)
                            }
                        )
                    }
                    composable<NavNewTask> {
                        NewTaskScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    /*composable<TaskDetails> { backStackEntry ->
                        val details = backStackEntry.toRoute<TaskDetails>()
                        ProjectDetailsScreen(id = details.projectId, onBack = { navController.popBackStack() })
                    }*/
                }
            }
        }
    }
}


