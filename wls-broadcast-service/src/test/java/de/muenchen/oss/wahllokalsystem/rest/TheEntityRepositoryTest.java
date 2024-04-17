/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2024
 */
package de.muenchen.oss.wahllokalsystem.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import de.muenchen.oss.wahllokalsystem.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.domain.TheEntity;

import static de.muenchen.oss.wahllokalsystem.TestConstants.SPRING_TEST_PROFILE;
import static de.muenchen.oss.wahllokalsystem.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(
        classes = { MicroServiceApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:wahllokalsystem;DB_CLOSE_ON_EXIT=FALSE",
                "refarch.gracefulshutdown.pre-wait-seconds=0"
        }
)
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
class TheEntityRepositoryTest {

    @Autowired
    private TheEntityRepository repository;

    @Test
    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = Exception.class)
    void testSave() {

        // Implement your logic here by replacing and/or extending the code

        // initialize
        TheEntity original = new TheEntity();
        original.setTextAttribute("test");

        // persist
        original = repository.save(original);

        // check
        TheEntity persisted = repository.findById(original.getId()).orElse(null);
        assertNotNull(persisted);
        assertEquals(original, persisted);

    }

}
