package li.flurin.organiplus.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.window.core.layout.WindowSizeClass
import li.flurin.organiplus.Graph
import li.flurin.organiplus.composable.tooltip
import li.flurin.organiplus.models.Priority
import li.flurin.organiplus.viewmodel.NewTaskScreenViewModel
import li.flurin.organiplus.viewmodel.TaskCreationType
import li.flurin.organiplus.viewmodel.TaskDraft
import org.jetbrains.compose.resources.painterResource
import organiplus.composeapp.generated.resources.Res
import organiplus.composeapp.generated.resources.add_24px
import organiplus.composeapp.generated.resources.arrow_back_24px
import organiplus.composeapp.generated.resources.bolt_24px
import organiplus.composeapp.generated.resources.date_range_24px
import organiplus.composeapp.generated.resources.flag_24px
import organiplus.composeapp.generated.resources.home_filled_24px
import organiplus.composeapp.generated.resources.notifications_active_24px

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskScreen(
    onNavigateBack: () -> Unit
) {
    val viewModel: NewTaskScreenViewModel = viewModel {
        NewTaskScreenViewModel(Graph.taskDraftRepository)
    }
    val currentDraft by viewModel.draft.collectAsState()
    val showPopup by viewModel.showPopup.collectAsState()

    val mainFocusRequester = remember { FocusRequester() }
    val firstFocusRequester = remember { FocusRequester() }
    var isRootFocused by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .fillMaxSize()
        .focusRequester(mainFocusRequester)
        .onFocusChanged { isRootFocused = it.isFocused }
        .focusable()
        .pointerInput(Unit) {
            detectTapGestures(onTap = { mainFocusRequester.requestFocus() })
        }
        .onPreviewKeyEvent { event ->
            if (event.type == KeyEventType.KeyDown) {
                if (event.isCtrlPressed && event.key == Key.S) {
                    viewModel.saveTask(onComplete = onNavigateBack)
                    true
                } else if (event.key == Key.Tab && isRootFocused) {
                    firstFocusRequester.requestFocus()
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }
    ) {

        LaunchedEffect(Unit) {
            mainFocusRequester.requestFocus()
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Add Task") },
                    actions = {
                        FilledTonalButton(
                            onClick = {
                                viewModel.saveTask(onComplete = onNavigateBack)
                            },
                            modifier = Modifier.tooltip("Save Task", "s", ctrl = true)
                        ) {
                            Text("Save Task")
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(painterResource(Res.drawable.arrow_back_24px), contentDescription = "Back")
                        }
                    }
                )
            }
        ) { paddingValues ->
            NewTaskScreenContent(
                draft = currentDraft,
                onDraftChange = { viewModel.updateDraft(it) },
                firstFocusRequester = firstFocusRequester,
                modifier = Modifier.padding(paddingValues).fillMaxSize()
            )
        }

        if (showPopup) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    viewModel.onShowPopupChanged(false)
                }
            )

            AddTaskPopup(
                onDismiss = { onNavigateBack() },
                onDirectSave = { finalDraft ->
                    viewModel.updateDraft(finalDraft)
                    viewModel.saveTask(onComplete = onNavigateBack)
                },
                onExpandToFullEdit = { draftFromPopup ->
                    viewModel.onExpandFromPopup(draftFromPopup)
                }
            )
        }
    }
}



// TODO: EVERYTHING HERE BELOW IS A WORK IN PROGRESS AND CURRENTLY ONLY TEMPLATE ELEMENTS. THIS WILL NOT BE THE FINAL UI DESIGN AND FUNCTIONALITY WILL CHANGE!!!


@Composable
fun NewTaskScreenContent(
    draft: TaskDraft,
    onDraftChange: (TaskDraft) -> Unit,
    firstFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {

    val scheduleFocus = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        firstFocusRequester.requestFocus()
    }

    val isCompact = !currentWindowAdaptiveInfo().windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)


    val rightContent = @Composable {
        when (draft.type) {
            TaskCreationType.TASK -> {
                ScheduleAndRemindersModule(draft, onDraftChange, scheduleFocus)
                // TODO: Project Module
                TagsModule(draft, onDraftChange)
            }
            TaskCreationType.DROP -> {
                // TODO: Due Date Module
                // TODO: Duration Module
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    PriorityModule(draft, onDraftChange, Modifier.weight(1f))
                    EnergyModule(draft, onDraftChange, Modifier.weight(1f))
                }
            }
            TaskCreationType.HABIT -> {
                // TODO: Habit Modules
            }
        }
    }


    if (isCompact) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TypeSelectorModule(draft, onDraftChange)
            CreativeCanvasModule(draft, onDraftChange, firstFocusRequester, scheduleFocus)
            rightContent()
        }
    } else {
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(
                modifier = Modifier.weight(0.6f),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                TypeSelectorModule(draft, onDraftChange)

                CreativeCanvasModule(
                    draft = draft,
                    onDraftChange = onDraftChange,
                    modifier = Modifier.weight(1f),
                    titleFocus = firstFocusRequester,
                    nextFocus = scheduleFocus
                )
            }


            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rightContent()
            }
        }
    }
}



@Composable
fun ExpressiveModule(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = containerColor,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}

// --- Individual Modules ---

