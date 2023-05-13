package io.jonathanlee.sparrowexpressapikotlin.service.organization.impl

import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationResponseDto
import io.jonathanlee.sparrowexpressapikotlin.mapper.organization.OrganizationMapper
import io.jonathanlee.sparrowexpressapikotlin.model.organization.OrganizationModel
import io.jonathanlee.sparrowexpressapikotlin.repository.organization.OrganizationRepository
import io.jonathanlee.sparrowexpressapikotlin.service.organization.OrganizationService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class OrganizationServiceImpl(
    private val organizationRepository: OrganizationRepository,
    private val organizationMapper: OrganizationMapper
) : OrganizationService {
    override fun getOrganizationById(requestingUserEmail: String, organizationId: String): OrganizationResponseDto {
        val organizationModel = this.organizationRepository.findById(organizationId)
        if (isNotOrganizationAdministrator(organizationModel, requestingUserEmail) && !isOrganizationMember(organizationModel, requestingUserEmail)) {
            val organizationResponseDto = getEmptyOrganizationResponseDto()
            organizationResponseDto.httpStatus = HttpStatus.FORBIDDEN
            return organizationResponseDto
        }
        if (organizationModel != null) {
            val organizationResponseDto = this.organizationMapper.organizationModelToOrganizationResponseDto(organizationModel)
            if (organizationResponseDto != null) {
                organizationResponseDto.httpStatus = HttpStatus.OK
                return organizationResponseDto
            }
        }
        val organizationResponseDto = getEmptyOrganizationResponseDto()
        organizationResponseDto.httpStatus = HttpStatus.NOT_FOUND
        return organizationResponseDto
    }

    private fun isNotOrganizationAdministrator(
        organizationModel: OrganizationModel?,
        requestingUserEmail: String
    ): Boolean {
        return organizationModel?.administratorEmails?.contains(requestingUserEmail) ?: true
    }

    private fun isOrganizationMember(
        organizationModel: OrganizationModel?,
        requestingUserEmail: String
    ): Boolean {
        return organizationModel?.memberEmails?.contains(requestingUserEmail) ?: false
    }

    private fun getEmptyOrganizationResponseDto(): OrganizationResponseDto = OrganizationResponseDto(null, null, null, null)

}