package io.jonathanlee.sparrowexpressapikotlin.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.jonathanlee.sparrowexpressapikotlin.config.security.ITSecurityConfig
import io.jonathanlee.sparrowexpressapikotlin.dto.RequestDto
import io.jonathanlee.sparrowexpressapikotlin.exception.handler.RestExceptionHandler
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Answers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@ExtendWith(SpringExtension::class)
@WebMvcTest(IndexController::class)
@ActiveProfiles("integration")
@ContextConfiguration(classes = [ITSecurityConfig::class, RestExceptionHandler::class])
class IndexControllerIT {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean(answer = Answers.CALLS_REAL_METHODS)
    lateinit var indexController: IndexController

    @Test
    fun `Given valid request body, when POST to root URL, then return 200 OK`() {
        // Given
        val requestBody = RequestDto("1")

        // When
        val result = mockMvc
            .perform(post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))

        // Then
        result
            .andExpect(status().isOk)
            .andExpect(content().json("""
            {
                "id": "1"
            }
            """.trimIndent()))
    }

    @Test
    fun `Given invalid request body, when POST to root URL, then return 400 Bad Request with errors response body`() {
        // Given
        val requestBody = RequestDto("-1")

        // When
        val result = mockMvc.perform(post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))

        // Then
        result
            .andExpect(status().isBadRequest)
            .andExpect(content().json("""
        {
            "errors":[
                {
                    "field":"id",
                    "message":"size must be between 1 and 1"
                }
            ]
        }
        """.trimIndent()))
    }

    @Test
    fun `Given valid request body, when GET to missing-field URL, then return 400 Bad Request with errors response body`() {
        // When
        val result = mockMvc.perform(get("/missing-field"))

        // Then
        result
            .andExpect(status().isBadRequest)
            .andExpect(content().json("""
            {
                  "errors": [
                  {
                    "field": "any",
                    "message": "This field is missing"
                  }
                ]
            }
            """.trimIndent()))
    }

    @Test
    fun `Given valid request body, when GET to bad-request URL, then return 400 Bad Request with errors response body`() {
        // When
        val result = mockMvc.perform(get("/bad-request"))

        // Then
        result
            .andExpect(status().isBadRequest)
            .andExpect(content().json("""
            {
                "error": "No field is missing but the request is bad"
            }
            """.trimIndent()))
    }

}
