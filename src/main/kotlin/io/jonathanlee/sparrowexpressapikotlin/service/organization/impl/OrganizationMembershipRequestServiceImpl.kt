package io.jonathanlee.sparrowexpressapikotlin.service.organization.impl

import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationMembershipDto
import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationMembershipRequestsContainerDto
import io.jonathanlee.sparrowexpressapikotlin.enums.organization.OrganizationMembershipStatus
import io.jonathanlee.sparrowexpressapikotlin.exception.BadRequestException
import io.jonathanlee.sparrowexpressapikotlin.mapper.organization.OrganizationMembershipRequestMapper
import io.jonathanlee.sparrowexpressapikotlin.model.organization.OrganizationMembershipRequestModel
import io.jonathanlee.sparrowexpressapikotlin.repository.organization.OrganizationMembershipRequestRepository
import io.jonathanlee.sparrowexpressapikotlin.repository.organization.OrganizationRepository
import io.jonathanlee.sparrowexpressapikotlin.service.organization.OrganizationMembershipRequestService
import io.jonathanlee.sparrowexpressapikotlin.service.random.RandomService
import org.bson.types.ObjectId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class OrganizationMembershipRequestServiceImpl(
    private val organizationRepository: OrganizationRepository,
    private val organizationMembershipRequestRepository: OrganizationMembershipRequestRepository,
    private val organizationMembershipRequestMapper: OrganizationMembershipRequestMapper,
    private val randomService: RandomService
) : OrganizationMembershipRequestService {

    private val logger: Logger = LoggerFactory.getLogger(OrganizationMembershipRequestServiceImpl::class.java)

    override fun requestToJoinOrganization(
        requestingUserEmail: String,
        requesterEmail: String,
        organizationId: String
    ): OrganizationMembershipDto? {
        logger.info("Request from $requestingUserEmail for $requesterEmail to join organization with ID: $organizationId")
        val organizationModel = this.organizationRepository.findById(organizationId)
            ?: return getOrganizationMembershipDto(OrganizationMembershipStatus.ORGANIZATION_DOES_NOT_EXIST, HttpStatus.BAD_REQUEST)
        if (!organizationModel.administratorEmails.contains(requestingUserEmail)) {
            return getOrganizationMembershipDto(OrganizationMembershipStatus.FORBIDDEN, HttpStatus.FORBIDDEN)
        }
        if (organizationModel.administratorEmails.contains(requesterEmail) || organizationModel.memberEmails.contains(requesterEmail)) {
            return getOrganizationMembershipDto(OrganizationMembershipStatus.USER_ALREADY_MEMBER, HttpStatus.BAD_REQUEST)
        }
        val organizationMembershipRequestModel = this.organizationMembershipRequestRepository.findByRequestingUserEmailIs(requesterEmail)
        if (organizationMembershipRequestModel != null) {
            return getOrganizationMembershipDto(OrganizationMembershipStatus.REQUEST_ALREADY_EXISTS, HttpStatus.CONFLICT)
        }
        val newOrganizationMembershipRequestModel = OrganizationMembershipRequestModel(
            ObjectId.get(),
            this.randomService.generateNewId(),
            organizationId,
            requesterEmail,
            false,
            null
        )
        this.organizationMembershipRequestRepository.save(newOrganizationMembershipRequestModel)
        return getOrganizationMembershipDto(OrganizationMembershipStatus.AWAITING_APPROVAL, HttpStatus.OK)
    }

    override fun getRequestsToJoinOrganization(
        requestingUserEmail: String,
        organizationId: String
    ): OrganizationMembershipRequestsContainerDto? {
        logger.info("Request for $requestingUserEmail to get requests to join organization with ID: $organizationId")
        val organizationModel = this.organizationRepository.findById(organizationId)
        if (organizationModel == null) {
            val organizationMembershipRequestsResponseDto = OrganizationMembershipRequestsContainerDto(null)
            organizationMembershipRequestsResponseDto.httpStatus = HttpStatus.NOT_FOUND
            return organizationMembershipRequestsResponseDto
        }
        if (!organizationModel.administratorEmails.contains(requestingUserEmail)) {
            val organizationMembershipRequestsResponseDto = OrganizationMembershipRequestsContainerDto(null)
            organizationMembershipRequestsResponseDto.httpStatus = HttpStatus.FORBIDDEN
            return organizationMembershipRequestsResponseDto
        }
        val organizationMembershipRequestModels = this.organizationMembershipRequestRepository.findOrganizationMembershipRequestModelsByOrganizationIdIs(organizationId)
        val organizationMembershipRequestDtos = this.organizationMembershipRequestMapper.organizationMembershipRequestListToOrganizationMembershipRequestDtoList(organizationMembershipRequestModels)
        val organizationMembershipRequestsResponseDto = OrganizationMembershipRequestsContainerDto(organizationMembershipRequestDtos)
        organizationMembershipRequestsResponseDto.httpStatus = HttpStatus.OK
        return organizationMembershipRequestsResponseDto
    }

    override fun approveRequestToJoinOrganization(
        requestingUserEmail: String,
        organizationMembershipRequestId: String,
    ): OrganizationMembershipDto? {
        logger.info("Request for $requestingUserEmail to approve organization membership request with ID: $organizationMembershipRequestId")
        val organizationMembershipRequestModel = this.organizationMembershipRequestRepository.findById(organizationMembershipRequestId)
            ?: return getOrganizationMembershipDto(OrganizationMembershipStatus.NOT_FOUND, HttpStatus.NOT_FOUND)
        val organizationModel = this.organizationRepository.findById(organizationMembershipRequestModel.organizationId)
        if (organizationModel == null) {
            logger.error("Organization membership request with ID: $organizationMembershipRequestId references non-existent organization with ID: ${organizationMembershipRequestModel.organizationId}")
            logger.error("Organization membership request with ID: $organizationMembershipRequestId references non-existent organization with ID: ${organizationMembershipRequestModel.organizationId}")
            return OrganizationMembershipDto(OrganizationMembershipStatus.FAILURE) // Return 500 internal server error
        }
        if (!organizationModel.administratorEmails.contains(requestingUserEmail)) {
            return getOrganizationMembershipDto(OrganizationMembershipStatus.FORBIDDEN, HttpStatus.FORBIDDEN)
        }
        if (organizationModel.memberEmails.contains(organizationMembershipRequestModel.requestingUserEmail)) {
            throw BadRequestException("Requesting User E-mail", "Requesting user e-mail: ${organizationMembershipRequestModel.requestingUserEmail} is already a member of organization with ID: ${organizationMembershipRequestModel.organizationId}")
        }
        // Checks complete, therefore, request is approved and user is added as member
        organizationModel.memberEmails.add(organizationMembershipRequestModel.requestingUserEmail)
        organizationMembershipRequestModel.isApproved = true
        organizationMembershipRequestModel.approvingAdministratorEmail = requestingUserEmail
        this.organizationRepository.save(organizationModel)
        this.organizationMembershipRequestRepository.save(organizationMembershipRequestModel)
        return getOrganizationMembershipDto(OrganizationMembershipStatus.APPROVED, HttpStatus.OK)
    }

    private fun getOrganizationMembershipDto(organizationMembershipStatus: OrganizationMembershipStatus, httpStatus: HttpStatus): OrganizationMembershipDto {
        val organizationMembershipDto = OrganizationMembershipDto(organizationMembershipStatus)
        organizationMembershipDto.httpStatus = httpStatus
        return organizationMembershipDto
    }

}
