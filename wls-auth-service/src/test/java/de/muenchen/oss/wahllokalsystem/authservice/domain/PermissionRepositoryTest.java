package de.muenchen.oss.wahllokalsystem.authservice.domain;

import de.muenchen.oss.wahllokalsystem.authservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.authservice.TestConstants;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles(TestConstants.SPRING_TEST_PROFILE)
class PermissionRepositoryTest {

    @Autowired
    private PermissionRepository permissionRepository;

    @AfterEach
    void tearDown() {
        permissionRepository.deleteAll();
    }

    @Nested
    class FindByPermission {

        @Test
        void should_returnOptionalWithPermission_when_found() {
            val permissionString = "permission";
            val permissionToFind = permissionRepository.save(createPermissionWithPermission(permissionString));

            val findByResult = permissionRepository.findByPermission(permissionString);

            Assertions.assertThat(findByResult.get()).isEqualTo(permissionToFind);
        }

        @Test
        void should_returnEmptyOptional_when_notFound() {
            val findByResult = permissionRepository.findByPermission("not-found");

            Assertions.assertThat(findByResult).isEmpty();
        }
    }

    private Permission createPermissionWithPermission(final String permission) {
        val permissionObject = new Permission();

        permissionObject.setPermission(permission);

        return permissionObject;
    }

}
