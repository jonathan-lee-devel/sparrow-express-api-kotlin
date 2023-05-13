package io.jonathanlee.sparrowexpressapikotlin.model.organization

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "organizations")
data class OrganizationModel(
    @field:Id val objectId: ObjectId,
    val id: String,
    val name: String,
    val administratorEmails: List<String>,
    val memberEmails: List<String>,
)
