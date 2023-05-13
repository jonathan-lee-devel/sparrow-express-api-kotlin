package io.jonathanlee.sparrowexpressapikotlin.dto.organization

import io.jonathanlee.sparrowexpressapikotlin.dto.ResponseDto
import org.springframework.http.HttpStatus

data class OrganizationResponseDto(
    val id: String?,
    val name: String?,
    val administratorEmails: List<String>?,
    val memberEmails: List<String>?
) : ResponseDto(HttpStatus.INTERNAL_SERVER_ERROR)
