package io.jonathanlee.sparrowexpressapikotlin.controller.organization

import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationEmailRequestDto
import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationMembershipDto
import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationMembershipRequestsContainerDto
import io.jonathanlee.sparrowexpressapikotlin.enums.organization.OrganizationMembershipStatus
import io.jonathanlee.sparrowexpressapikotlin.service.organization.OrganizationMembershipRequestService
import io.jonathanlee.sparrowexpressapikotlin.service.profile.ActiveProfileService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User

class OrganizationMembershipRequestControllerTest {

    @Mock
    private lateinit var activeProfileService: ActiveProfileService

    @Mock
    private lateinit var organizationMembershipRequestService: OrganizationMembershipRequestService

    @InjectMocks
    private lateinit var controller: OrganizationMembershipRequestController

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun testGetOrganizationMembershipRequests() {
        // Mock the authentication token
        val authenticationToken = mockAuthenticationToken("test@example.com", null)

        // Mock the necessary dependencies
        val requestingUserEmail = "test@example.com"
        val organizationId = "org123"
        val expectedContainerDto = OrganizationMembershipRequestsContainerDto(listOf())
        expectedContainerDto.httpStatus = HttpStatus.OK
        `when`(activeProfileService.isLocalActiveProfile()).thenReturn(true)
        `when`(organizationMembershipRequestService.getRequestsToJoinOrganization(requestingUserEmail, organizationId)).thenReturn(expectedContainerDto)

        // Call the controller method
        val responseEntity = controller.getOrganizationMembershipRequests(authenticationToken, organizationId)

        // Assert the response
        assertNotNull(responseEntity)
        assertEquals(HttpStatus.OK, responseEntity.statusCode)
        assertEquals(expectedContainerDto, responseEntity.body)
    }

