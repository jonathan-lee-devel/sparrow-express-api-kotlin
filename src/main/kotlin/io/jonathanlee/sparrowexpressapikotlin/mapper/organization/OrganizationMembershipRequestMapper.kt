package io.jonathanlee.sparrowexpressapikotlin.mapper.organization

import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationMembershipRequestResponseDto
import io.jonathanlee.sparrowexpressapikotlin.model.organization.OrganizationMembershipRequestModel
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface OrganizationMembershipRequestMapper {

    @Mapping(source = "id", target = "id")
    fun organizationMembershipRequestListToOrganizationMembershipRequestDtoList(organizationMembershipRequestModel: List<OrganizationMembershipRequestModel>): List<OrganizationMembershipRequestResponseDto>

}
