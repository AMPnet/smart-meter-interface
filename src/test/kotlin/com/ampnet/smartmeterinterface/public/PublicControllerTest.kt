package com.ampnet.smartmeterinterface.public

import com.ampnet.smartmeterinterface.ControllerTestBase
import org.junit.jupiter.api.Test
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document

class PublicControllerTest : ControllerTestBase() {

    @Test
    fun `must return hello`() {
        webTestClient.get().uri("/public")
            .exchange().expectStatus().isOk
            .expectBody().consumeWith(document(defaultDocumentIdentifier))
    }
}
