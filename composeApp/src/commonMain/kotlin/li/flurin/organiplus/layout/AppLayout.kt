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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import li.flurin.organiplus.screen.HomeScreen


data class NavRoute(val title: String, val icon: ImageVector, val content: @Composable () -> Unit)

@Composable
fun AppLayout() {
    val routes = listOf(
        NavRoute("Home", Icons.Default.Home) { HomeScreen() },
        NavRoute("Bucket", Icons.Default.ExpandCircleDown) { DemoScreen("Bucket") },
        NavRoute("Stats", Icons.Default.Leaderboard) { DemoScreen("Stats") },
        NavRoute("Settings", Icons.Default.Settings) { DemoScreen("Settings") }
    )

    var currentRoute by remember { mutableStateOf(routes[0]) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isMobile = maxWidth < 600.dp

        if (isMobile) { // MOBILE VIEW
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        routes.forEach { route ->
                            NavigationBarItem(
                                selected = currentRoute == route,
                                onClick = { currentRoute = route },
                                icon = { Icon(route.icon, contentDescription = route.title) },
                                label = { Text(route.title) }
                            )
                        }
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(onClick = { /* TODO: Add Action */ }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
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
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Toggle"
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            FloatingActionButton(
                                onClick = { /* TODO: Add Create Action */ },
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                elevation = FloatingActionButtonDefaults.elevation(0.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add")
                            }
                        }
                    }
                ) {
                    Spacer(Modifier.height(16.dp))
                    routes.forEach { route ->
                        NavigationRailItem(
                            selected = currentRoute == route,
                            onClick = { currentRoute = route },
                            icon = { Icon(route.icon, contentDescription = null) },
                            label = { Text(route.title) },
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
        "Current Screen: ${title}"
    )
}