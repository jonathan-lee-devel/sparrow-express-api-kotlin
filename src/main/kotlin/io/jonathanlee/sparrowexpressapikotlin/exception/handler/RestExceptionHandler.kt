package io.jonathanlee.sparrowexpressapikotlin.exception.handler

import io.jonathanlee.sparrowexpressapikotlin.exception.BadRequestException
import io.jonathanlee.sparrowexpressapikotlin.validation.dto.ErrorDto
import io.jonathanlee.sparrowexpressapikotlin.validation.dto.ValidationErrorDto
import io.jonathanlee.sparrowexpressapikotlin.validation.dto.ValidationErrorsContainerDto
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class RestExceptionHandler: ResponseEntityExceptionHandler() {

    override fun handleMethodArgumentNotValid(exception: MethodArgumentNotValidException, headers: HttpHeaders, status: HttpStatusCode, request: WebRequest
    ): ResponseEntity<Any>? {
        val errors = exception.bindingResult.fieldErrors.parallelStream().map { ValidationErrorDto(it.field, it.defaultMessage!!) }.toList()
        val validationErrorsContainerDto = ValidationErrorsContainerDto(errors)
        return ResponseEntity.status(status).body(validationErrorsContainerDto)
    }

    @ExceptionHandler(BadRequestException::class)
    protected fun handleBadRequest(exception: RuntimeException, request: WebRequest): ResponseEntity<Any>? {
        val (field, message) = exception as BadRequestException
        var validationErrorsContainerDto: ValidationErrorsContainerDto? = null
        if (field != null) {
            validationErrorsContainerDto = ValidationErrorsContainerDto(listOf(ValidationErrorDto(field, message)))
        }
        return handleExceptionInternal(exception, validationErrorsContainerDto ?: ErrorDto(message), HttpHeaders(), HttpStatus.BAD_REQUEST, request)
    }

}
