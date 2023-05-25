package io.jonathanlee.sparrowexpressapikotlin.dto.organization

import io.jonathanlee.sparrowexpressapikotlin.constraint.CommonConstraints
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class OrganizationMembershipRequestDto(
    @field:NotNull @field:Size(min = CommonConstraints.ID_LENGTH, max = CommonConstraints.ID_LENGTH) val id: String
)
