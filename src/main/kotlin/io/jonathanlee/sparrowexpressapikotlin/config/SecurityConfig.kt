package io.jonathanlee.sparrowexpressapikotlin.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf()
            .disable()
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests()
            .anyRequest()
            .permitAll()
            .and()
            .oauth2Login()
            .disable();

        return http.build();
    }

}