@Composable
fun TypeSelectorModule(draft: TaskDraft, onDraftChange: (TaskDraft) -> Unit) {
    // Minimalist pill-shaped selector
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val types = listOf(TaskCreationType.TASK, TaskCreationType.DROP, TaskCreationType.HABIT)
            types.forEach { type ->
                val isSelected = draft.type == type
                val label = type.name.lowercase().replaceFirstChar { it.uppercase() }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CircleShape)
                        .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                        .clickable { onDraftChange(draft.copy(type = type)) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun CreativeCanvasModule(
    draft: TaskDraft,
    onDraftChange: (TaskDraft) -> Unit,
    titleFocus: FocusRequester,
    nextFocus: FocusRequester,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    ExpressiveModule(modifier = modifier) {
        TextField(
            value = draft.title,
            onValueChange = { onDraftChange(draft.copy(title = it)) },
            placeholder = { Text("What needs to be done?", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
            textStyle = MaterialTheme.typography.displaySmall,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(titleFocus)
                .onPreviewKeyEvent { event ->
                    if (event.key == Key.Tab && event.type == KeyEventType.KeyDown && !event.isShiftPressed) {
                        focusManager.moveFocus(FocusDirection.Next)
                        true
                    } else {
                        false
                    }
                },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )

        TextField(
            value = draft.description ?: "",
            onValueChange = { onDraftChange(draft.copy(description = it)) },
            placeholder = { Text("Add notes, details, or links...", style = MaterialTheme.typography.bodyLarge) },
            textStyle = MaterialTheme.typography.bodyLarge,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .onPreviewKeyEvent { event ->
                    if (event.key == Key.Tab && event.type == KeyEventType.KeyDown && !event.isShiftPressed) {
                        nextFocus.requestFocus()
                        true
                    } else {
                        false
                    }
                },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleAndRemindersModule(draft: TaskDraft, onDraftChange: (TaskDraft) -> Unit, scheduleFocus: FocusRequester) {
    ExpressiveModule {
        // --- Schedule Section ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(scheduleFocus)
                    .clickable { /* TODO: Open Date Picker */ }) {
                Text(
                    "Schedule",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = draft.date?.toString()
                        ?: "Today", // TODO: Format nicely (e.g. "Tomorrow, Oct 24")
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { /* TODO: Open Time Picker */ }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(painterResource(Res.drawable.date_range_24px), contentDescription = null, modifier = Modifier.size(20.dp))
                    Text(
                        text = draft.time?.toString() ?: "Add Time",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

        // --- Reminders Section ---
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(painterResource(Res.drawable.notifications_active_24px), contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Text("Reminders", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // TODO: Replace with actual draft.reminders list
        val fakeReminders = listOf("15m before • Gentle", "08:00 on day • Aggressive")

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            fakeReminders.forEach { reminderText ->
                InputChip(
                    selected = false,
                    onClick = { /* TODO: Edit Reminder */ },
                    label = { Text(reminderText) },
                    trailingIcon = { Icon(painterResource(Res.drawable.home_filled_24px), "Remove", modifier = Modifier.size(16.dp)) },
                    shape = RoundedCornerShape(12.dp),
                    colors = InputChipDefaults.inputChipColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)
                )
            }

            AssistChip(
                onClick = { /* TODO: Open Add Reminder Flow */ },
                label = { Text("Add") },
                leadingIcon = { Icon(painterResource(Res.drawable.add_24px), "Add", modifier = Modifier.size(16.dp)) },
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
fun PriorityModule(draft: TaskDraft, onDraftChange: (TaskDraft) -> Unit, modifier: Modifier = Modifier) {
    // AspectRatio(1f) forces the module to be a perfect square!
    ExpressiveModule(
        modifier = modifier.aspectRatio(1f).clickable { /* TODO: Cycle priority or open popup */ },
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            val iconTint = when(draft.priority) {
                Priority.NONE -> MaterialTheme.colorScheme.onSurfaceVariant
                else -> MaterialTheme.colorScheme.primary // TODO: Use your harmonize colors here
            }

            Icon(painterResource(Res.drawable.flag_24px), contentDescription = "Priority", tint = iconTint, modifier = Modifier.size(32.dp))

            Column {
                Text("Priority", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = draft.priority.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun EnergyModule(draft: TaskDraft, onDraftChange: (TaskDraft) -> Unit, modifier: Modifier = Modifier) {
    ExpressiveModule(
        modifier = modifier.aspectRatio(1f).clickable { /* TODO: Cycle energy or open popup */ },
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(painterResource(Res.drawable.bolt_24px), contentDescription = "Energy", tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(32.dp))

            Column {
                Text("Energy", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = draft.energyLevel.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun TagsModule(draft: TaskDraft, onDraftChange: (TaskDraft) -> Unit) {
    ExpressiveModule {
        Text("Tags", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(12.dp))

        // TODO: Replace with draft.tags
        val fakeTags = listOf("Work", "Deep Work", "Urgent")

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            fakeTags.forEach { tag ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.clickable { /* TODO: Remove tag */ }
                ) {
                    Text(
                        text = "#$tag",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.Transparent,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.clickable { /* TODO: Open Tag Selector */ }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(painterResource(Res.drawable.add_24px), contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("Add tag", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}