package li.flurin.organiplus

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Organiplus",
    ) {
        App()
    }
}