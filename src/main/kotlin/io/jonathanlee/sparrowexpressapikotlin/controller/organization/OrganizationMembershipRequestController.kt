package io.jonathanlee.sparrowexpressapikotlin.controller.organization

import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationEmailRequestDto
import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationMembershipDto
import io.jonathanlee.sparrowexpressapikotlin.dto.organization.OrganizationMembershipRequestsContainerDto
import io.jonathanlee.sparrowexpressapikotlin.service.organization.OrganizationMembershipRequestService
import io.jonathanlee.sparrowexpressapikotlin.service.profile.ActiveProfileService
import io.jonathanlee.sparrowexpressapikotlin.util.OAuth2ClientUtils
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/organizations/membership-requests")
class OrganizationMembershipRequestController(
    private val activeProfileService: ActiveProfileService,
    private val organizationMembershipRequestService: OrganizationMembershipRequestService
) {

    @GetMapping(
        value = ["/organization/{organizationId}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getOrganizationMembershipRequests(
        oAuth2AuthenticationToken: OAuth2AuthenticationToken?,
        @PathVariable organizationId: String
    ): ResponseEntity<OrganizationMembershipRequestsContainerDto> {
        if (OAuth2ClientUtils.isUnauthenticated(oAuth2AuthenticationToken, this.activeProfileService)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        val requestingUserEmail = OAuth2ClientUtils.getRequestingUserEmail(oAuth2AuthenticationToken, this.activeProfileService)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        val organizationMembershipRequestsContainerDto = this.organizationMembershipRequestService.getRequestsToJoinOrganization(requestingUserEmail, organizationId)
        return ResponseEntity.status(organizationMembershipRequestsContainerDto?.httpStatus ?: HttpStatus.INTERNAL_SERVER_ERROR).body(organizationMembershipRequestsContainerDto)
    }

    @PostMapping(
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun requestToJoinOrganization(
        oAuth2AuthenticationToken: OAuth2AuthenticationToken?,
        @Valid @RequestBody emailRequestDto: OrganizationEmailRequestDto,
    ): ResponseEntity<OrganizationMembershipDto> {
        if (OAuth2ClientUtils.isUnauthenticated(oAuth2AuthenticationToken, this.activeProfileService)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        val requestingUserEmail = OAuth2ClientUtils.getRequestingUserEmail(oAuth2AuthenticationToken, this.activeProfileService)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        val organizationMembershipDto = this.organizationMembershipRequestService.requestToJoinOrganization(requestingUserEmail, emailRequestDto.email, emailRequestDto.organizationId)
        return ResponseEntity.status(organizationMembershipDto?.httpStatus ?: HttpStatus.INTERNAL_SERVER_ERROR).body(organizationMembershipDto)
    }

    @PatchMapping(
        value = ["/{organizationMembershipRequestId}/approve"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun approveRequestToJoinOrganization(
        oAuth2AuthenticationToken: OAuth2AuthenticationToken?,
        @PathVariable organizationMembershipRequestId: String
    ): ResponseEntity<OrganizationMembershipDto> {
        if (OAuth2ClientUtils.isUnauthenticated(oAuth2AuthenticationToken, this.activeProfileService)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        val requestingUserEmail = OAuth2ClientUtils.getRequestingUserEmail(oAuth2AuthenticationToken, this.activeProfileService)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        val organizationMembershipDto = this.organizationMembershipRequestService.approveRequestToJoinOrganization(requestingUserEmail, organizationMembershipRequestId)
        return ResponseEntity.status(organizationMembershipDto?.httpStatus ?: HttpStatus.INTERNAL_SERVER_ERROR).body(organizationMembershipDto)
    }

}
