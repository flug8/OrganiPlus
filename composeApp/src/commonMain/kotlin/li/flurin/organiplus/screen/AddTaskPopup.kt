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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import li.flurin.organiplus.ui.theme.AppTheme
import li.flurin.organiplus.viewmodel.NewTaskViewModel
import li.flurin.organiplus.viewmodel.PopupStep
import li.flurin.organiplus.viewmodel.TaskCreationType
import org.jetbrains.compose.resources.painterResource
import organiplus.composeapp.generated.resources.Res
import organiplus.composeapp.generated.resources.arrow_forward_24px
import organiplus.composeapp.generated.resources.keyboard_arrow_up_24px
import organiplus.composeapp.generated.resources.send_24px
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddTaskPopup(
    viewModel: NewTaskViewModel,
    onDismiss: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            // .imePadding() TODO: ADD IT BACK IT IS ONLY TO TEST WITH THE PREVIEW
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
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        when (step) {
                            PopupStep.TYPE_SELECTION -> {
                                Row (
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    SingleChoiceSegmentedButtonRow(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        SegmentedButton(
                                            shape = SegmentedButtonDefaults.itemShape(
                                                index = 0,
                                                count = 3
                                            ),
                                            onClick = { viewModel.selectTaskType(TaskCreationType.TASK) },
                                            selected = viewModel.taskType == TaskCreationType.TASK
                                        ) {
                                            Text("Task")
                                        }
                                        SegmentedButton(
                                            shape = SegmentedButtonDefaults.itemShape(
                                                index = 1,
                                                count = 3
                                            ),
                                            onClick = { viewModel.selectTaskType(TaskCreationType.HABIT) },
                                            selected = viewModel.taskType == TaskCreationType.HABIT
                                        ) {
                                            Text("Habit")
                                        }
                                        SegmentedButton(
                                            shape = SegmentedButtonDefaults.itemShape(
                                                index = 2,
                                                count = 3
                                            ),
                                            onClick = { viewModel.selectTaskType(TaskCreationType.DROP) },
                                            selected = viewModel.taskType == TaskCreationType.DROP
                                        ) {
                                            Text("Drop")
                                        }
                                    }
                                    Button(
                                        modifier = Modifier.width(40.dp),
                                        onClick = { viewModel.advanceToNextStep() },
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

                            PopupStep.DATE_SELECTION -> {
                                DateSelectionStep(
                                    selectedDate = viewModel.selectedDate, // Add this state to your ViewModel
                                    onDateSelected = { newDate -> viewModel.selectedDate = newDate },
                                    onSkipNext = { viewModel.advanceToNextStep() }
                                )
                                /*Row {
                                    Button(onClick = {  }) { Text("Select Date") }
                                    Spacer(Modifier.width(8.dp))
                                    OutlinedButton(onClick = { viewModel.advanceToNextStep() }) { Text("Next") }
                                }*/
                            }
                            PopupStep.TIME_SELECTION -> {
                                Row {
                                    Button(onClick = {  }) { Text("Select Time") }
                                    Spacer(Modifier.width(8.dp))
                                    OutlinedButton(onClick = { viewModel.advanceToNextStep(); viewModel.isReadyToSend = true }) { Text("Next") }
                                }
                            }
                            PopupStep.PRIORITY_SELECTION -> {
                                Row {
                                    Button(onClick = {  }) { Text("Select Time") }
                                    Spacer(Modifier.width(8.dp))
                                    OutlinedButton(onClick = { viewModel.advanceToNextStep() }) { Text("Next") }
                                }
                            }
                            PopupStep.ENERGYLEVEL_SELECTION -> {
                                Row {
                                    Button(onClick = {  }) { Text("Select Time") }
                                    Spacer(Modifier.width(8.dp))
                                    OutlinedButton(onClick = { viewModel.advanceToNextStep() }) { Text("Next") }
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
fun DateSelectionStep(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onSkipNext: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val initialPagerState by remember { mutableIntStateOf(500) }
    val pagerState = rememberPagerState(initialPage = initialPagerState) { initialPagerState * 2 }
    val scope = rememberCoroutineScope()
    val today = remember { LocalDate.now() }

    val displayedDate by remember {
        derivedStateOf {
            val weekOffset = pagerState.currentPage - initialPagerState
            today.plusWeeks(weekOffset.toLong())
        }
    }
    var displayedMonth by remember {
        mutableStateOf(displayedDate.withDayOfMonth(1))
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
                        displayedMonth = today.withDayOfMonth(1)
                        scope.launch {
                            pagerState.animateScrollToPage(initialPagerState)
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
                    IconButton(onClick = { displayedMonth = displayedMonth.minusMonths(1) }) {
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
                    IconButton(onClick = { displayedMonth = displayedMonth.plusMonths(1) }) {
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
            val firstDayOfMonth = displayedMonth.withDayOfMonth(1)
            val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value
            val daysToPrepend = firstDayOfWeek - 1
            val startOfGrid = firstDayOfMonth.minusDays(daysToPrepend.toLong())

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val daysOfWeek = DayOfWeek.entries.toTypedArray()
                    daysOfWeek.forEach { day ->
                        Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = day.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.6f)
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
                                    val weekOffset = (daysBetween / 7).toInt()
                                    scope.launch {
                                        pagerState.animateScrollToPage(initialPagerState + weekOffset)
                                    }
                                }
                            )
                        }
                    }
                }
            }

        } else {
            HorizontalPager(state = pagerState) { page ->
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
        }
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


@Preview
@Composable
fun DayCellPreview() {

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