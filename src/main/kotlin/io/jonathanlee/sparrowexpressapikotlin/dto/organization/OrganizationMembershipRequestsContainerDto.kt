package io.jonathanlee.sparrowexpressapikotlin.dto.organization

import io.jonathanlee.sparrowexpressapikotlin.dto.ResponseDto
import org.springframework.http.HttpStatus

data class OrganizationMembershipRequestsContainerDto(
    val organizationMembershipRequests: List<OrganizationMembershipRequestResponseDto>?
): ResponseDto(HttpStatus.INTERNAL_SERVER_ERROR)
