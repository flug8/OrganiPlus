package li.flurin.organiplus

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import kotlinx.coroutines.flow.MutableStateFlow
import li.flurin.organiplus.database.DatabaseHolder
import li.flurin.organiplus.database.SqlDriverFactory

class MainActivity : ComponentActivity() {

    private val navigationSignal = MutableStateFlow<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        DatabaseHolder.init(SqlDriverFactory(applicationContext))

        handleIntent(intent)

        setContent {
            App(navigationSignal = navigationSignal)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == "ACTION_OPEN_NEW_TASK") {
            navigationSignal.value = "NAV_NEW_TASK"
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