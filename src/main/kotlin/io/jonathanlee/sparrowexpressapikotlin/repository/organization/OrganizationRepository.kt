package io.jonathanlee.sparrowexpressapikotlin.repository.organization

import io.jonathanlee.sparrowexpressapikotlin.model.organization.OrganizationModel
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository


interface OrganizationRepository : MongoRepository<OrganizationModel?, ObjectId?> {
    fun findById(id: String?): OrganizationModel?
}
