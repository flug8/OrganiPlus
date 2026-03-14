package li.flurin.organiplus.composable

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import org.jetbrains.compose.resources.Font
import organiplus.composeapp.generated.resources.Res
import organiplus.composeapp.generated.resources.muesomoderno_italic_variable

@Composable
fun TextLogo(color: Color = MaterialTheme.colorScheme.onSurface, fontSize: TextUnit = TextUnit.Unspecified) {
    val customFontFamily = FontFamily(
        Font(
            resource = Res.font.muesomoderno_italic_variable,
            weight = FontWeight.Bold,
            variationSettings = FontVariation.Settings(
                FontVariation.weight(800)
            )
        )
    )

    Text(
        text = "OrganiPlus",
        fontFamily = customFontFamily,
        color = color,
        fontSize = fontSize
    )
}