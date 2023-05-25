package io.jonathanlee.sparrowexpressapikotlin.model.organization

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "organization_membership_requests")
data class OrganizationMembershipRequestModel(
    @field:Id var objectId: ObjectId,
    var id: String,
    val organizationId: String,
    val requestingUserEmail: String,
    var isApproved: Boolean,
    var approvingAdministratorEmail: String?
)
