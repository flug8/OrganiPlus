package li.flurin.organiplus


import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import li.flurin.organiplus.layout.AppLayout
import li.flurin.organiplus.screen.NewTaskScreen
import li.flurin.organiplus.ui.theme.AppTheme
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable object NavLayout
@Serializable object NavNewTask
@Serializable object NavSettings
@Serializable data class TaskDetails @OptIn(ExperimentalUuidApi::class) constructor(val uuid: Uuid)

@Composable
@Preview
fun App() {
    AppTheme {
        val navController = rememberNavController()

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
                        animationSpec = tween(durationMillis = transitionDuration / 2, delayMillis = transitionDuration / 2)
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
                        animationSpec = tween(durationMillis = transitionDuration / 2, delayMillis = transitionDuration / 2)
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
                    NewTaskScreen { navController.popBackStack() }
                }

                /*composable<TaskDetails> { backStackEntry ->
                    val details = backStackEntry.toRoute<TaskDetails>()
                    ProjectDetailsScreen(id = details.projectId, onBack = { navController.popBackStack() })
                }*/
            }
        }
    }
}


