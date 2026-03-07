package li.flurin.organiplus.layout

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp


data class NavRoute(val title: String, val icon: ImageVector)

@Composable
fun AppLayout() {
    val routes = listOf(
        NavRoute("Home", Icons.Default.Home),
        NavRoute("Bucket", Icons.Default.ExpandCircleDown),
        NavRoute("Stats", Icons.Default.Leaderboard),
        NavRoute("Settings", Icons.Default.Settings)
    )

    var currentRoute by remember { mutableStateOf(routes[0]) }
    var isSidePanelExpanded by remember { mutableStateOf(false) }

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
                        Text(
                            "Current Screen: ${activeRoute.title}",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        } else { // DESKTOP VIEW
            val panelWidth by animateDpAsState(
                targetValue = if (isSidePanelExpanded) 200.dp else 80.dp,
                label = "panelWidth"
            )

            Row(modifier = Modifier.fillMaxSize()) {
                Surface(
                    modifier = Modifier.width(panelWidth).fillMaxHeight(),
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(
                            onClick = { isSidePanelExpanded = !isSidePanelExpanded },
                            modifier = Modifier.align(Alignment.Start)
                        ) {
                            Icon(
                                imageVector = if (isSidePanelExpanded) Icons.Default.MenuOpen else Icons.Default.Menu,
                                contentDescription = "Toggle Menu"
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        ExtendedFloatingActionButton(
                            onClick = { /* TODO: Add Action */ },
                            expanded = isSidePanelExpanded,
                            icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                            text = { Text(text = "Create", maxLines = 1, softWrap = false) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        routes.forEach { route ->
                            SideNavigationItem(
                                route = route,
                                isSelected = currentRoute == route,
                                isExpanded = isSidePanelExpanded,
                                onClick = { currentRoute = route }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                AnimatedContent(
                    targetState = currentRoute,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    transitionSpec = {
                        val isMovingForward = routes.indexOf(targetState) > routes.indexOf(initialState)
                        val offset = if (isMovingForward) 10 else -10
                        (fadeIn() + slideInVertically { offset }).togetherWith(fadeOut() + slideOutVertically { -offset })
                    },
                    label = "Page Transition"
                ) { activeRoute ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        Text(
                            "Current Screen: ${activeRoute.title}",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun SideNavigationItem(
    route: NavRoute,
    isSelected: Boolean,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(CircleShape)
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp)
    ) {
        Icon(
            imageVector = route.icon,
            contentDescription = route.title,
            tint = contentColor
        )
        AnimatedVisibility(visible = isExpanded) {
            Text(
                text = route.title,
                color = contentColor,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(start = 16.dp),
                maxLines = 1
            )
        }
    }
}