package de.muenchen.oss.wahllokalsystem.eaiservice;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorstand.WahlvorstandRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles({ TestConstants.SPRING_TEST_PROFILE, TestConstants.SPRING_NO_SECURITY_PROFILE, "db-h2", "db-dummydata" })
class MicroServiceApplicationDummyDataIntegrationTest {

    @Autowired
    WahlvorstandRepository wahlvorstandRepository;

    @Test
    void applicationIsRunningAndHasDummyData() {
        Assertions.assertThat(wahlvorstandRepository.count()).isGreaterThan(0);
    }

}