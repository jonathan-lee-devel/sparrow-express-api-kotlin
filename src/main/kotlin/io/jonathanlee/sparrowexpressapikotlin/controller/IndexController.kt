package io.jonathanlee.sparrowexpressapikotlin.controller

import io.jonathanlee.sparrowexpressapikotlin.dto.RequestDto
import io.jonathanlee.sparrowexpressapikotlin.service.random.RandomService
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class IndexController(private val randomService: RandomService) {

    @PostMapping
    fun index(@Validated @RequestBody body: RequestDto): ResponseEntity<RequestDto> {
        randomService.generateNewId()
        return ResponseEntity.ok(body)
    }

}
