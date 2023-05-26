package io.jonathanlee.sparrowexpressapikotlin.controller

import io.jonathanlee.sparrowexpressapikotlin.dto.RequestDto
import io.jonathanlee.sparrowexpressapikotlin.exception.BadRequestException
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/")
class IndexController {

    @GetMapping
    fun greeting(): ResponseEntity<Any> {
        return ResponseEntity.ok("""{
            "greeting": "Greetings!"
        }""".trimIndent())
    }

    @PostMapping
    fun index(@Validated @RequestBody body: RequestDto): ResponseEntity<RequestDto> {
        return ResponseEntity.ok(body)
    }

    @GetMapping("/missing-field")
    fun getIndex(): ResponseEntity<Any> {
        throw BadRequestException("any", "This field is missing")
    }

    @GetMapping("/bad-request")
    fun getMappedIndex(): ResponseEntity<Any> {
        throw BadRequestException(null, "No field is missing but the request is bad")
    }

}
