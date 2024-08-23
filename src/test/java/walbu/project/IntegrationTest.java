package walbu.project;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import io.restassured.RestAssured;

@ActiveProfiles(profiles = "test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class IntegrationTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    protected void setUp() {
        RestAssured.port = port;
    }

}
