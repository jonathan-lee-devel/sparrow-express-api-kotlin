package io.jonathanlee.sparrowexpressapikotlin.validation.dto

data class ValidationErrorsContainerDto(
    val errors: Collection<ValidationErrorDto>
)
