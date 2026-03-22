package li.flurin.organiplus

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import li.flurin.organiplus.screen.AddTaskPopup
import li.flurin.organiplus.ui.theme.AppTheme

class QuickAddActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )

        //WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AppTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            finish()
                        }
                        .imePadding(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    AddTaskPopup(
                        onDismiss = { finish() },
                        onDirectSave = { finalDraft ->
                            // TODO: viewModel.savetodatabase
                            finish()
                        },
                        onExpandToFullEdit = { draftFromPopup ->
                            Graph.taskDraftRepository.pendingDraft = draftFromPopup
                            val intent = Intent(this@QuickAddActivity, MainActivity::class.java).apply {
                                action = "ACTION_OPEN_NEW_TASK"
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            }
                            startActivity(intent)
                            finish()
                        }
                    )
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        finish()
    }
}