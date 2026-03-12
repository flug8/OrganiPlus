package li.flurin.organiplus


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.sun.jna.Native
import com.sun.jna.platform.win32.Kernel32
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
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
import java.awt.event.WindowEvent
import java.awt.event.WindowFocusListener
import javax.swing.KeyStroke
import com.tulskiy.keymaster.common.Provider
import io.github.vinceglb.autolaunch.AutoLaunch
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.awt.Window

fun main(args: Array<String>) = application {

    val autoLaunch = remember { AutoLaunch("com.yourdomain.yourapp") }

    val coroutineScope = rememberCoroutineScope()

    var isAutoLaunchEnabled by remember { mutableStateOf(runBlocking { autoLaunch.isEnabled() }) }
    var startedViaAutostart by remember { mutableStateOf(autoLaunch.isStartedViaAutostart()) }

    var isMainWindowOpen by remember { mutableStateOf(!startedViaAutostart)}


    // ONLY FOR TESTING, TODO REMOVE LATER AGAIN
    var isPopupVisible by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val provider = Provider.getCurrentProvider(false)

        provider.register(KeyStroke.getKeyStroke("alt A")) { // TODO Make KeyStroke changeable
            isPopupVisible = !isPopupVisible
        }

        onDispose {
            provider.reset()
            provider.stop()
        }
    }

    Tray(
        icon = painterResource("OrganiPlus-300.png"), // TODO Change Icon
        tooltip = "OrganiPlus",
        menu = {
            Item("Open", onClick = {isMainWindowOpen = true})
            Item("Exit", onClick = ::exitApplication)
            CheckboxItem(
                text = "Launch on Startup",
                checked = isAutoLaunchEnabled,
                onCheckedChange = { isChecked ->
                    isAutoLaunchEnabled = isChecked
                    coroutineScope.launch {
                        try {
                            if (isChecked) {
                                autoLaunch.enable()
                            } else {
                                autoLaunch.disable()
                            }
                            isAutoLaunchEnabled = autoLaunch.isEnabled()
                        } catch (e: Exception) {
                            println("Failed to toggle auto-launch: ${e.message}")
                            isAutoLaunchEnabled = autoLaunch.isEnabled()
                        }
                    }
                })
        },
        onAction = {isMainWindowOpen = true}
    )
        val isDark = isSystemInDarkTheme()
        IntUiTheme(
            isDark
        ) {
            DecoratedWindow(
                visible = isPopupVisible,
                onCloseRequest = { isPopupVisible = false },
                state = rememberWindowState(
                    position = WindowPosition(Alignment.Center),
                    width = 500.dp,
                    height = 65.dp
                ),
                alwaysOnTop = true,
                resizable = false,
                style = if (isDark) DecoratedWindowStyle.dark() else DecoratedWindowStyle.light()
            ) {
                val focusRequester = remember { FocusRequester() }
                var text by remember { mutableStateOf("") }


                LaunchedEffect(isPopupVisible) {
                    if (isPopupVisible) {
                        window.toFront()
                        forceWindowsFocus(window)
                        focusRequester.requestFocus()
                    }
                }

                DisposableEffect(Unit) {
                    val focusListener = object : WindowFocusListener {
                        override fun windowGainedFocus(e: WindowEvent?) {}
                        override fun windowLostFocus(e: WindowEvent?) {
                            isPopupVisible = false
                            text = ""
                        }
                    }
                    window.addWindowFocusListener(focusListener)
                    onDispose { window.removeWindowFocusListener(focusListener) }
                }



                AppTheme {
                    TitleBar(
                        modifier = Modifier.newFullscreenControls().height(1.dp),
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
                    Surface(
                        modifier = Modifier.fillMaxSize().offset(y=(-1).dp)
                    ) {
                        TextField(
                            value = text,
                            onValueChange = { text = it },
                            modifier = Modifier
                                .fillMaxSize()
                                .focusRequester(focusRequester)
                                .onPreviewKeyEvent { keyEvent ->
                                    if (keyEvent.key == Key.Escape && keyEvent.type == KeyEventType.KeyDown) {
                                        isPopupVisible = false
                                        text = ""
                                        true
                                    } else {
                                        false
                                    }
                                },
                            placeholder = { Text("Search or type a command...") },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.background,
                                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                            )
                        )
                    }
                }
            }
    }






















    DatabaseHolder.init(SqlDriverFactory())

    if(isMainWindowOpen) {

        AppTheme {
            val isDark = isSystemInDarkTheme()
            IntUiTheme(
                isDark
            ) {
                DecoratedWindow(
                    onCloseRequest = { isMainWindowOpen = false },
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
                                        backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                        inactiveBackground = MaterialTheme.colorScheme.background
                                    )
                                )
                            else
                                TitleBarStyle.lightWithLightHeader(
                                    colors = TitleBarColors.lightWithLightHeader(
                                        backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow,
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
}




fun forceWindowsFocus(window: Window) {
    try {
        val hwnd = WinDef.HWND(Native.getComponentPointer(window))
        val user32 = User32.INSTANCE

        val foregroundWindow = user32.GetForegroundWindow()
        if (foregroundWindow == hwnd) return

        val foregroundThreadId = user32.GetWindowThreadProcessId(foregroundWindow, null)
        val currentThreadId = Kernel32.INSTANCE.GetCurrentThreadId()

        user32.AttachThreadInput(
            WinDef.DWORD(currentThreadId.toLong()),
            WinDef.DWORD(foregroundThreadId.toLong()),
            true
        )

        user32.SetForegroundWindow(hwnd)
        user32.SetFocus(hwnd)

        user32.AttachThreadInput(
            WinDef.DWORD(currentThreadId.toLong()),
            WinDef.DWORD(foregroundThreadId.toLong()),
            false
        )
    } catch (e: Exception) {
        println("Failed to force Windows focus: ${e.message}")
    }
}

