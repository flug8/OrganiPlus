package li.flurin.organiplus.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import li.flurin.organiplus.composable.TextLogo

@Composable
fun MoreScreen() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            TextLogo(fontSize = 32.sp)
        }
    }
}



@Preview
@Composable
fun MoreScreenPreview() {
    MoreScreen()
}