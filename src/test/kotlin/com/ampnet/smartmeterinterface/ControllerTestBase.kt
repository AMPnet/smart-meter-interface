package com.ampnet.smartmeterinterface

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient

@ExtendWith(value = [SpringExtension::class, RestDocumentationExtension::class])
@SpringBootTest
abstract class ControllerTestBase : TestBase() {

    protected val defaultDocumentIdentifier = "{ClassName}/{methodName}"

    protected lateinit var webTestClient: WebTestClient

    @BeforeEach
    fun setUp(context: ApplicationContext, restDocumentation: RestDocumentationContextProvider) {
        webTestClient = WebTestClient.bindToApplicationContext(context)
            .configureClient()
            .filter(documentationConfiguration(restDocumentation)
                .operationPreprocessors()
                .withRequestDefaults(prettyPrint())
                .withResponseDefaults(prettyPrint()))
            .filter(documentationConfiguration(restDocumentation))
            .build()
    }
}
