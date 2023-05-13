package io.jonathanlee.sparrowexpressapikotlin.exception

data class BadRequestException(
    val field: String?,
    override val message: String
): RuntimeException(message)
