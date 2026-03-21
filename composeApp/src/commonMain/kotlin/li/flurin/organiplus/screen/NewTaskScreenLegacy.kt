package li.flurin.organiplus.screen


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.InputChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import li.flurin.organiplus.NavNewTask
import li.flurin.organiplus.models.EnergyLevel
import li.flurin.organiplus.models.Priority
import li.flurin.organiplus.viewmodel.AddTaskPopupViewModel
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import organiplus.composeapp.generated.resources.Res
import organiplus.composeapp.generated.resources.add_24px
import organiplus.composeapp.generated.resources.add_big_24px
import organiplus.composeapp.generated.resources.arrow_back_24px
import organiplus.composeapp.generated.resources.assignment_24px
import organiplus.composeapp.generated.resources.assignment_late_24px
import organiplus.composeapp.generated.resources.bolt_filled_24px
import organiplus.composeapp.generated.resources.bucket_check_24px
import organiplus.composeapp.generated.resources.bucket_check_filled_24px
import organiplus.composeapp.generated.resources.date_range_24px
import organiplus.composeapp.generated.resources.extension_24px
import organiplus.composeapp.generated.resources.flag_24px
import organiplus.composeapp.generated.resources.flag_filled_24px
import organiplus.composeapp.generated.resources.home_filled_24px
import organiplus.composeapp.generated.resources.hourglass_24px
import organiplus.composeapp.generated.resources.notifications_active_24px
import organiplus.composeapp.generated.resources.release_alert_24px
import organiplus.composeapp.generated.resources.repeat_24px
import organiplus.composeapp.generated.resources.save_filled_24px
import organiplus.composeapp.generated.resources.tag_24px
import organiplus.composeapp.generated.resources.wand_shine_24px
import organiplus.composeapp.generated.resources.wand_shine_filled_24px


@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NewTaskScreenLegacy(
    onBack: () -> Unit,
    onNavigate: (Any) -> Unit,
    viewModel: AddTaskPopupViewModel = viewModel()
) {
    var isExpanded by remember { mutableStateOf(true) }

    val titleFocusRequester = remember { FocusRequester() }
    val descriptionFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current


    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { target ->
            when (target) {
                is AddTaskPopupViewModel.NavTarget.Back -> onBack()
                is AddTaskPopupViewModel.NavTarget.NewTask -> onNavigate(NavNewTask)
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(100)
        titleFocusRequester.requestFocus()
        keyboardController?.show()
    }

    val scrollState = rememberScrollState()
    val showTopBarTitle by remember {
        derivedStateOf { scrollState.value > 125 }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    AnimatedVisibility(
                        modifier = Modifier.padding(end = 16.dp).offset(x = 16.dp),
                        visible = showTopBarTitle && viewModel.title.isNotBlank(),
                        enter = fadeIn() + slideInVertically { it / 2 },
                        exit = fadeOut() + slideOutVertically { it / 2 }
                    ) {
                        Text(
                            text = viewModel.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.offset(x = 16.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    ) {
                        Icon(
                            painterResource(Res.drawable.arrow_back_24px),
                            contentDescription = "Go back"
                        )
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        contentWindowInsets = WindowInsets.safeDrawing,
        floatingActionButton = {
            HorizontalFloatingToolbar(
                expanded = isExpanded,
                floatingActionButton = {
                    FloatingToolbarDefaults.StandardFloatingActionButton(
                        onClick = {
                            viewModel.saveAndExit()
                        }
                    ) {
                        Icon(
                            painterResource(Res.drawable.save_filled_24px),
                            contentDescription = "Save and Close"
                        )
                    }
                },
                content = {
                    ToolbarItemToggle(
                        condition = viewModel.isSmartSchedule,
                        icon = Res.drawable.wand_shine_24px,
                        iconSelected = Res.drawable.wand_shine_filled_24px
                    ) {
                        viewModel.isSmartSchedule = !viewModel.isSmartSchedule
                        viewModel.isDrop = false
                    }
                    ToolbarItemToggle(
                        condition = viewModel.isDrop,
                        icon = Res.drawable.bucket_check_24px,
                        iconSelected = Res.drawable.bucket_check_filled_24px
                    ) {
                        viewModel.isDrop = !viewModel.isDrop
                        viewModel.isSmartSchedule = false
                    }
                    ToolbarItemSelect(
                        list = listOf(
                            SelectItem(Priority.URGENT,"Urgent", Res.drawable.flag_filled_24px, Color(0xFFD72638)),
                            SelectItem(Priority.HIGH,"High", Res.drawable.flag_filled_24px, Color(0xFFF4900C)),
                            SelectItem(Priority.NORMAL,"Normal", Res.drawable.flag_filled_24px, Color(0xFF3F88C5)),
                            SelectItem(Priority.LOW,"Low", Res.drawable.flag_filled_24px, Color(0xFF429E46)),
                            SelectItem(Priority.NONE,"None", Res.drawable.flag_24px, LocalContentColor.current),
                        ),
                        state = viewModel.priorityState
                    ) { state ->
                        viewModel.priorityState = state
                    }
                    ToolbarItemSelect(
                            list = listOf(
                                SelectItem(EnergyLevel.HIGH,"High", Res.drawable.bolt_filled_24px, Color(0xFFD72638)),
                                SelectItem(EnergyLevel.MEDIUM,"Medium", Res.drawable.bolt_filled_24px, Color(0xFFF4900C)),
                                SelectItem(EnergyLevel.LOW,"Low", Res.drawable.bolt_filled_24px, Color(0xFF429E46)),
                            ),
                    state = viewModel.energyState
                    ) { state ->
                        viewModel.energyState = state
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
                        viewModel.saveAndNext()
                    }
                },
                colors = FloatingToolbarDefaults.standardFloatingToolbarColors(
                    toolbarContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            )
        }
    ) { paddingValues ->

        // TODO: Edit, only temporary placeholder, idk if this design will be final

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = viewModel.title,
                    onValueChange = { viewModel.title = it },
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
                    value = viewModel.description,
                    onValueChange = { viewModel.description = it },
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
                visible = !viewModel.isDrop,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    NewTaskCard {
                        NewTaskCardItem(
                            headline = "Scheduled Start",
                            supportingText = "When to do this",
                            icon = painterResource(Res.drawable.date_range_24px),
                            trailingText = "Tomorrow, 10:00 AM"
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        NewTaskCardItem(
                            headline = "Repeat",
                            icon = painterResource(Res.drawable.repeat_24px),
                            trailingText = "Weekly"
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painterResource(Res.drawable.notifications_active_24px),
                                    null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    InputChip(
                                        selected = false,
                                        onClick = { },
                                        label = { Text("On Scheduled") },
                                    )
                                    InputChip(
                                        selected = false,
                                        onClick = { },
                                        label = { Text("Aggressive") },
                                    )
                                    AssistChip(
                                        onClick = { },
                                        label = {
                                            Icon(
                                                painterResource(Res.drawable.add_24px),
                                                "Remove Reminder"
                                            )
                                        },
                                    )
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Spacer(modifier = Modifier.width((16 + 24).dp))
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    InputChip(
                                        selected = false,
                                        onClick = { },
                                        label = { Text("Deep Work") },
                                        trailingIcon = {
                                            Icon(
                                                painterResource(Res.drawable.home_filled_24px),
                                                null,
                                                Modifier.size(16.dp)
                                            )
                                        }
                                    )
                                    AssistChip(
                                        onClick = { },
                                        label = { Text("Add tag") },
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
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }

            NewTaskCard {
                NewTaskCardItem(
                    headline = "Duration",
                    supportingText = "Estimated time to complete",
                    icon = painterResource(Res.drawable.hourglass_24px),
                    trailingText = "15m" // Dummy state
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                NewTaskCardItem(
                    headline = "Due Date",
                    supportingText = "Hard deadline",
                    icon = painterResource(Res.drawable.assignment_late_24px),
                    trailingText = "None"
                )
            }

            Spacer(Modifier.height(24.dp))

            NewTaskCard {
                NewTaskCardItem(
                    headline = "Project",
                    icon = painterResource(Res.drawable.assignment_24px),
                    trailingText = "Inbox"
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                // Tags
                Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(Res.drawable.tag_24px), null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(16.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            InputChip(
                                selected = false,
                                onClick = { },
                                label = { Text("Deep Work") },
                            )
                            AssistChip(
                                onClick = { },
                                label = { Text("Add tag") },
                                leadingIcon = { Icon(painterResource(Res.drawable.add_24px), null, Modifier.size(16.dp)) }
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                NewTaskCardItem(
                    headline = "Smart Context",
                    supportingText = "Trigger when conditions are met",
                    icon = painterResource(Res.drawable.extension_24px),
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




data class SelectItem<T>(
    val state: T,
    val name: String,
    val icon: DrawableResource,
    val color: Color
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> ToolbarItemSelect(
    list: List<SelectItem<T>>,
    state: T,
    onStateChange: (T) -> Unit
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
                painter = painterResource(activeItem?.icon ?: Res.drawable.release_alert_24px),
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
fun NewTaskScreenLegacyPreview() {
    NewTaskScreenLegacy({},{})
}