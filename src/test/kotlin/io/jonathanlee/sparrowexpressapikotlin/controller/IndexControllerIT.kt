package io.jonathanlee.sparrowexpressapikotlin.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.jonathanlee.sparrowexpressapikotlin.config.security.ITSecurityConfig
import io.jonathanlee.sparrowexpressapikotlin.dto.RequestDto
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@ExtendWith(SpringExtension::class)
@WebMvcTest(IndexController::class)
@ActiveProfiles("integration")
@ContextConfiguration(classes = [ITSecurityConfig::class])
class IndexControllerIT {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var indexController: IndexController

    @Test
    fun `Given valid request body, when POST to root URL, then return 200 OK with request body`() {
        // Given
        val requestBody = RequestDto("1")

        // When
        val result = mockMvc.perform(post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))

        // Then
        result.andExpect(status().isOk)
    }

    @Test
    fun `Given invalid request body, when POST to root URL, then return 400 Bad Request`() {
        // Given
        val requestBody = RequestDto("-1")

        // When
        val result = mockMvc.perform(post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))

        // Then
        result.andExpect(status().isBadRequest)
    }

}
