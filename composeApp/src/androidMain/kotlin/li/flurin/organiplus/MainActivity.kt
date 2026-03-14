package li.flurin.organiplus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import li.flurin.organiplus.database.DatabaseHolder
import li.flurin.organiplus.database.SqlDriverFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        DatabaseHolder.init(SqlDriverFactory(applicationContext))

        setContent {
            App()
        }
    }
}

actual fun kmpLog(tag: String, message: String, isError: Boolean) {
    if (isError) android.util.Log.e(tag, message)
    else android.util.Log.d(tag, message)
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}