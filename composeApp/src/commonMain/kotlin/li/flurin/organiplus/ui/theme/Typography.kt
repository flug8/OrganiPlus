package li.flurin.organiplus.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import org.jetbrains.compose.resources.Font
import organiplus.composeapp.generated.resources.Res
import organiplus.composeapp.generated.resources.google_sans_flex_variable

@Composable
fun getAppTypography(): Typography {
    val googleSansRounded = FontFamily(
        Font(
            resource = Res.font.google_sans_flex_variable,
            variationSettings = FontVariation.Settings(
                FontVariation.Setting("ROND", 100f),
                FontVariation.Setting("wght", 500f)
            )
        )
    )
    val googleSansRegular = FontFamily(
        Font(
            resource = Res.font.google_sans_flex_variable,
            variationSettings = FontVariation.Settings(
                FontVariation.Setting("ROND", 50f),
                FontVariation.Setting("wght", 600f)
            )
        )
    )

    val baseline = Typography()

    return Typography(
        displayLarge = baseline.displayLarge.copy(fontFamily = googleSansRounded),
        displayMedium = baseline.displayMedium.copy(fontFamily = googleSansRounded),
        displaySmall = baseline.displaySmall.copy(fontFamily = googleSansRounded),
        headlineLarge = baseline.headlineLarge.copy(fontFamily = googleSansRounded),
        headlineMedium = baseline.headlineMedium.copy(fontFamily = googleSansRounded),
        headlineSmall = baseline.headlineSmall.copy(fontFamily = googleSansRounded),
        titleLarge = baseline.titleLarge.copy(fontFamily = googleSansRegular),
        titleMedium = baseline.titleMedium.copy(fontFamily = googleSansRegular),
        titleSmall = baseline.titleSmall.copy(fontFamily = googleSansRegular),
        bodyLarge = baseline.bodyLarge.copy(fontFamily = googleSansRegular),
        bodyMedium = baseline.bodyMedium.copy(fontFamily = googleSansRegular),
        bodySmall = baseline.bodySmall.copy(fontFamily = googleSansRegular),
        labelLarge = baseline.labelLarge.copy(fontFamily = googleSansRegular),
        labelMedium = baseline.labelMedium.copy(fontFamily = googleSansRegular),
        labelSmall = baseline.labelSmall.copy(fontFamily = googleSansRegular)
    )
}



@Composable
fun getGoogleSansFlexFont(
    weight: Float = 400f,       // wght: 100 to 900
    roundness: Float = 50f,     // ROND: 0 to 100
    slant: Float = 0f,          // slnt: -10 to 0
    grade: Float = 0f,          // GRAD: 0 to 100
    width: Float = 100f,        // wdth: 25 to 150
    opticalSize: Float = 14f    // opsz: ...
): FontFamily {
    return FontFamily(
        Font(
            resource = Res.font.google_sans_flex_variable,
            variationSettings = FontVariation.Settings(
                FontVariation.Setting("wght", weight),
                FontVariation.Setting("ROND", roundness),
                FontVariation.Setting("slnt", slant),
                FontVariation.Setting("GRAD", grade),
                FontVariation.Setting("wdth", width),
                FontVariation.Setting("opsz", opticalSize)
            )
        )
    )
}