@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package li.flurin.organiplus.layout

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import li.flurin.organiplus.NavNewTask
import li.flurin.organiplus.composable.tooltip
import li.flurin.organiplus.screen.DemoTaskScreen
import li.flurin.organiplus.screen.HomeScreen
import li.flurin.organiplus.screen.MoreScreen
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import organiplus.composeapp.generated.resources.Res
import organiplus.composeapp.generated.resources.add_24px
import organiplus.composeapp.generated.resources.assignment_24px
import organiplus.composeapp.generated.resources.assignment_filled_24px
import organiplus.composeapp.generated.resources.bucket_check_24px
import organiplus.composeapp.generated.resources.bucket_check_filled_24px
import organiplus.composeapp.generated.resources.home_24px
import organiplus.composeapp.generated.resources.home_filled_24px
import organiplus.composeapp.generated.resources.menu_24px
import organiplus.composeapp.generated.resources.more_horiz_24px


data class NavRoute(
    val title: String,
    val icon: DrawableResource,
    val iconSelected: DrawableResource,
    val content: @Composable () -> Unit
)

@Composable
fun AppLayout(onNavigate: (Any) -> Unit) {
    val routes = listOf(
        NavRoute("Home", Res.drawable.home_24px, Res.drawable.home_filled_24px) { HomeScreen() },
        NavRoute("Bucket", Res.drawable.bucket_check_24px,Res.drawable.bucket_check_filled_24px) { DemoTaskScreen() },
        NavRoute("Projects", Res.drawable.assignment_24px, Res.drawable.assignment_filled_24px) { DemoScreen("Stats") },
        NavRoute("More", Res.drawable.more_horiz_24px,Res.drawable.more_horiz_24px) { MoreScreen() }
    )

    var currentRoute by remember { mutableStateOf(routes[0]) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isMobile = maxWidth < 600.dp

        if (isMobile) { // MOBILE VIEW
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        routes.forEach { route ->
                            val selected = currentRoute == route
                            NavigationBarItem(
                                selected,
                                onClick = { currentRoute = route },
                                icon = { Icon(painter = painterResource(if(selected) route.iconSelected else route.icon), contentDescription = route.title) },
                                label = { Text(route.title, fontWeight = if(selected) FontWeight.Bold else FontWeight.Normal) }
                            )
                        }
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { onNavigate(NavNewTask) },
                        modifier = Modifier.tooltip("Add a new task", "a", ctrl = true)
                    ) {
                        Icon(painter = painterResource(Res.drawable.add_24px), contentDescription = "Add")
                    }
                }
            ) { innerPadding ->
                AnimatedContent(
                    targetState = currentRoute,
                    transitionSpec = {
                        val isMovingForward = routes.indexOf(targetState) > routes.indexOf(initialState)
                        val offset = if (isMovingForward) 10 else -10
                        (fadeIn() + slideInHorizontally { offset }).togetherWith(fadeOut() + slideOutHorizontally { -offset })
                    },
                    label = "Page Transition"
                ) {activeRoute ->
                    Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                        activeRoute.content()
                    }
                }
            }
        } else { // DESKTOP VIEW

            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    header = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {

                            IconButton(onClick = { /* TODO: Add Drawer */}) {
                                Icon(
                                    painter = painterResource(Res.drawable.menu_24px),
                                    contentDescription = "Toggle"
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            FloatingActionButton(
                                onClick = { onNavigate(NavNewTask) },
                                modifier = Modifier.tooltip("Add a new task", "a", ctrl = true),
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                elevation = FloatingActionButtonDefaults.elevation(0.dp)
                            ) {
                                Icon(painter = painterResource(Res.drawable.add_24px), contentDescription = "Add")
                            }
                        }
                    }
                ) {
                    Spacer(Modifier.height(16.dp))
                    routes.forEach { route ->
                        val selected = currentRoute == route
                        NavigationRailItem(
                            selected,
                            onClick = { currentRoute = route },
                            icon = { Icon(painter = painterResource(if(selected) route.iconSelected else route.icon), contentDescription = null) },
                            label = { Text(route.title, fontWeight = if(selected) FontWeight.Bold else FontWeight.Normal) },
                            alwaysShowLabel = true
                        )
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AnimatedContent(
                        targetState = currentRoute,
                        transitionSpec = {
                            val isMovingForward = routes.indexOf(targetState) > routes.indexOf(initialState)
                            val offset = if (isMovingForward) 10 else -10
                            (fadeIn() + slideInVertically { offset }).togetherWith(fadeOut() + slideOutVertically { -offset })
                        },
                        label = "Page Transition"
                    ) { activeRoute ->
                        Box(modifier = Modifier.fillMaxSize()) {
                            activeRoute.content()
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun DemoScreen(title: String) {
    Text(
        "Current Screen: $title"
    )
}

@Preview
@Composable
fun AppLayoutPreview() {
    AppLayout(onNavigate = {})
}