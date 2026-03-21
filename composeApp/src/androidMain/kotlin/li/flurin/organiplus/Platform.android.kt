package li.flurin.organiplus

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val isMobile: Boolean = true
    override val isDesktop: Boolean = false
}

actual fun getPlatform(): Platform = AndroidPlatform()