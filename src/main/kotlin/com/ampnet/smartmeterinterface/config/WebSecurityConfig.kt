package com.ampnet.smartmeterinterface.config

import com.ampnet.core.jwt.SecurityContextRepository
import com.ampnet.core.jwt.filter.DisabledProfileWebFilter
import com.ampnet.core.jwt.filter.UnverifiedProfileWebFilter
// import com.ampnet.core.jwt.filter.disabledProfileMessage
// import com.ampnet.core.jwt.filter.unVerifiedUserMessage
import com.ampnet.core.jwt.provider.JwtReactiveAuthenticationManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.reactive.config.EnableWebFlux

@Configuration
@EnableWebFlux
@EnableReactiveMethodSecurity
class WebSecurityConfig(private val applicationProperties: ApplicationProperties) {

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("*")
        configuration.allowedMethods = listOf(
                HttpMethod.HEAD.name,
                HttpMethod.GET.name,
                HttpMethod.POST.name,
                HttpMethod.PUT.name,
                HttpMethod.DELETE.name
        )
        configuration.allowedHeaders = listOf(
                HttpHeaders.AUTHORIZATION,
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.CACHE_CONTROL
        )

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        val authProvider = JwtReactiveAuthenticationManager(applicationProperties.jwt.signingKey)
        val securityContextRepo = SecurityContextRepository(authProvider)
        val disabledFilter = DisabledProfileWebFilter()
        val unverifiedProfileWebFilter = UnverifiedProfileWebFilter()

        return http
            .csrf().disable()
            .formLogin().disable()
            .httpBasic().disable()
            .authenticationManager(authProvider)
            .securityContextRepository(securityContextRepo)
            .authorizeExchange()
            .pathMatchers(HttpMethod.GET, "/actuator/**").authenticated()
            .pathMatchers(HttpMethod.GET, "/docs/index.html").permitAll()
            .pathMatchers("/public/**").permitAll()
            .and()
            .addFilterAfter(disabledFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .addFilterAfter(unverifiedProfileWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build()
    }
}
