package io.jonathanlee.sparrowexpressapikotlin.service.organization.impl

import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationRequestDto
import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationResponseDto
import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationSnippetResponseDto
import io.jonathanlee.sparrowexpressapikotlin.exception.BadRequestException
import io.jonathanlee.sparrowexpressapikotlin.mapper.organization.OrganizationMapper
import io.jonathanlee.sparrowexpressapikotlin.model.organization.OrganizationModel
import io.jonathanlee.sparrowexpressapikotlin.repository.organization.OrganizationRepository
import io.jonathanlee.sparrowexpressapikotlin.service.organization.OrganizationService
import io.jonathanlee.sparrowexpressapikotlin.service.random.RandomService
import io.jonathanlee.sparrowexpressapikotlin.util.ListUtil
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class OrganizationServiceImpl(
    private val organizationRepository: OrganizationRepository,
    private val organizationMapper: OrganizationMapper,
    private val randomService: RandomService
) : OrganizationService {

    override fun getOrganizationById(requestingUserEmail: String, organizationId: String): OrganizationResponseDto {
        val organizationModel = this.organizationRepository.findById(organizationId)
        if (organizationModel == null) {
            val organizationResponseDto = getEmptyOrganizationResponseDto()
            organizationResponseDto.httpStatus = HttpStatus.NOT_FOUND
            return organizationResponseDto
        }
        if (isNotOrganizationAdministrator(organizationModel, requestingUserEmail) && !isOrganizationMember(organizationModel, requestingUserEmail)) {
            val organizationResponseDto = getEmptyOrganizationResponseDto()
            organizationResponseDto.httpStatus = HttpStatus.FORBIDDEN
            return organizationResponseDto
        }
        val organizationResponseDto = this.organizationMapper.organizationModelToOrganizationResponseDto(organizationModel)
        return if (organizationResponseDto != null) {
            organizationResponseDto.httpStatus = HttpStatus.OK
            organizationResponseDto
        } else {
            getEmptyOrganizationResponseDto()
        }
    }

    override fun getOrganizationSnippetById(
        organizationId: String
    ): OrganizationSnippetResponseDto? {
        val organizationModel = this.organizationRepository.findById(organizationId)
        if (organizationModel == null) {
            val organizationResponseDto = getEmptyOrganizationSnippetResponseDto()
            organizationResponseDto.httpStatus = HttpStatus.NOT_FOUND
            return organizationResponseDto
        }
        val organizationResponseDto = this.organizationMapper.organizationModelToOrganizationSnippetResponseDto(organizationModel)
        return if (organizationResponseDto != null) {
            organizationResponseDto.httpStatus = HttpStatus.OK
            organizationResponseDto
        } else {
            getEmptyOrganizationSnippetResponseDto()
        }
    }

    override fun createOrganization(
        requestingUserEmail: String,
        organizationRequestDto: OrganizationRequestDto
    ): OrganizationResponseDto? {
        val organizationModel = OrganizationModel(
            ObjectId.get(),
            this.randomService.generateNewId(),
            organizationRequestDto.name,
            ListUtil.removeDuplicatesFromList(organizationRequestDto.administratorEmails),
            ListUtil.removeDuplicatesFromList(organizationRequestDto.memberEmails)
        )
        if (!organizationModel.administratorEmails.contains(requestingUserEmail)) {
            organizationModel.administratorEmails.add(requestingUserEmail)
        }
        val organizationResponseDto = this.organizationMapper.organizationModelToOrganizationResponseDto(
            this.organizationRepository.save(organizationModel)
        ) ?: return getEmptyOrganizationResponseDto()
        organizationResponseDto.httpStatus = HttpStatus.CREATED
        return organizationResponseDto
    }

    override fun removeOrganizationAdministrator(
        requestingUserEmail: String,
        organizationId: String,
        administratorEmailToRemove: String
    ): OrganizationResponseDto? {
        val organizationModel = this.organizationRepository.findById(organizationId)
        if (organizationModel == null) {
            val organizationResponseDto = getEmptyOrganizationResponseDto()
            organizationResponseDto.httpStatus = HttpStatus.NOT_FOUND
            return organizationResponseDto
        }
        if (isNotOrganizationAdministrator(organizationModel, requestingUserEmail)) {
            val organizationResponseDto = getEmptyOrganizationResponseDto()
            organizationResponseDto.httpStatus = HttpStatus.FORBIDDEN
            return organizationResponseDto
        }
        if (!organizationModel.administratorEmails.contains(administratorEmailToRemove)) {
            throw BadRequestException(ADMINISTRATOR_EMAIL_TO_REMOVE, "$administratorEmailToRemove is not an administrator of organization with ID: $organizationId")
        }
        if (organizationModel.administratorEmails.size <= 1) {
            throw BadRequestException(ADMINISTRATOR_EMAIL_TO_REMOVE, "$administratorEmailToRemove is the last remaining administrator of organization with ID: $organizationId")
        }
        organizationModel.administratorEmails.remove(administratorEmailToRemove)
        val organizationResponseDto = this.organizationMapper.organizationModelToOrganizationResponseDto(
            this.organizationRepository.save(organizationModel)
        ) ?: return getEmptyOrganizationResponseDto()
        organizationResponseDto.httpStatus = HttpStatus.OK
        return organizationResponseDto
    }

    override fun removeOrganizationMember(
        requestingUserEmail: String,
        organizationId: String,
        memberEmailToRemove: String
    ): OrganizationResponseDto? {
        val organizationModel = this.organizationRepository.findById(organizationId)
        if (organizationModel == null) {
            val organizationResponseDto = getEmptyOrganizationResponseDto()
            organizationResponseDto.httpStatus = HttpStatus.NOT_FOUND
            return organizationResponseDto
        }
        if (isNotOrganizationAdministrator(organizationModel, requestingUserEmail)) {
            val organizationResponseDto = getEmptyOrganizationResponseDto()
            organizationResponseDto.httpStatus = HttpStatus.FORBIDDEN
            return organizationResponseDto
        }
        if (!organizationModel.memberEmails.contains(memberEmailToRemove)) {
            throw BadRequestException(MEMBER_EMAIL_TO_REMOVE, "$memberEmailToRemove is not a member of organization with ID: $organizationId")
        }
        organizationModel.memberEmails.remove(memberEmailToRemove)
        val organizationResponseDto = this.organizationMapper.organizationModelToOrganizationResponseDto(
            this.organizationRepository.save(organizationModel)
        ) ?: return getEmptyOrganizationResponseDto()
        organizationResponseDto.httpStatus = HttpStatus.OK
        return organizationResponseDto
    }

    override fun updateOrganizationAdministratorToJoinAsMember(
        requestingUserEmail: String,
        organizationId: String,
        administratorEmailToAddAsMember: String
    ): OrganizationResponseDto? {
        val organizationModel = this.organizationRepository.findById(organizationId)
        if (organizationModel == null) {
            val organizationResponseDto = getEmptyOrganizationResponseDto()
            organizationResponseDto.httpStatus = HttpStatus.NOT_FOUND
            return organizationResponseDto
        }
        if (isNotOrganizationAdministrator(organizationModel, requestingUserEmail)) {
            val organizationResponseDto = getEmptyOrganizationResponseDto()
            organizationResponseDto.httpStatus = HttpStatus.NOT_FOUND
            return organizationResponseDto
        }
        if (organizationModel.memberEmails.contains(administratorEmailToAddAsMember)) {
            throw BadRequestException(ADMINISTRATOR_EMAIL_TO_ADD_AS_MEMBER, "$administratorEmailToAddAsMember is already a member of organization with ID: $organizationId")
        }
        if (isNotOrganizationAdministrator(organizationModel, administratorEmailToAddAsMember)) {
            throw BadRequestException(ADMINISTRATOR_EMAIL_TO_ADD_AS_MEMBER, "$administratorEmailToAddAsMember is not an administrator of organization with ID: $organizationId")
        }
        organizationModel.memberEmails.add(administratorEmailToAddAsMember)
        val organizationResponseDto = this.organizationMapper.organizationModelToOrganizationResponseDto(
            this.organizationRepository.save(organizationModel)
        ) ?: return getEmptyOrganizationResponseDto()
        organizationResponseDto.httpStatus = HttpStatus.OK
        return organizationResponseDto
    }


    private fun isNotOrganizationAdministrator(
        organizationModel: OrganizationModel?,
        requestingUserEmail: String
    ): Boolean {
        return (organizationModel?.administratorEmails?.contains(requestingUserEmail) == false)
    }

    private fun isOrganizationMember(
        organizationModel: OrganizationModel?,
        requestingUserEmail: String
    ): Boolean {
        return organizationModel?.memberEmails?.contains(requestingUserEmail) ?: false
    }

    private fun getEmptyOrganizationResponseDto(): OrganizationResponseDto = OrganizationResponseDto(null, null, null, null)

    private fun getEmptyOrganizationSnippetResponseDto(): OrganizationSnippetResponseDto = OrganizationSnippetResponseDto(null, null)

    companion object {
        const val ADMINISTRATOR_EMAIL_TO_REMOVE = "Administrator e-mail to remove"
        const val MEMBER_EMAIL_TO_REMOVE = "Member e-mail to remove"
        const val ADMINISTRATOR_EMAIL_TO_ADD_AS_MEMBER = "Administrator e-mail to add as member"
    }

}