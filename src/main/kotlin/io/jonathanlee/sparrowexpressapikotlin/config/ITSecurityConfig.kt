package io.jonathanlee.sparrowexpressapikotlin.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Profile("integration")
@EnableWebSecurity
class ITSecurityConfig {

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
        .disable()

    return http.build()
  }

}
