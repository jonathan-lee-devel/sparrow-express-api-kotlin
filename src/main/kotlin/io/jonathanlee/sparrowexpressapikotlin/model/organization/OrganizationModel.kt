package io.jonathanlee.sparrowexpressapikotlin.model.organization

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "organizations")
data class OrganizationModel(
    @field:Id var objectId: ObjectId,
    var id: String,
    val name: String,
    var administratorEmails: MutableList<String>,
    var memberEmails: MutableList<String>,
)
