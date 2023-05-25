package io.jonathanlee.sparrowexpressapikotlin.dto.organization

import io.jonathanlee.sparrowexpressapikotlin.dto.ResponseDto
import org.springframework.http.HttpStatus

data class OrganizationMembershipRequestResponseDto(
    val id: String?,
    val organizationId: String?,
    val requestingUserEmail: String?,
    val isApproved: Boolean,
    val approvingAdministratorEmail: String?
    ): ResponseDto(HttpStatus.INTERNAL_SERVER_ERROR)
