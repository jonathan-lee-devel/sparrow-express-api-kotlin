package io.jonathanlee.sparrowexpressapikotlin.dto

import jakarta.validation.constraints.Size

data class RequestDto(
        @field:Size(min = 1, max = 1) val id: String
)
