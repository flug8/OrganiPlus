package li.flurin.organiplus


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import li.flurin.organiplus.database.DatabaseHolder
import li.flurin.organiplus.database.SqlDriverFactory
import li.flurin.organiplus.screen.TextLogo
import li.flurin.organiplus.ui.theme.AppTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.window.styling.dark
import org.jetbrains.jewel.intui.window.styling.light
import org.jetbrains.jewel.intui.window.styling.lightWithLightHeader
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.jewel.window.TitleBar
import org.jetbrains.jewel.window.newFullscreenControls
import org.jetbrains.jewel.window.styling.DecoratedWindowStyle
import org.jetbrains.jewel.window.styling.TitleBarColors
import org.jetbrains.jewel.window.styling.TitleBarStyle
import java.awt.Dimension
import java.util.logging.Level
import java.util.logging.Logger

fun main() = application {

    // ONLY FOR TESTING, TODO REMOVE LATER AGAIN
    var isPopupVisible by remember { mutableStateOf(false) }

    Tray(
        icon = painterResource("icon-512.png"),
        tooltip = "My Global Popup App",
        menu = {
            Item("Exit", onClick = ::exitApplication)
        }
    )
    DisposableEffect(Unit) {
        val logger = Logger.getLogger(GlobalScreen::class.java.getPackage().name)
        logger.level = Level.WARNING
        logger.useParentHandlers = false

        GlobalScreen.registerNativeHook()

        val keyListener = object : NativeKeyListener {
            override fun nativeKeyPressed(e: NativeKeyEvent) {
                if (e.modifiers and NativeKeyEvent.CTRL_MASK != 0 && e.keyCode == NativeKeyEvent.VC_SPACE) {
                    isPopupVisible = !isPopupVisible
                }
            }
        }
        GlobalScreen.addNativeKeyListener(keyListener)
        onDispose {
            GlobalScreen.removeNativeKeyListener(keyListener)
            GlobalScreen.unregisterNativeHook()
        }
    }
    if (isPopupVisible) {
        Window(
            onCloseRequest = { isPopupVisible = false },
            state = rememberWindowState(
                position = WindowPosition(Alignment.Center),
                width = 500.dp,
                height = 65.dp
            ),
            undecorated = true,
            transparent = true,
            alwaysOnTop = true,
            resizable = false
        ) {
            val focusRequester = remember { FocusRequester() }
            var text by remember { mutableStateOf("") }

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = MaterialTheme.shapes.medium,
                    elevation = 8.dp
                ) {
                    TextField(
                        value = text,
                        onValueChange = { text = it },
                        modifier = Modifier
                            .fillMaxSize()
                            .focusRequester(focusRequester)
                            .onPreviewKeyEvent { keyEvent ->
                                if(keyEvent.key == Key.Escape && keyEvent.type == KeyEventType.KeyDown) {
                                    isPopupVisible = false
                                    text = ""
                                    true
                                } else {
                                    false
                                }
                            },
                        placeholder = { Text("Search or type a command...") },
                        singleLine = true
                    )
                }
            }
        }
    }






















    DatabaseHolder.init(SqlDriverFactory())
    
    AppTheme {
        val isDark = isSystemInDarkTheme()
        IntUiTheme(
            isDark
        ) {
            DecoratedWindow(
                onCloseRequest = ::exitApplication,
                title = "OrganiPlus",
                style = if (isDark) DecoratedWindowStyle.dark() else DecoratedWindowStyle.light()
            ) {
                val density = LocalDensity.current
                LaunchedEffect(Unit) {
                    val minWidth = 400.dp
                    val minHeight = 300.dp

                    window.minimumSize = with(density) {
                        Dimension(
                            minWidth.toPx().toInt(),
                            minHeight.toPx().toInt()
                        )
                    }
                }


                TitleBar(
                    modifier = Modifier.newFullscreenControls().height(32.dp),
                    style =
                        if (isDark)
                            TitleBarStyle.dark(
                                colors = TitleBarColors.dark(
                                    backgroundColor = MaterialTheme.colorScheme.background,
                                    inactiveBackground = MaterialTheme.colorScheme.background
                                )
                            )
                        else
                            TitleBarStyle.lightWithLightHeader(
                                colors = TitleBarColors.lightWithLightHeader(
                                    backgroundColor = MaterialTheme.colorScheme.background,
                                    inactiveBackground = MaterialTheme.colorScheme.background
                                )
                            )
                ) {
                    TextLogo()
                }

                App()
            }
        }
    }
}

