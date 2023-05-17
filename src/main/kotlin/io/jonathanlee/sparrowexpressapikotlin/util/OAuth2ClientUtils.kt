package io.jonathanlee.sparrowexpressapikotlin.util

import io.jonathanlee.sparrowexpressapikotlin.service.profile.ActiveProfileService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken


object OAuth2ClientUtils {

    const val NAME_ATTRIBUTE = "name"

    private const val EMAIL_ATTRIBUTE = "email"

    private const val DEFAULT_LOCAL_EMAIL = "test@example.com"

    private fun getEmailAttributeFromOAuth2AuthenticationToken(oAuth2AuthenticationToken: OAuth2AuthenticationToken?): String? {
        return if (oAuth2AuthenticationToken == null || oAuth2AuthenticationToken.principal == null) {
            null
        } else oAuth2AuthenticationToken.principal.attributes[EMAIL_ATTRIBUTE].toString()
    }

    private fun isNoAuthentication(oAuth2AuthenticationToken: OAuth2AuthenticationToken?): Boolean {
        return oAuth2AuthenticationToken == null || oAuth2AuthenticationToken.principal == null || oAuth2AuthenticationToken.principal.attributes[EMAIL_ATTRIBUTE] == null
    }

    fun getRequestingUserEmail(
        oAuth2AuthenticationToken: OAuth2AuthenticationToken?,
        activeProfileService: ActiveProfileService
    ): String? {
        return if (activeProfileService.isLocalActiveProfile()) DEFAULT_LOCAL_EMAIL else getEmailAttributeFromOAuth2AuthenticationToken(oAuth2AuthenticationToken)
    }

    fun isUnauthenticated(
        oAuth2AuthenticationToken: OAuth2AuthenticationToken?,
        activeProfileService: ActiveProfileService
    ): Boolean {
        val isNoAuthentication = isNoAuthentication(oAuth2AuthenticationToken)
        return isNoAuthentication && !activeProfileService.isLocalActiveProfile()
    }
}

