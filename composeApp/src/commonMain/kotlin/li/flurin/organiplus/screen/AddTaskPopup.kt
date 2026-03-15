package li.flurin.organiplus.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import li.flurin.organiplus.ui.theme.AppTheme
import li.flurin.organiplus.viewmodel.NewTaskViewModel
import li.flurin.organiplus.viewmodel.PopupStep
import li.flurin.organiplus.viewmodel.TaskCreationType
import org.jetbrains.compose.resources.painterResource
import organiplus.composeapp.generated.resources.Res
import organiplus.composeapp.generated.resources.arrow_back_24px
import organiplus.composeapp.generated.resources.arrow_forward_24px

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
        modifier = Modifier.fillMaxSize().imePadding().navigationBarsPadding(),
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
                TextField(
                    value = viewModel.title,
                    onValueChange = { viewModel.title = it },
                    placeholder = { Text("What needs to be done?")},
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done) // TODO: Change such that on enter, new line opens with Description
                )

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

                        enter togetherWith exit
                    },
                    label = "step_animation"
                ) { step ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
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
                            else -> {}
                        }
                    }
                }
            }
        }
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