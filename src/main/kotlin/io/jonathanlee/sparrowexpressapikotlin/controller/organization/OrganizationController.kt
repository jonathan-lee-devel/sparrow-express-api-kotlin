package io.jonathanlee.sparrowexpressapikotlin.controller.organization

import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationEmailRequestDto
import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationRequestDto
import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationResponseDto
import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationSnippetResponseDto
import io.jonathanlee.sparrowexpressapikotlin.service.organization.OrganizationService
import io.jonathanlee.sparrowexpressapikotlin.service.profile.ActiveProfileService
import io.jonathanlee.sparrowexpressapikotlin.util.OAuth2ClientUtils
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.web.bind.annotation.*

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

    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createOrganization(
        oAuth2AuthenticationToken: OAuth2AuthenticationToken?,
        @Valid @RequestBody organizationRequestDto: OrganizationRequestDto
    ): ResponseEntity<OrganizationResponseDto> {
        if (OAuth2ClientUtils.isUnauthenticated(oAuth2AuthenticationToken, this.activeProfileService)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        val requestingUserEmail = OAuth2ClientUtils.getRequestingUserEmail(oAuth2AuthenticationToken, this.activeProfileService)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        val organizationResponseDto = this.organizationService.createOrganization(requestingUserEmail, organizationRequestDto)
            ?: return ResponseEntity.internalServerError().build()
        return ResponseEntity.status(organizationResponseDto.httpStatus).body(organizationResponseDto)
    }

    @PatchMapping(
        value = ["/remove-organization-administrator"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun removeOrganizationAdministrator(
        oAuth2AuthenticationToken: OAuth2AuthenticationToken?,
        @Valid @RequestBody organizationEmailRequestDto: OrganizationEmailRequestDto
    ): ResponseEntity<OrganizationResponseDto> {
        if (OAuth2ClientUtils.isUnauthenticated(oAuth2AuthenticationToken, this.activeProfileService)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        val requestingUserEmail = OAuth2ClientUtils.getRequestingUserEmail(oAuth2AuthenticationToken, this.activeProfileService)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        val organizationResponseDto = this.organizationService.removeOrganizationAdministrator(requestingUserEmail, organizationEmailRequestDto.organizationId, organizationEmailRequestDto.email)
            ?: return ResponseEntity.internalServerError().build()
        return ResponseEntity.status(organizationResponseDto.httpStatus).body(organizationResponseDto)
    }

    @PatchMapping(
        value = ["/remove-organization-member"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun removeOrganizationMember(
        oAuth2AuthenticationToken: OAuth2AuthenticationToken?,
        @Valid @RequestBody organizationEmailRequestDto: OrganizationEmailRequestDto
    ): ResponseEntity<OrganizationResponseDto> {
        if (OAuth2ClientUtils.isUnauthenticated(oAuth2AuthenticationToken, this.activeProfileService)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        val requestingUserEmail = OAuth2ClientUtils.getRequestingUserEmail(oAuth2AuthenticationToken, this.activeProfileService)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        val organizationResponseDto = this.organizationService.removeOrganizationMember(requestingUserEmail, organizationEmailRequestDto.organizationId, organizationEmailRequestDto.email)
            ?: return ResponseEntity.internalServerError().build()
        return ResponseEntity.status(organizationResponseDto.httpStatus).body(organizationResponseDto)
    }

    @PatchMapping(
        value = ["/update-organization-member-to-join-as-member"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun updateOrganizationMemberToJoinAsMember(
        oAuth2AuthenticationToken: OAuth2AuthenticationToken?,
        @Valid @RequestBody organizationEmailRequestDto: OrganizationEmailRequestDto
    ): ResponseEntity<OrganizationResponseDto> {
        if (OAuth2ClientUtils.isUnauthenticated(oAuth2AuthenticationToken, this.activeProfileService)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        val requestingUserEmail = OAuth2ClientUtils.getRequestingUserEmail(oAuth2AuthenticationToken, this.activeProfileService)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        val organizationResponseDto = this.organizationService.updateOrganizationAdministratorToJoinAsMember(requestingUserEmail, organizationEmailRequestDto.organizationId, organizationEmailRequestDto.email)
            ?: return ResponseEntity.internalServerError().build()
        return ResponseEntity.status(organizationResponseDto.httpStatus).body(organizationResponseDto)
    }

}