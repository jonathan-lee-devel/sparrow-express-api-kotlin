package io.jonathanlee.sparrowexpressapikotlin.dto.organization

import io.jonathanlee.sparrowexpressapikotlin.dto.ResponseDto
import org.springframework.http.HttpStatus

data class OrganizationRequestDto(val name: String): ResponseDto(HttpStatus.INTERNAL_SERVER_ERROR)