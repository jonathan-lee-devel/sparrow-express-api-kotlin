package io.jonathanlee.sparrowexpressapikotlin.controller.organization

import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationResponseDto
import io.jonathanlee.sparrowexpressapikotlin.service.organization.OrganizationService
import io.jonathanlee.sparrowexpressapikotlin.service.profile.ActiveProfileService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User

class OrganizationControllerTests {

    private lateinit var activeProfileService: ActiveProfileService

    private lateinit var organizationService: OrganizationService

    @Test
    fun `get organizationByID returns valid organization response DTO`() {
        activeProfileService = Mockito.mock(ActiveProfileService::class.java)
        Mockito.`when`(activeProfileService.isLocalActiveProfile()).thenReturn(true)

        val organizationResponseDto = OrganizationResponseDto("123", "Org", listOf("test@example.com"), listOf("test@example.com"))
        organizationResponseDto.httpStatus = HttpStatus.OK
        organizationService = Mockito.mock(OrganizationService::class.java)
        Mockito.`when`(organizationService.getOrganizationById("test@example.com", "123")).thenReturn(organizationResponseDto)

        val organizationController = OrganizationController(activeProfileService, organizationService)

        val expectedResponse = ResponseEntity.ok(organizationResponseDto)

        val oAuth2AuthenticationToken = mockToken("test@example.com")

        // Act
        val response = organizationController.getOrganizationById(oAuth2AuthenticationToken, "123")

        // Assert
        Assertions.assertEquals(expectedResponse, response)
    }

    @Test
    fun `get organizationById returns not found with response DTO`() {
        activeProfileService = Mockito.mock(ActiveProfileService::class.java)
        Mockito.`when`(activeProfileService.isLocalActiveProfile()).thenReturn(true)

        val organizationResponseDto = OrganizationResponseDto(null, null, null, null)
        organizationResponseDto.httpStatus = HttpStatus.NOT_FOUND
        organizationService = Mockito.mock(OrganizationService::class.java)
        Mockito.`when`(organizationService.getOrganizationById("test@example.com", "123")).thenReturn(organizationResponseDto)

        val organizationController = OrganizationController(activeProfileService, organizationService)

        val expectedResponse = ResponseEntity.status(HttpStatus.NOT_FOUND).body(organizationResponseDto)

        val oAuth2AuthenticationToken = mockToken("test@example.com")

        // Act
        val response = organizationController.getOrganizationById(oAuth2AuthenticationToken, "123")

        // Assert
        Assertions.assertEquals(expectedResponse, response)
    }

    @Test
    fun `get organizationById returns unauthorized with response DTO`() {
        activeProfileService = Mockito.mock(ActiveProfileService::class.java)
        Mockito.`when`(activeProfileService.isLocalActiveProfile()).thenReturn(false)

        organizationService = Mockito.mock(OrganizationService::class.java)

        val organizationController = OrganizationController(activeProfileService, organizationService)

        val expectedResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build<OrganizationResponseDto>()

        // Act
        val response = organizationController.getOrganizationById(null, "123")

        // Assert
        Assertions.assertEquals(expectedResponse, response)
    }

    private fun mockToken(email: String): OAuth2AuthenticationToken {
        val token: OAuth2AuthenticationToken = Mockito.mock(OAuth2AuthenticationToken::class.java)
        val principal: OAuth2User = Mockito.mock(OAuth2User::class.java)
        Mockito.`when`(principal.name).thenReturn(email)
        Mockito.`when`(token.principal).thenReturn(principal)
        return token
    }

}