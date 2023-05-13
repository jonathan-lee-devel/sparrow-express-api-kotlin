package io.jonathanlee.sparrowexpressapikotlin.config.interceptor

import io.jonathanlee.sparrowexpressapikotlin.interceptor.RequestLoggerInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@EnableWebMvc
@Configuration
class InterceptorConfig: WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(RequestLoggerInterceptor())
    }

}