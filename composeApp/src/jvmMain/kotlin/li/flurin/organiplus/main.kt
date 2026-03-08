package li.flurin.organiplus


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
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

fun main() = application {

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