    @Test
    fun testGetOrganizationMembershipRequests_NullToken() {
        // Mock the authentication token
        val authenticationToken = null

        // Mock the necessary dependencies
        val requestingUserEmail = "test@example.com"
        val organizationId = "org123"
        val expectedContainerDto = OrganizationMembershipRequestsContainerDto(listOf())
        `when`(activeProfileService.isLocalActiveProfile()).thenReturn(false)
        `when`(organizationMembershipRequestService.getRequestsToJoinOrganization(requestingUserEmail, organizationId)).thenReturn(expectedContainerDto)

        // Call the controller method
        val responseEntity = controller.getOrganizationMembershipRequests(authenticationToken, organizationId)

        // Assert the response
        assertNotNull(responseEntity)
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.statusCode)
        assertNull(responseEntity.body)
    }

    @Test
    fun testGetOrganizationMembershipRequests_NullRequestingUserEmail() {
        // Mock the authentication token
        val authenticationToken = mockAuthenticationToken("test@example.com", mapOf())

        // Mock the necessary dependencies
        val requestingUserEmail = "test@example.com"
        val organizationId = "org123"
        val expectedContainerDto = OrganizationMembershipRequestsContainerDto(listOf())
        `when`(activeProfileService.isLocalActiveProfile()).thenReturn(false)
        `when`(organizationMembershipRequestService.getRequestsToJoinOrganization(requestingUserEmail, organizationId)).thenReturn(expectedContainerDto)

        // Call the controller method
        val responseEntity = controller.getOrganizationMembershipRequests(authenticationToken, organizationId)

        // Assert the response
        assertNotNull(responseEntity)
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.statusCode)
        assertNull(responseEntity.body)
    }

    @Test
    fun testRequestToJoinOrganization() {
        // Mock the authentication token
        val authenticationToken = mockAuthenticationToken("test@example.com", null)

        // Mock the necessary dependencies
        val requestingUserEmail = "test@example.com"
        `when`(activeProfileService.isLocalActiveProfile()).thenReturn(true)
        val emailRequestDto = OrganizationEmailRequestDto("12345", requestingUserEmail)
        val expectedMembershipDto = OrganizationMembershipDto(OrganizationMembershipStatus.APPROVED)
        expectedMembershipDto.httpStatus = HttpStatus.OK
        `when`(organizationMembershipRequestService.requestToJoinOrganization(requestingUserEmail, emailRequestDto.email, emailRequestDto.organizationId)).thenReturn(expectedMembershipDto)

        // Call the controller method
        val responseEntity = controller.requestToJoinOrganization(authenticationToken, emailRequestDto)

        // Assert the response
        assertNotNull(responseEntity)
        assertEquals(HttpStatus.OK, responseEntity.statusCode)
        assertEquals(expectedMembershipDto, responseEntity.body)
    }

    @Test
    fun testRequestToJoinOrganization_NullToken() {
        // Mock the authentication token
        val authenticationToken = null

        // Mock the necessary dependencies
        val requestingUserEmail = "test@example.com"
        `when`(activeProfileService.isLocalActiveProfile()).thenReturn(false)
        val emailRequestDto = OrganizationEmailRequestDto("12345", requestingUserEmail)
        val expectedMembershipDto = OrganizationMembershipDto(OrganizationMembershipStatus.APPROVED)
        `when`(organizationMembershipRequestService.requestToJoinOrganization(requestingUserEmail, emailRequestDto.email, emailRequestDto.organizationId)).thenReturn(expectedMembershipDto)

        // Call the controller method
        val responseEntity = controller.requestToJoinOrganization(authenticationToken, emailRequestDto)

        // Assert the response
        assertNotNull(responseEntity)
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.statusCode)
        assertNull(responseEntity.body)
    }

    @Test
    fun testApproveRequestToJoinOrganization() {
        // Mock the authentication token
        val authenticationToken = mockAuthenticationToken("test@example.com", null)

        // Mock the necessary dependencies
        val requestingUserEmail = "test@example.com"
        `when`(activeProfileService.isLocalActiveProfile()).thenReturn(true)
        val organizationMembershipRequestId = "request123"
        val expectedMembershipDto = OrganizationMembershipDto(OrganizationMembershipStatus.APPROVED)
        expectedMembershipDto.httpStatus = HttpStatus.OK
        `when`(organizationMembershipRequestService.approveRequestToJoinOrganization(requestingUserEmail, organizationMembershipRequestId)).thenReturn(expectedMembershipDto)

        // Call the controller method
        val responseEntity = controller.approveRequestToJoinOrganization(authenticationToken, organizationMembershipRequestId)

        // Assert the response
        assertNotNull(responseEntity)
        assertEquals(HttpStatus.OK, responseEntity.statusCode)
        assertEquals(expectedMembershipDto, responseEntity.body)
    }

    @Test
    fun testApproveRequestToJoinOrganization_NullToken() {
        // Mock the authentication token
        val authenticationToken = null

        // Mock the necessary dependencies
        val requestingUserEmail = "test@example.com"
        `when`(activeProfileService.isLocalActiveProfile()).thenReturn(false)
        val organizationMembershipRequestId = "request123"
        val expectedMembershipDto = OrganizationMembershipDto(OrganizationMembershipStatus.APPROVED)
        `when`(organizationMembershipRequestService.approveRequestToJoinOrganization(requestingUserEmail, organizationMembershipRequestId)).thenReturn(expectedMembershipDto)

        // Call the controller method
        val responseEntity = controller.approveRequestToJoinOrganization(authenticationToken, organizationMembershipRequestId)

        // Assert the response
        assertNotNull(responseEntity)
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.statusCode)
        assertNull(responseEntity.body)
    }

    private fun mockAuthenticationToken(email: String, attributeMap: Map<String, String>?): OAuth2AuthenticationToken {
        val token: OAuth2AuthenticationToken = Mockito.mock(OAuth2AuthenticationToken::class.java)
        val principal: OAuth2User = Mockito.mock(OAuth2User::class.java)
        `when`(principal.name).thenReturn(email)
        `when`(token.principal).thenReturn(principal)
        `when`(token.principal.attributes).thenReturn(attributeMap ?: mapOf("email" to email))
        return token
    }
}
