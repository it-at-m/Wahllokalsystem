package de.muenchen.oss.wahllokalsystem.eaiservice;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorstand.WahlvorstandRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class, properties = "spring.datasource.url=jdbc:h2:mem:wls-eai-service-dummydata")
@ActiveProfiles({ "local", "db-oracle", "db-h2", "no-security", "db-dummydata" })
class MicroServiceApplicationDummyDataIntegrationTest {

    @Autowired
    WahlvorstandRepository wahlvorstandRepository;

    @Test
    void applicationIsRunningAndHasDummyData() {
        Assertions.assertThat(wahlvorstandRepository.count()).isGreaterThan(0);
    }

}
