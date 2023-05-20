package io.jonathanlee.sparrowexpressapikotlin.controller.organization

import io.jonathanlee.sparrowexpressapikotlin.config.security.ITSecurityConfig
import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationResponseDto
import io.jonathanlee.sparrowexpressapikotlin.exception.handler.RestExceptionHandler
import io.jonathanlee.sparrowexpressapikotlin.service.organization.OrganizationService
import io.jonathanlee.sparrowexpressapikotlin.service.profile.ActiveProfileService
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ExtendWith(SpringExtension::class)
@WebMvcTest(OrganizationController::class)
@ActiveProfiles("integration")
@ContextConfiguration(classes = [ITSecurityConfig::class, RestExceptionHandler::class])
class OrganizationControllerIT {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var activeProfileService: ActiveProfileService

    @MockBean
    private lateinit var organizationService: OrganizationService

    @Disabled
    fun `test getOrganizationById when authenticated`() {
        val organizationId = "123"
        val requestingUserEmail = "user@example.com"

        `when`(activeProfileService.isLocalActiveProfile()).thenReturn(true)
        `when`(organizationService.getOrganizationById(requestingUserEmail, organizationId)).thenReturn(
            OrganizationResponseDto(
                organizationId,
                "Org-123",
                listOf("user@example.com"),
                listOf("user@example.com")
            )
        )

        val token: OAuth2AuthenticationToken = mockToken(requestingUserEmail)

        mockMvc.perform(
            get("/organizations/{organizationId}", organizationId)
                .contentType(MediaType.APPLICATION_JSON)
                .principal(token)
        )
            .andExpect(status().isOk)
            .andExpect(content().json("""
                {
                "id": "$organizationId",
                "name": "Org-123",
                "administratorEmails": ["user@example.com"],
                "memberEmails": ["user@example.com"]
                }
            """.trimIndent()))
    }

    @Test
    fun `test getOrganizationById not found when authenticated`() {
        val requestingUserEmail = "user@example.com"

        `when`(activeProfileService.isLocalActiveProfile()).thenReturn(true)

        val organizationResponseDto = OrganizationResponseDto(null, null, null, null)
        organizationResponseDto.httpStatus = HttpStatus.NOT_FOUND
        `when`(organizationService.getOrganizationById(requestingUserEmail, "123")).thenReturn(organizationResponseDto)

        val token: OAuth2AuthenticationToken = mockToken(requestingUserEmail)

        mockMvc.perform(
            get("/organizations/{organizationId}", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .principal(token)
        )
            .andExpect(status().isNotFound)
    }

    @Disabled
    fun `test getOrganizationById when unauthenticated`() {
        val organizationId = "123"

        `when`(activeProfileService.isLocalActiveProfile()).thenReturn(false)

        mockMvc.perform(
            get("/organizations/{organizationId}", organizationId)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isUnauthorized)
    }

    private fun mockToken(email: String): OAuth2AuthenticationToken {
        val token: OAuth2AuthenticationToken = mock(OAuth2AuthenticationToken::class.java)
        val principal: OAuth2User = mock(OAuth2User::class.java)
        `when`(principal.name).thenReturn(email)
        `when`(token.principal).thenReturn(principal)
        return token
    }
}
