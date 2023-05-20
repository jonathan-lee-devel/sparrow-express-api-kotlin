package io.jonathanlee.sparrowexpressapikotlin.util

import io.jonathanlee.sparrowexpressapikotlin.service.profile.ActiveProfileService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User

class OAuth2ClientUtilsTest {

    private lateinit var activeProfileService: ActiveProfileService

    @Test
    fun `getRequestingUserEmail should return default local email when local profile is active`() {
        // Arrange
        activeProfileService = mock(ActiveProfileService::class.java)
        Mockito.`when`(activeProfileService.isLocalActiveProfile()).thenReturn(true)

        // Act
        val result = OAuth2ClientUtils.getRequestingUserEmail(null, activeProfileService)

        // Assert
        assertEquals("test@example.com", result)
    }

    @Test
    fun `getRequestingUserEmail should return email attribute from OAuth2AuthenticationToken`() {
        // Arrange
        val emailAttribute = "test@example.com"
        val principalAttributes = mapOf("email" to emailAttribute)
        val oAuth2AuthenticationToken = mock(OAuth2AuthenticationToken::class.java)
        val principal: OAuth2User = mock(OAuth2User::class.java)
        Mockito.`when`(oAuth2AuthenticationToken.principal).thenReturn(principal)
        Mockito.`when`(oAuth2AuthenticationToken.principal.attributes).thenReturn(principalAttributes)
        activeProfileService = mock(ActiveProfileService::class.java)
        Mockito.`when`(activeProfileService.isLocalActiveProfile()).thenReturn(true)

        // Act
        val result = OAuth2ClientUtils.getRequestingUserEmail(oAuth2AuthenticationToken, activeProfileService)

        // Assert
        assertEquals(emailAttribute, result)
    }

    @Test
    fun `getRequestingUserEmail should return null when OAuth2AuthenticationToken or principal is null`() {
        // Arrange
        val oAuth2AuthenticationToken = mock(OAuth2AuthenticationToken::class.java)
        activeProfileService = mock(ActiveProfileService::class.java)
        Mockito.`when`(activeProfileService.isLocalActiveProfile()).thenReturn(false)
        Mockito.`when`(oAuth2AuthenticationToken.principal).thenReturn(null)

        // Act
        val result = OAuth2ClientUtils.getRequestingUserEmail(oAuth2AuthenticationToken, activeProfileService)

        // Assert
        assertNull(result)
    }

    @Test
    fun `isUnauthenticated should return true when OAuth2AuthenticationToken and local profile are null`() {
        // Arrange
        val oAuth2AuthenticationToken = null
        activeProfileService = mock(ActiveProfileService::class.java)
        Mockito.`when`(activeProfileService.isLocalActiveProfile()).thenReturn(false)

        // Act
        val result = OAuth2ClientUtils.isUnauthenticated(oAuth2AuthenticationToken, activeProfileService)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `isUnauthenticated should return false when OAuth2AuthenticationToken is null and local profile is active`() {
        // Arrange
        val oAuth2AuthenticationToken = null
        activeProfileService = mock(ActiveProfileService::class.java)
        Mockito.`when`(activeProfileService.isLocalActiveProfile()).thenReturn(true)

        // Act
        val result = OAuth2ClientUtils.isUnauthenticated(oAuth2AuthenticationToken, activeProfileService)

        // Assert
        assertFalse(result)
    }

    @Test
    fun `isUnauthenticated should return false when OAuth2AuthenticationToken has a principal with email attribute`() {
        // Arrange
        val principalAttributes = mapOf("email" to "user@example.com")
        val oAuth2AuthenticationToken = mockToken("user@example.com")
        Mockito.`when`(oAuth2AuthenticationToken.principal.attributes).thenReturn(principalAttributes)
        activeProfileService = mock(ActiveProfileService::class.java)
        Mockito.`when`(activeProfileService.isLocalActiveProfile()).thenReturn(false)

        // Act
        val result = OAuth2ClientUtils.isUnauthenticated(oAuth2AuthenticationToken, activeProfileService)

        // Assert
        assertFalse(result)
    }

    @Test
    fun `isUnauthenticated should return true when OAuth2AuthenticationToken has a principal without email attribute`() {
        // Arrange
        val principalAttributes = mapOf<String, String>()
        val oAuth2AuthenticationToken = mock(OAuth2AuthenticationToken::class.java)
        activeProfileService = mock(ActiveProfileService::class.java)
        Mockito.`when`(activeProfileService.isLocalActiveProfile()).thenReturn(false)
        val principal: OAuth2User = mock(OAuth2User::class.java)
        Mockito.`when`(oAuth2AuthenticationToken.principal).thenReturn(principal)
        Mockito.`when`(oAuth2AuthenticationToken.principal.attributes).thenReturn(principalAttributes)

        // Act
        val result = OAuth2ClientUtils.isUnauthenticated(oAuth2AuthenticationToken, activeProfileService)

        // Assert
        assertTrue(result)
    }

    private fun mockToken(email: String): OAuth2AuthenticationToken {
        val token: OAuth2AuthenticationToken = mock(OAuth2AuthenticationToken::class.java)
        val principal: OAuth2User = mock(OAuth2User::class.java)
        Mockito.`when`(principal.name).thenReturn(email)
        Mockito.`when`(token.principal).thenReturn(principal)
        return token
    }

}
