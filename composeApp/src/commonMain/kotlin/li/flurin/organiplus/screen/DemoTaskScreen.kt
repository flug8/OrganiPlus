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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import li.flurin.organiplus.database.DatabaseHolder
import li.flurin.organiplus.database.DatabaseManager

@Composable
fun DemoTaskScreen() { // TODO: Remove later
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
                        text = DatabaseManager.insertNonRecurringTaskMinimal(text)
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