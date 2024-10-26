package davenkin.springboot.web;

import davenkin.springboot.web.about.QAboutInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class SpringBootWebApplicationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldDisplayAboutInfo() {
        var result = this.webTestClient
                .get()
                .uri("/about")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(QAboutInfo.class)
                .returnResult()
                .getResponseBody();
        assertNotNull(result.getDeployTime());
    }
}
