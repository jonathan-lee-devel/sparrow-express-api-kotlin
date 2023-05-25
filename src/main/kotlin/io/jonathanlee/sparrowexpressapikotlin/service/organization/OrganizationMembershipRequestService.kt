package io.jonathanlee.sparrowexpressapikotlin.service.organization

import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationMembershipDto
import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationMembershipRequestDto
import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationMembershipRequestsContainerDto

interface OrganizationMembershipRequestService {

    fun requestToJoinOrganization(requestingUserEmail: String, requesterEmail: String, organizationId: String): OrganizationMembershipDto?

    fun getRequestsToJoinOrganization(requestingUserEmail: String, organizationId: String): OrganizationMembershipRequestsContainerDto?

    fun approveRequestToJoinOrganization(requestingUserEmail: String, organizationMembershipRequestDto: OrganizationMembershipRequestDto): OrganizationMembershipDto?

}
