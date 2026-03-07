package li.flurin.organiplus

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform