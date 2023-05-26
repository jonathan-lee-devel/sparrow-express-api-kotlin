package io.jonathanlee.sparrowexpressapikotlin.service.organization.impl

import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationMembershipRequestDto
import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationMembershipRequestResponseDto
import io.jonathanlee.sparrowexpressapikotlin.enums.organization.OrganizationMembershipStatus
import io.jonathanlee.sparrowexpressapikotlin.exception.BadRequestException
import io.jonathanlee.sparrowexpressapikotlin.mapper.organization.OrganizationMembershipRequestMapper
import io.jonathanlee.sparrowexpressapikotlin.model.organization.OrganizationMembershipRequestModel
import io.jonathanlee.sparrowexpressapikotlin.model.organization.OrganizationModel
import io.jonathanlee.sparrowexpressapikotlin.repository.organization.OrganizationMembershipRequestRepository
import io.jonathanlee.sparrowexpressapikotlin.repository.organization.OrganizationRepository
import io.jonathanlee.sparrowexpressapikotlin.service.random.RandomService
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.http.HttpStatus

class OrganizationMembershipRequestServiceImplTest {

    @Mock
    private lateinit var organizationRepository: OrganizationRepository

    @Mock
    private lateinit var organizationMembershipRequestRepository: OrganizationMembershipRequestRepository

    @Mock
    private lateinit var organizationMembershipRequestMapper: OrganizationMembershipRequestMapper

    @Mock
    private lateinit var randomService: RandomService

