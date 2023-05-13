package io.jonathanlee.sparrowexpressapikotlin.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.http.HttpStatus

abstract class ResponseDto(@field:JsonIgnore var httpStatus: HttpStatus)
