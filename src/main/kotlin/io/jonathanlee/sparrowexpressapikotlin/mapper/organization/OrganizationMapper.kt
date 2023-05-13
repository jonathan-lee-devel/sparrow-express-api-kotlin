package io.jonathanlee.sparrowexpressapikotlin.mapper.organization

import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationRequestDto
import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationResponseDto
import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationSnippetResponseDto
import io.jonathanlee.sparrowexpressapikotlin.model.organization.OrganizationModel
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper
interface OrganizationMapper {

    @Mapping(source = "id", target = "id")
    fun organizationModelToOrganizationResponseDto(organizationModel: OrganizationModel?): OrganizationResponseDto?

    @Mapping(source = "id", target = "id")
    fun organizationModelToOrganizationSnippetResponseDto(organizationModel: OrganizationModel?): OrganizationSnippetResponseDto?

    @Mapping(source = "name", target = "name")
    fun organizationRequestDtoToOrganizationModel(organizationRequestDto: OrganizationRequestDto?): OrganizationModel?

}
