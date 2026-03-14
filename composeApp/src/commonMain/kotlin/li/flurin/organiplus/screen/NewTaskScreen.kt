package li.flurin.organiplus.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import organiplus.composeapp.generated.resources.Res


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import organiplus.composeapp.generated.resources.home_filled_24px
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import org.jetbrains.compose.resources.DrawableResource
import organiplus.composeapp.generated.resources.add_big_24px
import organiplus.composeapp.generated.resources.bolt_filled_24px
import organiplus.composeapp.generated.resources.bucket_check_24px
import organiplus.composeapp.generated.resources.bucket_check_filled_24px
import organiplus.composeapp.generated.resources.flag_24px
import organiplus.composeapp.generated.resources.flag_filled_24px
import organiplus.composeapp.generated.resources.save_filled_24px
import organiplus.composeapp.generated.resources.wand_shine_24px
import organiplus.composeapp.generated.resources.wand_shine_filled_24px


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NewTaskScreen(
    onBack: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }

    val titleFocusRequester = remember { FocusRequester() }
    val descriptionFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isDrop by remember { mutableStateOf(false) }
    var isSmartSchedule by remember { mutableStateOf(false) }
    var priorityState by remember { mutableIntStateOf(1) }
    var energyState by remember { mutableIntStateOf(1) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100)
        titleFocusRequester.requestFocus()
        keyboardController?.show()
    }

    Scaffold(
        floatingActionButtonPosition = FabPosition.Center,
        contentWindowInsets = WindowInsets.safeDrawing,
        floatingActionButton = {
            HorizontalFloatingToolbar(
                expanded = isExpanded,
                floatingActionButton = {
                    FloatingToolbarDefaults.StandardFloatingActionButton(
                        onClick = { /* TODO: Save and Close */ }
                    ) {
                        Icon(
                            painterResource(Res.drawable.save_filled_24px),
                            contentDescription = "Save and Close"
                        )
                    }
                },
                content = {
                    ToolbarItemToggle(
                        condition = isSmartSchedule,
                        icon = Res.drawable.wand_shine_24px,
                        iconSelected = Res.drawable.wand_shine_filled_24px
                    ) {
                        isSmartSchedule = !isSmartSchedule
                        isDrop = false
                    }
                    ToolbarItemToggle(
                        condition = isDrop,
                        icon = Res.drawable.bucket_check_24px,
                        iconSelected = Res.drawable.bucket_check_filled_24px
                    ) {
                        isDrop = !isDrop
                        isSmartSchedule = false
                    }
                    ToolbarItemSelect(
                        list = listOf(
                            SelectItem(5,"Urgent", Res.drawable.flag_filled_24px, Color(0xFFD72638)),
                            SelectItem(4,"High", Res.drawable.flag_filled_24px, Color(0xFFF4900C)),
                            SelectItem(3,"Medium", Res.drawable.flag_filled_24px, Color(0xFF3F88C5)),
                            SelectItem(2,"Low", Res.drawable.flag_filled_24px, Color(0xFF429E46)),
                            SelectItem(1,"None", Res.drawable.flag_24px, LocalContentColor.current),
                        ),
                        state = priorityState
                    ) { state ->
                        priorityState = state
                    }
                    ToolbarItemSelect(
                            list = listOf(
                                SelectItem(3,"High", Res.drawable.bolt_filled_24px, Color(0xFFD72638)),
                                SelectItem(2,"Medium", Res.drawable.bolt_filled_24px, Color(0xFFF4900C)),
                                SelectItem(1,"Low", Res.drawable.bolt_filled_24px, Color(0xFF429E46)),
                            ),
                    state = energyState
                    ) { state ->
                    energyState = state
                }
                    VerticalDivider(
                        modifier = Modifier
                            .height(24.dp)
                            .padding(horizontal = 4.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                    ToolbarItemToggle(
                        condition = false,
                        icon = Res.drawable.add_big_24px,
                        iconSelected = Res.drawable.add_big_24px
                    ) {
                        // TODO: Save and New Task
                    }
                },
                colors = FloatingToolbarDefaults.standardFloatingToolbarColors(
                    toolbarContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            )
        }
    ) { paddingValues ->

        // TODO Edit, only temporary placeholder, idk if this design will be final

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = {
                        Text(
                            "Task Name",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        )
                    },
                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(titleFocusRequester),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { descriptionFocusRequester.requestFocus()}
                    )
                )

                TextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = {
                        Text(
                            "Add details, notes, or links...",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyLarge,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(descriptionFocusRequester),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default)
                )
            }

            AnimatedVisibility(
                visible = !isDrop,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    NewTaskCard {
                        NewTaskCardItem(
                            headline = "Scheduled Start",
                            supportingText = "When to do this",
                            icon = painterResource(Res.drawable.home_filled_24px),
                            trailingText = "Tomorrow, 10:00 AM"
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        NewTaskCardItem(
                            headline = "Repeat",
                            icon = painterResource(Res.drawable.home_filled_24px),
                            trailingText = "Weekly"
                        )

                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Reminders",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = true,
                                    onClick = { },
                                    label = { Text("At start time") },
                                    leadingIcon = {
                                        Icon(
                                            painterResource(Res.drawable.home_filled_24px),
                                            null,
                                            Modifier.size(16.dp)
                                        )
                                    }
                                )
                                FilterChip(
                                    selected = true,
                                    onClick = { },
                                    label = { Text("10m before") }
                                )
                                AssistChip(
                                    onClick = { },
                                    label = { Text("Add reminder") },
                                    leadingIcon = {
                                        Icon(
                                            painterResource(Res.drawable.home_filled_24px),
                                            null,
                                            Modifier.size(16.dp)
                                        )
                                    }
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }

            NewTaskCard {
                NewTaskCardItem(
                    headline = "Duration",
                    supportingText = "Estimated time to complete",
                    icon = painterResource(Res.drawable.home_filled_24px),
                    trailingText = "15m" // Dummy state
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                NewTaskCardItem(
                    headline = "Due Date",
                    supportingText = "Hard deadline",
                    icon = painterResource(Res.drawable.home_filled_24px),
                    trailingText = "None"
                )
            }

            Spacer(Modifier.height(24.dp))

            NewTaskCard {
                NewTaskCardItem(
                    headline = "Project",
                    icon = painterResource(Res.drawable.home_filled_24px),
                    trailingText = "Inbox"
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                // Tags
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(Res.drawable.home_filled_24px), null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(16.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            InputChip(
                                selected = false,
                                onClick = { },
                                label = { Text("Deep Work") },
                                trailingIcon = { Icon(painterResource(Res.drawable.home_filled_24px), null, Modifier.size(16.dp)) }
                            )
                            AssistChip(
                                onClick = { },
                                label = { Text("Add tag") },
                                leadingIcon = { Icon(painterResource(Res.drawable.home_filled_24px), null, Modifier.size(16.dp)) }
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                NewTaskCardItem(
                    headline = "Smart Context",
                    supportingText = "Trigger when conditions are met",
                    icon = painterResource(Res.drawable.home_filled_24px),
                    trailingText = "Setup"
                )
            }
            Spacer(modifier = Modifier.height(140.dp))
        }


    }
}



@Composable
fun ToolbarItemToggle(
    condition: Boolean,
    icon: DrawableResource,
    iconSelected: DrawableResource,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        colors =
            if(condition) IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            else IconButtonDefaults.iconButtonColors()
    ) {
        Icon(
            painterResource(if(condition) iconSelected else icon),
            contentDescription = "Drop"
        )
    }
}




data class SelectItem(
    val state: Int,
    val name: String,
    val icon: DrawableResource,
    val color: Color
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolbarItemSelect(
    list: List<SelectItem>,
    state: Int,
    onStateChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val activeItem = list.find { it.state == state}
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it}
    ) {
        IconButton(
            onClick = { },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
        ) {
            Icon(
                painter = painterResource(activeItem?.icon ?: Res.drawable.flag_24px),
                contentDescription = "Select",
                tint = activeItem?.color ?: LocalContentColor.current
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(percent = 50),
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh).padding(horizontal = 6.dp),
            properties = PopupProperties(focusable = false),
            offset = DpOffset(x = (-2).dp, y = 0.dp)
        ) {
            list.forEach { item ->
                val isSelected = item.state == state
                IconButton(
                    modifier = Modifier.size(40.dp),
                    onClick = {
                        onStateChange(item.state)
                        expanded = false
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        }else {
                            Color.Transparent
                        }
                    )
                ) {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = item.name,
                        tint = item.color // Default: MaterialTheme.colorScheme.onSurfaceVariant or just LocalContentColor.current
                    )
                }
            }
        }
    }
}







@Composable
fun NewTaskCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(content = content)
    }
}

@Composable
fun NewTaskCardItem(
    headline: String,
    supportingText: String? = null,
    icon: Painter,
    trailingText: String
) {
    ListItem(
        headlineContent = {
            Text(
                headline,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
            )
        },
        supportingContent = supportingText?.let {
            { Text(it, style = MaterialTheme.typography.bodySmall) }
        },
        leadingContent = {
            Icon(
                painter = icon,
                contentDescription = headline,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            Text(
                text = trailingText,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier.fillMaxWidth()
    )
}





@Preview
@Composable
fun NewTaskScreenPreview() {
    NewTaskScreen {}
}