package io.jonathanlee.sparrowexpressapikotlin.repository.organization

import io.jonathanlee.sparrowexpressapikotlin.model.organization.OrganizationMembershipRequestModel
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface OrganizationMembershipRequestRepository: MongoRepository<OrganizationMembershipRequestModel, ObjectId> {

    fun findById(organizationMembershipRequestId: String): OrganizationMembershipRequestModel?

    fun findOrganizationMembershipRequestModelsByOrganizationIdIs(organizationId: String): List<OrganizationMembershipRequestModel>

    fun findByRequestingUserEmailIs(requestingUserEmail: String): OrganizationMembershipRequestModel?

}