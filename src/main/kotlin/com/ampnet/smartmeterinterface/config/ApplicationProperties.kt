package com.ampnet.smartmeterinterface.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "com.ampnet.smart-meter-interface")
class ApplicationProperties {
    val jwt: JwtProperties = JwtProperties()
}

class JwtProperties {
    lateinit var signingKey: String
}
