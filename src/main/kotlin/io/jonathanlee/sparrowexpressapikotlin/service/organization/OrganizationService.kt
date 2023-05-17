package io.jonathanlee.sparrowexpressapikotlin.service.organization

import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationRequestDto
import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationResponseDto
import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationSnippetResponseDto

interface OrganizationService {

    fun getOrganizationById(requestingUserEmail: String, organizationId: String): OrganizationResponseDto?

    fun getOrganizationSnippetById(organizationId: String): OrganizationSnippetResponseDto?

    fun createOrganization(requestingUserEmail: String, organizationRequestDto: OrganizationRequestDto): OrganizationResponseDto?

    fun removeOrganizationAdministrator(requestingUserEmail: String, organizationId: String, administratorEmailToRemove: String): OrganizationResponseDto?

    fun removeOrganizationMember(requestingUserEmail: String, organizationId: String, memberEmailToRemove: String): OrganizationResponseDto?

    fun updateOrganizationAdministratorToJoinAsMember(requestingUserEmail: String, organizationId: String, administratorEmailToAddAsMember: String): OrganizationResponseDto?

}
