package li.flurin.organiplus.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AppTooltipState {
    var isVisible by mutableStateOf(false)
    var text by mutableStateOf("")
    var position by mutableStateOf(Offset.Zero)

    var key by mutableStateOf<String?>(null)
    var isCtrl by mutableStateOf(false)
    var isAlt by mutableStateOf(false)
    var isShift by mutableStateOf(false)
}

val LocalAppTooltip = compositionLocalOf { AppTooltipState() }

fun Modifier.tooltip(
    text: String,
    key: String? = null,
    ctrl: Boolean = false,
    alt: Boolean = false,
    shift: Boolean = false
): Modifier = composed {
    val tooltipState = LocalAppTooltip.current
    val scope = rememberCoroutineScope()
    var hoverJob by remember { mutableStateOf<Job?>(null) }
    var itemPosition by remember { mutableStateOf(Offset.Zero) }

    this
        .onGloballyPositioned { itemPosition = it.positionInWindow() }
        .pointerInput(text, key, ctrl, alt, shift) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent()
                    val cursorPosition = event.changes.first().position

                    when (event.type) {
                        PointerEventType.Enter -> {
                            hoverJob = scope.launch {
                                delay(500)
                                tooltipState.text = text
                                tooltipState.key = key
                                tooltipState.isCtrl = ctrl
                                tooltipState.isAlt = alt
                                tooltipState.isShift = shift
                                tooltipState.isVisible = true
                            }
                        }
                        PointerEventType.Move -> {
                            tooltipState.position = itemPosition + cursorPosition
                        }
                        PointerEventType.Exit -> {
                            hoverJob?.cancel()
                            tooltipState.isVisible = false
                        }
                    }
                }
            }
        }
}

@Composable
fun AppTooltipProvider(content: @Composable () -> Unit) {
    val tooltipState = LocalAppTooltip.current
    val animationState = remember { MutableTransitionState(false) }

    LaunchedEffect(tooltipState.isVisible) {
        animationState.targetState = tooltipState.isVisible
    }

    Box(modifier = Modifier.fillMaxSize()) {
        content()

        if (animationState.currentState || animationState.targetState) {
            Popup(
                popupPositionProvider = TooltipPositionProvider(tooltipState.position),
                properties = PopupProperties(focusable = false, dismissOnClickOutside = false)
            ) {
                AnimatedVisibility(
                    visibleState = animationState,
                    enter = fadeIn(tween(200)) + scaleIn(initialScale = 0.8f),
                    exit = fadeOut(tween(150)) + scaleOut(targetScale = 0.8f)
                ) {
                    /*Box(
                        modifier = Modifier
                            .shadow(12.dp, RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp), RoundedCornerShape(10.dp))
                            .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(0.3f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = tooltipState.text,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }*/
                    Box(
                        modifier = Modifier
                            .shadow(12.dp, RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp), RoundedCornerShape(10.dp))
                            .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(0.3f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            // 12.dp separates the text from the hotkeys
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Main Tooltip Text
                            Text(
                                text = tooltipState.text,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            // Hotkey Indicators
                            if (tooltipState.key != null) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp) // Tight spacing between keys
                                ) {
                                    if (tooltipState.isCtrl) HotkeyChip(getCtrlLabel())
                                    if (tooltipState.isAlt) HotkeyChip(getAltLabel())
                                    if (tooltipState.isShift) HotkeyChip("Shift")

                                    HotkeyChip(tooltipState.key!!.uppercase())
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


class TooltipPositionProvider(
    private val cursorPosition: Offset,
    private val offset: IntOffset = IntOffset(20, 20)
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        var x = cursorPosition.x.toInt() + offset.x
        var y = cursorPosition.y.toInt() + offset.y

        if (x + popupContentSize.width > windowSize.width) {
            x = cursorPosition.x.toInt() - popupContentSize.width - offset.x
        }

        if (y + popupContentSize.height > windowSize.height) {
            y = cursorPosition.y.toInt() - popupContentSize.height - offset.y
        }

        return IntOffset(x.coerceAtLeast(0), y.coerceAtLeast(0))
    }
}


fun getCtrlLabel() = "Ctrl" // TODO: if (isMac) "Cmd" else "Ctrl"
fun getAltLabel() = "Alt"   // TODO: if (isMac) "Option" else "Alt"

@Composable
fun HotkeyChip(text: String) {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                shape = RoundedCornerShape(4.dp)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 4.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}