package io.jonathanlee.sparrowexpressapikotlin.config.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.lang.NonNull
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
@EnableWebSecurity
@Profile("production")
class SecurityProductionConfig(
    private val clientRegistrationRepository: ClientRegistrationRepository
) {

    @Value("\${sparrow.environment.frontEndHost}")
    private lateinit var frontEndHost: String

    @Value("\${sparrow.environment.googleLoginHost}")
    private lateinit var googleLoginHost: String

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf()
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .and()
            .cors { httpSecurityCorsConfigurer: CorsConfigurer<HttpSecurity?> ->
                httpSecurityCorsConfigurer.configurationSource(
                    urlBasedCorsConfigurationSource()
                )
            }
            .authorizeHttpRequests()
            .requestMatchers("/logout-success")
            .permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .oauth2Login { httpSecurityOAuth2LoginConfigurer: OAuth2LoginConfigurer<HttpSecurity?> ->
                httpSecurityOAuth2LoginConfigurer.defaultSuccessUrl(
                    "http://localhost:4200/login-success",
                    true
                )
            }
            .logout { httpSecurityLogoutConfigurer: LogoutConfigurer<HttpSecurity?> ->
                httpSecurityLogoutConfigurer.logoutSuccessUrl("http://localhost:4200/logout-success")
                httpSecurityLogoutConfigurer.logoutSuccessHandler(
                    oidcClientInitiatedLogoutSuccessHandler()
                )
                httpSecurityLogoutConfigurer.invalidateHttpSession(true)
                httpSecurityLogoutConfigurer.clearAuthentication(true)
                httpSecurityLogoutConfigurer.deleteCookies(COOKIE_NAME_TO_CLEAR)
                httpSecurityLogoutConfigurer.permitAll()
            }
        return http.build()
    }

    @Bean
    fun corsFilter(): CorsFilter {
        return CorsFilter(urlBasedCorsConfigurationSource())
    }

    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        val allowedDomains = arrayOf(frontEndHost, googleLoginHost)
        return object : WebMvcConfigurer {
            override fun addCorsMappings(@NonNull registry: CorsRegistry) {
                registry.addMapping("/**").allowedOrigins(*allowedDomains)
            }
        }
    }

    private fun urlBasedCorsConfigurationSource(): UrlBasedCorsConfigurationSource {
        val corsConfiguration = CorsConfiguration()
        corsConfiguration.allowCredentials = true
        corsConfiguration.allowedOrigins =
            listOf(frontEndHost, googleLoginHost)
        corsConfiguration.allowedHeaders = listOf(
            "Origin",
            "Access-Control-Allow-Origin",
            "Content-Type",
            "Accepts",
            "Authorization",
            "Origin, Accept",
            "X-Requested-With",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        )
        corsConfiguration.exposedHeaders = listOf(
            "Origin", "Content-Type", "Accept", "Authorization",
            "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"
        )
        corsConfiguration.allowedMethods =
            listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        val urlBasedCorsConfigurationSource = UrlBasedCorsConfigurationSource()
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration)
        return urlBasedCorsConfigurationSource
    }

    private fun oidcClientInitiatedLogoutSuccessHandler(): OidcClientInitiatedLogoutSuccessHandler {
        val successHandler = OidcClientInitiatedLogoutSuccessHandler(
            clientRegistrationRepository
        )
        successHandler.setPostLogoutRedirectUri("http://localhost:42000/logout-success")
        return successHandler
    }

    companion object {
        private const val COOKIE_NAME_TO_CLEAR = "JSESSIONID"
    }

}

