package io.jonathanlee.sparrowexpressapikotlin.controller

import io.jonathanlee.sparrowexpressapikotlin.dto.RequestDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseEntity

class IndexControllerTests {

    @Test
    fun `index returns ResponseEntity with RequestDto`() {
        // Arrange
        val controller = IndexController()
        val requestDto = RequestDto("1")
        val expectedResponse = ResponseEntity.ok(requestDto)

        // Act
        val response = controller.index(requestDto)

        // Assert
        assertEquals(expectedResponse, response)
    }

}
