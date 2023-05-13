package io.jonathanlee.sparrowexpressapikotlin.validation.dto


data class ValidationErrorDto(
    val field: String,
    val message: String
)
