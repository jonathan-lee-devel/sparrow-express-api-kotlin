package io.jonathanlee.sparrowexpressapikotlin.controller.organization

import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationResponseDto
import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationSnippetResponseDto
import io.jonathanlee.sparrowexpressapikotlin.service.organization.OrganizationService
import io.jonathanlee.sparrowexpressapikotlin.service.profile.ActiveProfileService
import io.jonathanlee.sparrowexpressapikotlin.util.OAuth2ClientUtils
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/organizations")
class OrganizationController(
    private val activeProfileService: ActiveProfileService,
    private val organizationService: OrganizationService
) {

    @GetMapping(
        value = ["/{organizationId}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getOrganizationById(
        oAuth2AuthenticationToken: OAuth2AuthenticationToken?,
        @PathVariable organizationId: String
    ): ResponseEntity<OrganizationResponseDto> {
        if (OAuth2ClientUtils.isUnauthenticated(oAuth2AuthenticationToken, this.activeProfileService)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        val requestingUserEmail = OAuth2ClientUtils.getRequestingUserEmail(oAuth2AuthenticationToken, this.activeProfileService)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        val organizationResponseDto = this.organizationService.getOrganizationById(requestingUserEmail, organizationId)
        return ResponseEntity.status(organizationResponseDto?.httpStatus ?: HttpStatus.INTERNAL_SERVER_ERROR).body(organizationResponseDto)
    }

    @GetMapping(
        value = ["/{organizationId}/snippet"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getOrganizationSnippetById(
        @PathVariable organizationId: String
    ): ResponseEntity<OrganizationSnippetResponseDto> {
        val organizationSnippetResponseDto = this.organizationService.getOrganizationSnippetById(organizationId)
            ?: return ResponseEntity.internalServerError().build()
        return ResponseEntity.status(organizationSnippetResponseDto.httpStatus).body(organizationSnippetResponseDto)
    }

}