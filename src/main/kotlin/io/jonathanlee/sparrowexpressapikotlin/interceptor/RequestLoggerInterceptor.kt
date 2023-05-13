package io.jonathanlee.sparrowexpressapikotlin.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.servlet.HandlerInterceptor

class RequestLoggerInterceptor(
    private val logger: Logger = LoggerFactory.getLogger(RequestLoggerInterceptor::class.java)
): HandlerInterceptor {

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        logger.info("${request.method} request at ${request.requestURI}")
        return true
    }
}