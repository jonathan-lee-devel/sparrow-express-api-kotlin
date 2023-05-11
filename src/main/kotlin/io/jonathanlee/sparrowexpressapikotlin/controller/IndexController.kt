package io.jonathanlee.sparrowexpressapikotlin.controller

import io.jonathanlee.sparrowexpressapikotlin.dto.RequestDto
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class IndexController {

    @PostMapping
    fun index(@Validated @RequestBody body: RequestDto): ResponseEntity<RequestDto> {
        return ResponseEntity.ok(body)
    }

}
