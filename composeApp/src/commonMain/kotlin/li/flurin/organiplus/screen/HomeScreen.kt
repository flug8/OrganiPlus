package li.flurin.organiplus.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import li.flurin.organiplus.Tasks
import li.flurin.organiplus.database.DatabaseHolder
import li.flurin.organiplus.ui.theme.getGoogleSansFlexFont
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import organiplus.composeapp.generated.resources.Res
import organiplus.composeapp.generated.resources.event_available_filled_24px
import organiplus.composeapp.generated.resources.home_filled_24px
import organiplus.composeapp.generated.resources.pacifico_regular
import organiplus.composeapp.generated.resources.water_drop_filled_24px

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen() {
    val pacificoFontFamily = FontFamily(Font(Res.font.pacifico_regular))
    val motivationalSentences = listOf(
        "Small steps, big impact.",
        "Focus on the next right thing.",
        "You've got this.",
        "Time to make magic happen.",
        "Good morning!\nLet’s be awesome",
        "Ready to crush the day?",
        "Hello!\nWhat’s the plan today?",
        "A fresh start begins now",
        "Make today absolutely count",
        "One task at a time",
        "Dreams don’t work unless you do",
        "Small steps lead to big wins",
        "Stop wishing, start doing",
        "Your future self will thank you",
        "Coffee first,\nthen adulting",
        "Conquer the list,\nearn the nap",
        "Less scrolling,\nmore doing",
        "Become a productivity wizard",
        "Do it for the checkmark",
        "You’ve got this, superstar!",
        "Shine bright and get busy",
        "Focus on the good stuff",
        "Believe you can.\nDo it.",
        "Today is full of possibilities."
    )

    val dailySentence = remember { motivationalSentences.random() }

    val queries = DatabaseHolder.db.taskDatabaseQueries
    val tasks by remember {
        queries.getAllTasks()
            .asFlow()
            .mapToList(Dispatchers.Default)
    }.collectAsState(initial = emptyList())


    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 32.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = dailySentence,
                    fontFamily = pacificoFontFamily,
                    style = MaterialTheme.typography.displaySmallEmphasized,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 24.dp).fillMaxWidth(),
                    lineHeight = 60.sp,
                    textAlign = TextAlign.Center
                )
            }
            item {
                HomeScreenStatsRow(
                    completedTasks = 2,
                    totalTasks = 10,
                    dropsInBucket = 1
                )
            }
            item {
                Text(
                    text = "Your Tasks",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            if (tasks.isEmpty()) {
                item {
                    EmptyStateMessage()
                }
            } else {
                items(tasks) { task ->
                    TaskItemCard(task = task)
                }
            }
        }
    }
}




@Composable
fun TaskItemCard(task: Tasks) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.home_filled_24px),
                contentDescription = "Task Icon",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(28.dp)
            )

            Column {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun EmptyStateMessage() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "All caught up! Time to relax.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}






@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreenStatsRow(
    completedTasks: Int,
    totalTasks: Int,
    dropsInBucket: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Res.drawable.event_available_filled_24px,
            value = "$completedTasks/$totalTasks",
            label = "Tasks completed",
            iconShape = MaterialShapes.Pill.toShape()
        )

        StatCard(
            modifier = Modifier.weight(1f),
            icon = Res.drawable.water_drop_filled_24px,
            value = dropsInBucket.toString(),
            label = "Drops in bucket",
            iconShape = MaterialShapes.Cookie7Sided.toShape(),
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icon: DrawableResource,
    value: String,
    label: String,
    iconShape: Shape = CircleShape,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(iconShape)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
            }


            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = getGoogleSansFlexFont(
                    roundness = 100f,
                    weight = 800f,
                    slant = -10f,
                    width = 110f
                ),
                color = contentColor,
                fontWeight = FontWeight.Bold
            )


            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
            )
        }
    }
}





@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}