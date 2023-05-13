package io.jonathanlee.sparrowexpressapikotlin.service.organization

import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationResponseDto

interface OrganizationService {

    fun getOrganizationById(requestingUserEmail: String, organizationId: String): OrganizationResponseDto?

}
