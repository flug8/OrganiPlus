package li.flurin.organiplus.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedToggleButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.materialkolor.ktx.harmonize
import kotlinx.coroutines.launch
import li.flurin.organiplus.models.EnergyLevel
import li.flurin.organiplus.models.Priority
import li.flurin.organiplus.ui.theme.AppTheme
import li.flurin.organiplus.viewmodel.NewTaskViewModel
import li.flurin.organiplus.viewmodel.PopupStep
import li.flurin.organiplus.viewmodel.TaskCreationType
import org.jetbrains.compose.resources.painterResource
import organiplus.composeapp.generated.resources.Res
import organiplus.composeapp.generated.resources.arrow_forward_24px
import organiplus.composeapp.generated.resources.bolt_filled_24px
import organiplus.composeapp.generated.resources.event_available_filled_24px
import organiplus.composeapp.generated.resources.flag_24px
import organiplus.composeapp.generated.resources.flag_filled_24px
import organiplus.composeapp.generated.resources.keyboard_arrow_up_24px
import organiplus.composeapp.generated.resources.routine_filled_24px
import organiplus.composeapp.generated.resources.send_24px
import organiplus.composeapp.generated.resources.water_drop_filled_24px
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddTaskPopup(
    viewModel: NewTaskViewModel = viewModel(factory = NewTaskViewModel.Factory),
    onDismiss: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.Bottom
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 0.dp)
                    .fillMaxWidth()
            ) {
                Row (
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextField(
                        value = viewModel.title,
                        onValueChange = { viewModel.title = it },
                        placeholder = { Text("What needs to be done?") },
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done) // TODO: Change such that on enter, new line opens with Description
                    )

                    AnimatedVisibility(
                        visible = viewModel.isReadyToSend,
                        enter = scaleIn(),
                        exit = scaleOut(),
                        label = "send_button",
                    ) {
                        Button(
                            modifier = Modifier.width(40.dp),
                            onClick = { viewModel.advanceToNextStep() },
                            shape = ButtonDefaults.squareShape,
                            contentPadding = PaddingValues(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.send_24px),
                                contentDescription = "Next Step"
                            )
                        }
                    }

                }

                HorizontalDivider()

                AnimatedContent(
                    modifier = Modifier.padding(0.dp),
                    targetState = viewModel.currentStep,
                    transitionSpec =  {
                        val slideDirection = if (targetState.ordinal > viewModel.lastStepOrdinal) 1 else -1
                        val enter = slideInHorizontally(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            ),
                            initialOffsetX = { fullWidth -> fullWidth * slideDirection}
                        ) + fadeIn()

                        val exit = slideOutHorizontally(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            ),
                            targetOffsetX = { fullWidth -> -fullWidth * slideDirection }
                        ) + fadeOut()

                        (enter togetherWith exit).using(
                            SizeTransform(clip = false)
                        )
                    },
                    label = "step_animation"
                ) { step ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        when (step) {
                            PopupStep.TYPE_SELECTION -> {
                                SegmentedSelectionWithNextButton(
                                    items = listOf(
                                        TaskCreationType.TASK,
                                        TaskCreationType.DROP,
                                        TaskCreationType.HABIT
                                    ),
                                    selectedItem = viewModel.taskType,
                                    onItemSelected = { viewModel.selectTaskType(it) },
                                    onNextClicked = { viewModel.advanceToNextStep() },
                                    modifier = Modifier.padding(vertical = 16.dp)
                                ) { item, isSelected ->
                                    val (text, iconRes) = when (item) {
                                        TaskCreationType.TASK -> "Task" to Res.drawable.event_available_filled_24px
                                        TaskCreationType.DROP -> "Drop" to Res.drawable.water_drop_filled_24px
                                        TaskCreationType.HABIT -> "Habit" to Res.drawable.routine_filled_24px
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
                                    ) {
                                        Icon(
                                            painter = painterResource(iconRes),
                                            contentDescription = text,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(text)
                                    }
                                }
                            }

                            PopupStep.DATE_SELECTION -> {
                                Box(modifier = Modifier.padding(vertical = 16.dp)) {
                                    DateSelectionStep(
                                        selectedDate = viewModel.selectedDate,
                                        onDateSelected = { newDate ->
                                            viewModel.selectedDate = newDate
                                        },
                                        onSkipNext = { viewModel.advanceToNextStep() }
                                    )
                                }
                            }
                            PopupStep.TIME_SELECTION -> {
                                Box(modifier = Modifier.padding(vertical = 8.dp)) {
                                    TimeSelectionStep(
                                        initialTime = viewModel.selectedTime ?: LocalTime.now()
                                            .withMinute(0).plusHours(1),
                                        onTimeSelected = { newTime ->
                                            viewModel.selectedTime = newTime
                                        },
                                        onNext = {
                                            viewModel.advanceToNextStep()
                                        },
                                        onPrevious = {
                                            viewModel.goBackAStep()
                                        }
                                    )
                                }
                            }
                            PopupStep.REMINDER_SELECTION -> {
                                Row {
                                    Button(onClick = {  }) { Text("Select Time") }
                                    Spacer(Modifier.width(8.dp))
                                    OutlinedButton(onClick = { viewModel.advanceToNextStep() }) { Text("Next") }
                                }
                            }
                            PopupStep.PRIORITY_SELECTION -> {
                                SegmentedSelectionWithNextButton(
                                    items = listOf(
                                        Priority.NONE,
                                        Priority.LOW,
                                        Priority.NORMAL,
                                        Priority.HIGH,
                                        Priority.URGENT
                                    ),
                                    selectedItem = viewModel.priorityState,
                                    onItemSelected = { viewModel.selectPriority(it) },
                                    onNextClicked = { viewModel.advanceToNextStep() },
                                    selectedColorProvider = { item ->
                                        when (item) {
                                            Priority.NONE -> MaterialTheme.colorScheme.primary
                                            Priority.LOW -> Color(0xFF429E46).harmonize(MaterialTheme.colorScheme.primary)
                                            Priority.NORMAL -> Color(0xFF3F88C5).harmonize(MaterialTheme.colorScheme.primary)
                                            Priority.HIGH -> Color(0xFFF4900C).harmonize(MaterialTheme.colorScheme.primary)
                                            Priority.URGENT -> Color(0xFFD72638).harmonize(MaterialTheme.colorScheme.primary)
                                        }
                                    },
                                    modifier = Modifier.padding(vertical = 16.dp)
                                ) { item, isSelected ->
                                    val iconRes = if (item == Priority.NONE) Res.drawable.flag_24px else Res.drawable.flag_filled_24px
                                    Icon(
                                        painter = painterResource(iconRes),
                                        contentDescription = "Priority",
                                        tint = if (isSelected) LocalContentColor.current else when (item) {
                                            Priority.NONE -> LocalContentColor.current
                                            Priority.LOW -> Color(0xFF429E46).harmonize(MaterialTheme.colorScheme.primary)
                                            Priority.NORMAL -> Color(0xFF3F88C5).harmonize(MaterialTheme.colorScheme.primary)
                                            Priority.HIGH -> Color(0xFFF4900C).harmonize(MaterialTheme.colorScheme.primary)
                                            Priority.URGENT -> Color(0xFFD72638).harmonize(MaterialTheme.colorScheme.primary)
                                        }
                                    )
                                }
                            }
                            PopupStep.ENERGY_LEVEL_SELECTION -> {
                                SegmentedSelectionWithNextButton(
                                    items = listOf(
                                        EnergyLevel.LOW,
                                        EnergyLevel.MEDIUM,
                                        EnergyLevel.HIGH
                                    ),
                                    selectedItem = viewModel.energyState,
                                    onItemSelected = { viewModel.selectEnergyLevel(it) },
                                    onNextClicked = { viewModel.advanceToNextStep() },
                                    selectedColorProvider = { item ->
                                        when (item) {
                                            EnergyLevel.LOW -> Color(0xFF429E46)
                                            EnergyLevel.MEDIUM -> Color(0xFFF4900C)
                                            EnergyLevel.HIGH -> Color(0xFFD72638)
                                        }.harmonize(MaterialTheme.colorScheme.primary)
                                    },
                                    modifier = Modifier.padding(vertical = 16.dp)
                                ) { item, isSelected ->
                                    Icon(
                                        painter = painterResource(Res.drawable.bolt_filled_24px),
                                        contentDescription = "Energy Level",
                                        tint = if (isSelected) LocalContentColor.current else when (item) {
                                            EnergyLevel.LOW -> Color(0xFF429E46)
                                            EnergyLevel.MEDIUM -> Color(0xFFF4900C)
                                            EnergyLevel.HIGH -> Color(0xFFD72638)
                                        }.harmonize(MaterialTheme.colorScheme.primary)
                                    )
                                }
                            }
                            PopupStep.TAG_SELECTION -> {
                                Row {
                                    Button(onClick = {  }) { Text("Select Time") }
                                    Spacer(Modifier.width(8.dp))
                                    OutlinedButton(onClick = { viewModel.advanceToNextStep() }) { Text("Next") }
                                }
                            }
                            PopupStep.READY_TO_SEND -> {
                                Row {
                                    Button(onClick = {  }) { Text("Select Time") }
                                    Spacer(Modifier.width(8.dp))
                                    OutlinedButton(onClick = { viewModel.advanceToNextStep() }) { Text("Next") }
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T> SegmentedSelectionWithNextButton(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    onNextClicked: () -> Unit,
    modifier: Modifier = Modifier,
    selectedColorProvider: @Composable (T) -> Color = { MaterialTheme.colorScheme.primary },
    nextButtonIcon: Painter = painterResource(Res.drawable.arrow_forward_24px),
    itemContent: @Composable RowScope.(item: T, isSelected: Boolean) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
        ) {
            items.forEachIndexed { index, item ->
                val shape = when {
                    items.size == 1 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                    index == 0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                    index == items.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                }

                val isSelected = selectedItem == item

                val containerColor = selectedColorProvider(item)

                OutlinedToggleButton(
                    modifier = Modifier.weight(1f),
                    checked = isSelected,
                    onCheckedChange = { onItemSelected(item) },
                    shapes = shape,
                    colors = ToggleButtonDefaults.outlinedToggleButtonColors(
                        checkedContainerColor = containerColor,
                        checkedContentColor = contentColorFor(containerColor)
                    )
                ) {
                    itemContent(item, isSelected)
                }
            }
        }

        Button(
            modifier = Modifier.width(40.dp),
            onClick = onNextClicked,
            shape = ButtonDefaults.squareShape,
            contentPadding = PaddingValues(8.dp)
        ) {
            Icon(
                painter = nextButtonIcon,
                contentDescription = "Next Step"
            )
        }
    }
}




@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DateSelectionStep(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onSkipNext: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    val initialPagerState by remember { mutableIntStateOf(500) }
    val pagerState = rememberPagerState(initialPage = initialPagerState) { initialPagerState * 2 }

    val initialMonthPagerState by remember { mutableIntStateOf(500) }
    val monthPagerState = rememberPagerState(initialPage = initialMonthPagerState) { initialMonthPagerState * 2 }

    val scope = rememberCoroutineScope()
    val today = remember { LocalDate.now() }

    val displayedDate by remember {
        derivedStateOf {
            val weekOffset = pagerState.currentPage - initialPagerState
            today.plusWeeks(weekOffset.toLong())
        }
    }
    val displayedMonth by remember {
        derivedStateOf {
            val monthOffset = monthPagerState.currentPage - initialMonthPagerState
            today.withDayOfMonth(1).plusMonths(monthOffset.toLong())
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().animateContentSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val showTodayButton = if (isExpanded) {
                displayedMonth.year != today.year || displayedMonth.monthValue != today.monthValue
            } else {
                pagerState.currentPage != initialPagerState
            }
            if(showTodayButton) {
                Text(
                    text = "Today",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        onDateSelected(today)
                        scope.launch {
                            pagerState.animateScrollToPage(initialPagerState)
                            monthPagerState.animateScrollToPage(initialMonthPagerState)
                        }
                    }
                )
            } else {
                Box {}
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (isExpanded) {
                    IconButton(onClick = {
                        scope.launch { monthPagerState.animateScrollToPage(monthPagerState.currentPage - 1) }
                    }) {
                        Icon(
                            painter = painterResource(Res.drawable.keyboard_arrow_up_24px),
                            "Previous Month",
                            modifier = Modifier.rotate(270f)
                        )
                    }
                }
                Row (
                    modifier = Modifier.clickable { isExpanded = !isExpanded }
                ) {
                    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
                    val headerDate = if (isExpanded) displayedMonth else displayedDate
                    Text(
                        text = headerDate.format(formatter)
                    )
                    Icon(
                        painter = painterResource(Res.drawable.keyboard_arrow_up_24px),
                        contentDescription = "Toggle Month View",
                        modifier = Modifier.rotate(if (isExpanded) 180f else 0f)
                    )
                }
                if (isExpanded) {
                    IconButton(onClick = {
                        scope.launch { monthPagerState.animateScrollToPage(monthPagerState.currentPage + 1) }
                    }) {
                        Icon(
                            painter = painterResource(Res.drawable.keyboard_arrow_up_24px),
                            "Next Month",
                            modifier = Modifier.rotate(90f)
                        )
                    }
                }
            }
        }

        if (isExpanded) {
            HorizontalPager(state = monthPagerState) { page ->
                val monthOffset = page - initialMonthPagerState
                val currentPagerMonth = today.withDayOfMonth(1).plusMonths(monthOffset.toLong())

                val firstDayOfWeek = currentPagerMonth.dayOfWeek.value
                val daysToPrepend = firstDayOfWeek - 1
                val startOfGrid = currentPagerMonth.minusDays(daysToPrepend.toLong())

                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val daysOfWeek = DayOfWeek.entries.toTypedArray()
                        daysOfWeek.forEach { day ->
                            Box(
                                modifier = Modifier.size(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.getDisplayName(
                                        TextStyle.NARROW,
                                        Locale.getDefault()
                                    ),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                    for (row in 0 until 6) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            for (col in 0 until 7) {
                                val dayIndex = (row * 7) + col
                                val currentDate = startOfGrid.plusDays(dayIndex.toLong())

                                MonthDayCell(
                                    date = currentDate,
                                    currentMonth = displayedMonth,
                                    isSelected = currentDate.equals(selectedDate),
                                    isToday = currentDate.equals(today),
                                    onClick = {
                                        onDateSelected(currentDate)
                                        isExpanded = false

                                        val daysBetween = ChronoUnit.DAYS.between(
                                            today.with(DayOfWeek.MONDAY),
                                            currentDate.with(DayOfWeek.MONDAY)
                                        )
                                        val monthDiff = ChronoUnit.MONTHS.between(
                                            today.withDayOfMonth(1),
                                            currentDate.withDayOfMonth(1)
                                        )

                                        val weekOffset = (daysBetween / 7).toInt()
                                        scope.launch {
                                            pagerState.animateScrollToPage(initialPagerState + weekOffset)
                                            monthPagerState.scrollToPage(initialMonthPagerState + monthDiff.toInt())
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

        } else {
            Row (
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalPager(
                    modifier = Modifier.weight(1f),
                    state = pagerState,
                ) { page ->
                    val weekOffset = page - initialPagerState
                    val startOfWeek = today.plusWeeks(weekOffset.toLong()).with(DayOfWeek.MONDAY)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (i in 0..6) {
                            val currentDate = startOfWeek.plusDays(i.toLong())
                            DayCell(
                                date = currentDate,
                                isSelected = currentDate.equals(selectedDate),
                                isToday = currentDate.equals(today),
                                onClick = {
                                    onDateSelected(currentDate)
                                    //onSkipNext()
                                }
                            )
                        }
                    }
                }

                VerticalDivider(modifier = Modifier.height(28.dp))

                Button(
                    modifier = Modifier.width(40.dp),
                    onClick = onSkipNext,
                    shape = ButtonDefaults.squareShape,
                    contentPadding = PaddingValues(8.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.arrow_forward_24px),
                        contentDescription = "Next Step"
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DayCell(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault())
    val dayOfMonth = date.dayOfMonth.toString()

    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    val borderModifier = if (isToday && !isSelected) {
        Modifier.border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
    } else Modifier
    Column(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .then(borderModifier)
            .background(backgroundColor)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = dayOfWeek,
            fontSize = 10.sp,
            lineHeight = 10.sp,
            color = textColor
        )
        Text(
            text = dayOfMonth,
            fontSize = 14.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun MonthDayCell(
    date: LocalDate,
    currentMonth: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    val isCurrentMonth = date.month == currentMonth.month
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        isCurrentMonth -> MaterialTheme.colorScheme.onSurface
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
    }

    val borderModifier = if (isToday && !isSelected) {
        Modifier.border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
    } else Modifier

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .then(borderModifier)
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = textColor
        )
    }
}



@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TimeSelectionStep(
    initialTime: LocalTime = LocalTime.now(),
    onTimeSelected: (LocalTime) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    var selectedHour by remember { mutableIntStateOf(initialTime.hour) }
    var selectedMinute by remember { mutableIntStateOf(initialTime.minute) }

    LaunchedEffect(selectedHour, selectedMinute) {
        onTimeSelected(LocalTime.of(selectedHour, selectedMinute))
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Button(
            modifier = Modifier.width(40.dp),
            onClick = onPrevious,
            shape = ButtonDefaults.squareShape,
            contentPadding = PaddingValues(8.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.arrow_forward_24px),
                contentDescription = "Previous Step",
                modifier = Modifier.rotate(180f)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            ToggleButton(
                checked = false,
                onCheckedChange = {
                    val currentTime = LocalTime.of(selectedHour, selectedMinute)
                    val newTime = currentTime.minusMinutes(15)
                    selectedHour = newTime.hour
                    selectedMinute = newTime.minute
                },
                shapes = ButtonGroupDefaults.connectedLeadingButtonShapes(),
                colors = ToggleButtonDefaults.toggleButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    painter = painterResource(Res.drawable.keyboard_arrow_up_24px),
                    contentDescription = "Minus 15 Minutes",
                    modifier = Modifier.rotate(180f)
                )
            }
            WheelPickerLazy(
                count = 24,
                initialIndex = selectedHour,
                onIndexChanged = { selectedHour = it },
                modifier = Modifier.width(64.dp)
            )

            Text(
                text = ":",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            WheelPickerLazy(
                count = 12,
                initialIndex = selectedMinute / 5,
                onIndexChanged = { selectedMinute = it * 5 },
                modifier = Modifier.width(64.dp),
                formatItem = { String.format(Locale.ROOT, "%02d", it * 5) }
            )
            ToggleButton(
                checked = false,
                onCheckedChange = {
                    val currentTime = LocalTime.of(selectedHour, selectedMinute)
                    val newTime = currentTime.plusMinutes(15)
                    selectedHour = newTime.hour
                    selectedMinute = newTime.minute
                },
                shapes = ButtonGroupDefaults.connectedTrailingButtonShapes(),
                colors = ToggleButtonDefaults.toggleButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    painter = painterResource(Res.drawable.keyboard_arrow_up_24px),
                    contentDescription = "Plus 15 Minutes"
                )
            }
        }
        Button(
            modifier = Modifier.width(40.dp),
            onClick = onNext,
            shape = ButtonDefaults.squareShape,
            contentPadding = PaddingValues(8.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.arrow_forward_24px),
                contentDescription = "Next Step"
            )
        }
    }
}



@Composable
fun WheelPicker(
    count: Int,
    initialIndex: Int,
    onIndexChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    formatItem: (Int) -> String = { String.format(Locale.ROOT,"%02d", it) }
) {
    val virtualCount = 1_000_000 // probably safer than int.max
    val middle = virtualCount / 2
    val initialPage = remember { middle - (middle % count) + initialIndex }

    val pagerState = rememberPagerState(initialPage = initialPage) { virtualCount }

    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()
    var userScrolled by remember { mutableStateOf(false) }

    LaunchedEffect(isDragged) {
        if (isDragged) userScrolled = true
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            if (userScrolled) {
                onIndexChanged(page % count)
                userScrolled = false
            }
        }
    }

    LaunchedEffect(initialIndex) {
        val currentItem = pagerState.currentPage % count
        if (currentItem != initialIndex) {
            var diff = initialIndex - currentItem
            if (diff < -count / 2) diff += count
            if (diff > count / 2) diff -= count

            pagerState.animateScrollToPage(pagerState.currentPage + diff)
        }
    }

    Box(
        modifier = modifier.height(64.dp),
        contentAlignment = Alignment.Center
    ) {
        VerticalPager(
            state = pagerState,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 18.dp)
        ) { page ->
            val actualItem = page % count

            val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            val absOffset = pageOffset.absoluteValue

            val scale = 1f - (absOffset * 0.3f).coerceIn(0f, 0.5f)
            val alpha = 1f - (absOffset * 0.5f).coerceIn(0f, 0.8f)

            Box(
                modifier = Modifier
                    .height(28.dp)
                    .fillMaxWidth()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = formatItem(actualItem),
                    fontSize = 24.sp,
                    fontWeight = if (absOffset < 0.5f) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to MaterialTheme.colorScheme.surfaceContainer,
                        0.25f to Color.Transparent,
                        0.75f to Color.Transparent,
                        1.0f to MaterialTheme.colorScheme.surfaceContainer
                    )
                )
        )
    }
}


@Composable
fun WheelPickerLazy(
    count: Int,
    initialIndex: Int,
    onIndexChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    formatItem: (Int) -> String = { String.format(Locale.ROOT, "%02d", it) }
) {
    val virtualCount = 1_000_000
    val middle = virtualCount / 2
    val initialPage = remember { middle - (middle % count) + initialIndex }

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialPage)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    LaunchedEffect(initialIndex) {
        val layoutInfo = listState.layoutInfo
        val viewportTop = layoutInfo.viewportStartOffset
        val viewportBottom = layoutInfo.viewportEndOffset
        val viewportCenter = viewportTop + (viewportBottom - viewportTop) / 2f

        val centerItem = layoutInfo.visibleItemsInfo.minByOrNull {
            (viewportCenter - (it.offset + it.size / 2f)).absoluteValue
        }

        val currentVisible = centerItem?.index ?: listState.firstVisibleItemIndex
        val currentActual = currentVisible % count

        if (currentActual != initialIndex) {
            var diff = initialIndex - currentActual
            if (diff < -count / 2) diff += count
            if (diff > count / 2) diff -= count

            listState.animateScrollToItem(currentVisible + diff)
        }
    }

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val layoutInfo = listState.layoutInfo
            val viewportTop = layoutInfo.viewportStartOffset
            val viewportBottom = layoutInfo.viewportEndOffset
            val viewportCenter = viewportTop + (viewportBottom - viewportTop) / 2f

            val centerItem = layoutInfo.visibleItemsInfo.minByOrNull {
                (viewportCenter - (it.offset + it.size / 2f)).absoluteValue
            }

            centerItem?.let {
                onIndexChanged(it.index % count)
            }
        }
    }

    Box(
        modifier = modifier.height(64.dp),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 18.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(virtualCount) { index ->
                val actualItem = index % count

                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .fillMaxWidth()
                        .graphicsLayer {
                            val layoutInfo = listState.layoutInfo
                            val itemInfo = layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }

                            if (itemInfo != null) {
                                val viewportTop = layoutInfo.viewportStartOffset
                                val viewportBottom = layoutInfo.viewportEndOffset
                                val viewportCenter = viewportTop + (viewportBottom - viewportTop) / 2f

                                val itemCenter = itemInfo.offset + (itemInfo.size / 2f)
                                val distance = (viewportCenter - itemCenter).absoluteValue

                                val fraction = (distance / itemInfo.size.toFloat()).coerceIn(0f, 1f)

                                val scale = 1f - (fraction * 0.3f)
                                val alpha = 1f - (fraction * 0.5f)

                                scaleX = scale
                                scaleY = scale
                                this.alpha = alpha
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = formatItem(actualItem),
                        fontSize = 24.sp,
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to MaterialTheme.colorScheme.surfaceContainer,
                        0.25f to Color.Transparent,
                        0.75f to Color.Transparent,
                        1.0f to MaterialTheme.colorScheme.surfaceContainer
                    )
                )
        )
    }
}

@Composable
fun TempAddTaskScreen() {
    var showPopup by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = { showPopup = true }) {
                Text("Add")
            }
        }

        if (showPopup) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            )
            AddTaskPopup(
                onDismiss = { showPopup = false }
            )
        }
    }
}



@Preview
@Composable
fun DayCellPreview() {
    AppTheme {
        TimeSelectionStep(LocalTime.now(), {}, {}, {})
    }
}


@Preview(showBackground = true)
@Composable
fun AddTaskPopupPreview() {
    AppTheme {
        var showPopup by remember { mutableStateOf(true) }
        val mockViewModel = remember { NewTaskViewModel() }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(onClick = { showPopup = true }) {
                    Text("Add")
                }
            }

            if (showPopup) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                )
                AddTaskPopup(
                    viewModel = mockViewModel,
                    onDismiss = { showPopup = false }
                )
            }
        }
    }
}