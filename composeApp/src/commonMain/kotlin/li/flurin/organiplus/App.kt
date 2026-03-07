package li.flurin.organiplus

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import li.flurin.organiplus.layout.AppLayout
import li.flurin.organiplus.ui.theme.AppTheme

@Composable
@Preview
fun App() {
    AppTheme {
        AppLayout()
    }
}


