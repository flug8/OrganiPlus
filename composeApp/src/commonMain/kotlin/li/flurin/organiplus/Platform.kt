package li.flurin.organiplus

interface Platform {
    val name: String
    val isMobile: Boolean
    val isDesktop: Boolean
}

expect fun getPlatform(): Platform