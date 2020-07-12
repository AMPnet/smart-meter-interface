package com.ampnet.smartmeterinterface.public

import mu.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

@RestController
class PublicController {

    @GetMapping("/public")
    fun publicHello(): String {
        logger.debug { "Public hello" }
        return """
            {
                "public": "hello"
            }
        """.trimIndent()
    }
}
