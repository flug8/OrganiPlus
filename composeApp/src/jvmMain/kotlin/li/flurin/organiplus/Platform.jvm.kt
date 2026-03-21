package li.flurin.organiplus

enum class OSType {WINDOWS, MACOS, LINUX, UNKNOWN}

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
    val osType: OSType = run {
        val os = System.getProperty("os.name").lowercase()
        when {
            os.contains("win") -> OSType.WINDOWS
            os.contains("mac") -> OSType.MACOS
            os.contains("nix") || os.contains("nux") || os.contains("aix") -> OSType.LINUX
            else -> OSType.UNKNOWN
        }
    }
    override val isMobile: Boolean = false
    override val isDesktop: Boolean = true
}

actual fun getPlatform(): Platform = JVMPlatform()