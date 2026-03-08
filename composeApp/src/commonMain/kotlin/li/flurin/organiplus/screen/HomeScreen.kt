package li.flurin.organiplus.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import li.flurin.organiplus.database.DatabaseHolder
import li.flurin.organiplus.database.DatabaseManager

import org.jetbrains.compose.resources.Font
import organiplus.composeapp.generated.resources.Res
import organiplus.composeapp.generated.resources.muesomoderno_italic_variable
import kotlin.collections.emptyList


@Composable
fun HomeScreen() {
    TextLogo()

    // ONLY TO TEST, REMOVE AGAIN AFTERWARDS TODO REMOVE LATER
    val queries = DatabaseHolder.db.taskDatabaseQueries
    val tasks by queries.getAllTasks()
        .asFlow()
        .mapToList(Dispatchers.Default)
        .collectAsState(initial = emptyList())

    var text by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row {
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                label = { Text("New Task") }
            )
            Button(
                modifier = Modifier.padding(start = 8.dp),
                onClick = {
                    if (text.isNotBlank()) {
                        queries.insertTask(title = text, isCompleted = 0L)
                        text = DatabaseManager.insertNonRecurringTask(text)
                    }
                }
            ) {
                Text("Add")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(tasks) { task ->
                Text("• ${task.title}", modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}


@Composable
fun TextLogo(color: Color = MaterialTheme.colorScheme.onSurface) {
    val customFontFamily = FontFamily(
        Font(
            resource = Res.font.muesomoderno_italic_variable,
            weight = FontWeight.Bold,
            variationSettings = FontVariation.Settings(
                FontVariation.weight(800)
            )
        )
    )

    Text(
        text = "OrganiPlus",
        fontFamily = customFontFamily,
        color = color
    )
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}