    private lateinit var service: OrganizationMembershipRequestServiceImpl

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        service = OrganizationMembershipRequestServiceImpl(
            organizationRepository,
            organizationMembershipRequestRepository,
            organizationMembershipRequestMapper,
            randomService
        )
    }

    @Test
    fun testRequestToJoinOrganization_WhenOrganizationDoesNotExist_ReturnsBadRequest() {
        // Arrange
        val requestingUserEmail = "requesting@example.com"
        val requesterEmail = "requester@example.com"
        val organizationId = "12345"
        `when`(organizationRepository.findById(organizationId)).thenReturn(null)

        // Act
        val result = service.requestToJoinOrganization(requestingUserEmail, requesterEmail, organizationId)

        // Assert
        assertEquals(OrganizationMembershipStatus.ORGANIZATION_DOES_NOT_EXIST, result?.status)
        assertEquals(HttpStatus.BAD_REQUEST, result?.httpStatus)
    }

    @Test
    fun testRequestToJoinOrganization_WhenRequestingUserEmailIsNotAdministrator_ReturnsForbidden() {
        // Arrange
        val requestingUserEmail = "requesting@example.com"
        val requesterEmail = "requester@example.com"
        val organizationId = "12345"
        val organizationModel = OrganizationModel(
            ObjectId.get(),
            "12345",
            "Name",
            mutableListOf(),
            mutableListOf()
        )
        `when`(organizationRepository.findById(organizationId)).thenReturn(organizationModel)

        // Act
        val result = service.requestToJoinOrganization(requestingUserEmail, requesterEmail, organizationId)

        // Assert
        assertEquals(OrganizationMembershipStatus.FORBIDDEN, result?.status)
        assertEquals(HttpStatus.FORBIDDEN, result?.httpStatus)
    }

    @Test
    fun testRequestToJoinOrganization_WhenRequesterIsAlreadyMember_ReturnsBadRequest() {
        // Arrange
        val requestingUserEmail = "requesting@example.com"
        val requesterEmail = "requester@example.com"
        val organizationId = "12345"
        val organizationModel = OrganizationModel(
            ObjectId.get(),
            "12345",
            "Name",
            mutableListOf(requestingUserEmail),
            mutableListOf(requesterEmail)
        )
        `when`(organizationRepository.findById(organizationId)).thenReturn(organizationModel)

        // Act
        val result = service.requestToJoinOrganization(requestingUserEmail, requesterEmail, organizationId)

        // Assert
        assertEquals(OrganizationMembershipStatus.USER_ALREADY_MEMBER, result?.status)
        assertEquals(HttpStatus.BAD_REQUEST, result?.httpStatus)
    }

    @Test
    fun testRequestToJoinOrganization_WhenRequesterIsAlreadyRequested_ReturnsConflict() {
        // Arrange
        val requestingUserEmail = "requesting@example.com"
        val requesterEmail = "requester@example.com"
        val organizationId = "12345"
        val organizationModel = OrganizationModel(
            ObjectId.get(),
            "12345",
            "Name",
            mutableListOf(requestingUserEmail),
            mutableListOf()
        )
        `when`(organizationRepository.findById(organizationId)).thenReturn(organizationModel)
        val organizationMembershipRequestModel = OrganizationMembershipRequestModel(
            ObjectId.get(),
            "12345",
            organizationId,
            requesterEmail,
            false,
            null
        )
        `when`(organizationMembershipRequestRepository.findByRequestingUserEmailIs(requesterEmail)).thenReturn(organizationMembershipRequestModel)

        // Act
        val result = service.requestToJoinOrganization(requestingUserEmail, requesterEmail, organizationId)

        // Assert
        assertEquals(OrganizationMembershipStatus.REQUEST_ALREADY_EXISTS, result?.status)
        assertEquals(HttpStatus.CONFLICT, result?.httpStatus)
    }

    @Test
    fun testRequestToJoinOrganization_WhenRequesterIsValid_ReturnsAwaitingApproval() {
        // Arrange
        val requestingUserEmail = "requesting@example.com"
        val requesterEmail = "requester@example.com"
        val organizationId = "12345"
        val organizationModel = OrganizationModel(
            ObjectId.get(),
            "12345",
            "Name",
            mutableListOf(requestingUserEmail),
            mutableListOf()
        )
        `when`(organizationRepository.findById(organizationId)).thenReturn(organizationModel)
        `when`(organizationMembershipRequestRepository.findByRequestingUserEmailIs(requesterEmail)).thenReturn(null)
        `when`(randomService.generateNewId()).thenReturn("12345")

        // Act
        val result = service.requestToJoinOrganization(requestingUserEmail, requesterEmail, organizationId)

        // Assert
        verify(organizationMembershipRequestRepository, times(1)).save(any(OrganizationMembershipRequestModel::class.java))
        assertEquals(OrganizationMembershipStatus.AWAITING_APPROVAL, result?.status)
        assertEquals(HttpStatus.OK, result?.httpStatus)
    }

    @Test
    fun testGetRequestsToJoinOrganization_WhenOrganizationNotFound_ReturnsNotFound() {
        // Arrange
        val requestingUserEmail = "requesting@example.com"
        val organizationId = "12345"
        `when`(organizationRepository.findById(organizationId)).thenReturn(null)

        // Act
        val result = service.getRequestsToJoinOrganization(requestingUserEmail, organizationId)

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, result?.httpStatus)
        assertEquals(null, result?.organizationMembershipRequests)
    }

    @Test
    fun testGetRequestsToJoinOrganization_WhenRequestingUserNotAdministrator_ReturnsForbidden() {
        // Arrange
        val requestingUserEmail = "requesting@example.com"
        val organizationId = "12345"
        val organizationModel = OrganizationModel(
            ObjectId.get(),
            organizationId,
            "Name",
            mutableListOf(),
            mutableListOf()
        )
        `when`(organizationRepository.findById(organizationId)).thenReturn(organizationModel)

        // Act
        val result = service.getRequestsToJoinOrganization(requestingUserEmail, organizationId)

        //Assert
        assertEquals(HttpStatus.FORBIDDEN, result?.httpStatus)
        assertEquals(null, result?.organizationMembershipRequests)
    }

    @Test
    fun testGetRequestsToJoinOrganization_WhenRequestValid_ReturnsStatusOkWithData() {
        // Arrange
        val requestingUserEmail = "requesting@example.com"
        val organizationId = "12345"
        val organizationModel = OrganizationModel(
            ObjectId.get(),
            organizationId,
            "Name",
            mutableListOf(requestingUserEmail),
            mutableListOf()
        )
        `when`(organizationRepository.findById(organizationId)).thenReturn(organizationModel)
        val organizationMembershipRequestModels = listOf(
            OrganizationMembershipRequestModel(
                ObjectId.get(),
                "12345",
                organizationId,
                requestingUserEmail,
                false,
                null
            )
        )
        `when`(organizationMembershipRequestRepository.findOrganizationMembershipRequestModelsByOrganizationIdIs(organizationId))
            .thenReturn(organizationMembershipRequestModels)
        val organizationMembershipRequestResponseDtos = listOf(
            OrganizationMembershipRequestResponseDto(
                "12345",
                organizationId,
                requestingUserEmail,
                false,
                null
            )
        )
        `when`(organizationMembershipRequestMapper.organizationMembershipRequestListToOrganizationMembershipRequestDtoList(organizationMembershipRequestModels))
            .thenReturn(organizationMembershipRequestResponseDtos)

        // Act
        val result = service.getRequestsToJoinOrganization(requestingUserEmail, organizationId)

        //Assert
        assertEquals(HttpStatus.OK, result?.httpStatus)
        assertEquals(organizationMembershipRequestResponseDtos, result?.organizationMembershipRequests)
    }

    @Test
    fun testApproveRequestToJoinOrganization_WhenOrganizationMembershipRequestNotFound_ReturnsNotFound() {
        // Arrange
        val organizationMembershipRequestId = "12345"
        val requestingUserEmail = "requesting@example.com"
        val organizationMembershipRequestDto = OrganizationMembershipRequestDto(organizationMembershipRequestId)

        `when`(organizationMembershipRequestRepository.findById(organizationMembershipRequestId)).thenReturn(null)

        // Act
        val result = service.approveRequestToJoinOrganization(requestingUserEmail, organizationMembershipRequestId)

        // Assert
        assertEquals(OrganizationMembershipStatus.NOT_FOUND, result?.status)
        assertEquals(HttpStatus.NOT_FOUND, result?.httpStatus)
    }

    @Test
    fun testApproveRequestToJoinOrganization_WhenOrganizationNotFound_ReturnsFailure() {
        // Arrange
        val organizationMembershipRequestId = "12345"
        val requestingUserEmail = "requesting@example.com"
        val organizationId = "12345"

        val organizationMembershipRequestModel = OrganizationMembershipRequestModel(ObjectId.get(), organizationMembershipRequestId, organizationId, requestingUserEmail, false, null)
        `when`(organizationMembershipRequestRepository.findById(organizationMembershipRequestId)).thenReturn(organizationMembershipRequestModel)

        `when`(organizationRepository.findById(organizationId)).thenReturn(null)

        // Act
        val result = service.approveRequestToJoinOrganization(requestingUserEmail, organizationMembershipRequestId)

        // Assert
        assertEquals(OrganizationMembershipStatus.FAILURE, result?.status)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result?.httpStatus)
    }

    @Test
    fun testApproveRequestToJoinOrganization_WhenRequestingUserEmailNotAdministrator_ReturnsForbidden() {
        // Arrange
        val organizationMembershipRequestId = "12345"
        val requestingUserEmail = "requesting@example.com"
        val organizationId = "12345"

        val organizationMembershipRequestModel = OrganizationMembershipRequestModel(ObjectId.get(), organizationMembershipRequestId, organizationId, requestingUserEmail, false, null)
        `when`(organizationMembershipRequestRepository.findById(organizationMembershipRequestId)).thenReturn(organizationMembershipRequestModel)

        val organizationModel = OrganizationModel(ObjectId.get(), organizationId, "Organization", mutableListOf(), mutableListOf())
        `when`(organizationRepository.findById(organizationId)).thenReturn(organizationModel)

        // Act
        val result = service.approveRequestToJoinOrganization(requestingUserEmail, organizationMembershipRequestId)

        // Assert
        assertEquals(OrganizationMembershipStatus.FORBIDDEN, result?.status)
        assertEquals(HttpStatus.FORBIDDEN, result?.httpStatus)
    }

    @Test
    fun testApproveRequestToJoinOrganization_WhenRequestingUserEmailAlreadyMember_ReturnsBadRequest() {
        // Arrange
        val organizationMembershipRequestId = "12345"
        val requestingUserEmail = "requesting@example.com"
        val organizationId = "12345"

        val organizationMembershipRequestModel = OrganizationMembershipRequestModel(ObjectId.get(), organizationMembershipRequestId, organizationId, requestingUserEmail, false, null)
        `when`(organizationMembershipRequestRepository.findById(organizationMembershipRequestId)).thenReturn(organizationMembershipRequestModel)

        val organizationModel = OrganizationModel(ObjectId.get(), organizationId, "Organization", mutableListOf(requestingUserEmail), mutableListOf())
        `when`(organizationRepository.findById(organizationId)).thenReturn(organizationModel)

        try {
            // Act
            service.approveRequestToJoinOrganization(requestingUserEmail, organizationMembershipRequestId)
        } catch (exception: BadRequestException) {
            // Assert
            assertEquals("Requesting User E-mail", exception.field)
            assertEquals("Requesting user e-mail: $requestingUserEmail is already a member of organization with ID: ${organizationMembershipRequestModel.organizationId}", exception.message)
        }
    }

    @Test
    fun testApproveRequestToJoinOrganization_WhenRequestValid_ReturnsApproved() {
        // Arrange
        val organizationMembershipRequestId = "12345"
        val requestingUserEmail = "requesting@example.com"
        val organizationId = "12345"

        val organizationMembershipRequestModel = OrganizationMembershipRequestModel(ObjectId.get(), organizationMembershipRequestId, organizationId, requestingUserEmail, false, null)
        `when`(organizationMembershipRequestRepository.findById(organizationMembershipRequestId)).thenReturn(organizationMembershipRequestModel)

        val organizationModel = OrganizationModel(ObjectId.get(), organizationId, "Organization", mutableListOf(requestingUserEmail), mutableListOf())
        `when`(organizationRepository.findById(organizationId)).thenReturn(organizationModel)

        // Act
        val result = service.approveRequestToJoinOrganization(requestingUserEmail, organizationMembershipRequestId)

        // Assert
        assertEquals(mutableListOf(requestingUserEmail), organizationModel.memberEmails)
        assertTrue(organizationMembershipRequestModel.isApproved)
        assertEquals(requestingUserEmail, organizationMembershipRequestModel.approvingAdministratorEmail)
        verify(organizationRepository, times(1)).save(organizationModel)
        verify(organizationMembershipRequestRepository, times(1)).save(organizationMembershipRequestModel)
        assertEquals(OrganizationMembershipStatus.APPROVED, result?.status)
        assertEquals(HttpStatus.OK, result?.httpStatus)
    }

}
