package li.flurin.organiplus.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.tooling.preview.Preview

import org.jetbrains.compose.resources.Font
import organiplus.composeapp.generated.resources.Res
import organiplus.composeapp.generated.resources.muesomoderno_italic_variable


@Composable
fun HomeScreen() {
    TextLogo()
}


@Composable
fun TextLogo() {
    // 1. Create a FontFamily using the generated CMP resource
    val customFontFamily = FontFamily(
        Font(
            resource = Res.font.muesomoderno_italic_variable,
            weight = FontWeight.Bold,
            variationSettings = FontVariation.Settings(
                FontVariation.weight(800)
            )
        )
    )

    // 2. Display your text
    Text(
        text = "OrganiPlus",
        fontFamily = customFontFamily
    )
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}