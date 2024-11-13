package de.muenchen.oss.wahllokalsystem.authservice.domain;

import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UserTest {

    @Nested
    class FlatCopyOf {

        @Test
        void should_createUserWithNonNUllProperties_when_usingUserWithAllPropertiesSet() {
            val sourceUser = createUserWithAllPropertiesSet();

            val result = User.flatCopyOf(sourceUser);

            Assertions.assertThat(result).hasNoNullFieldsOrProperties();
        }

        @Test
        void should_createUserWithAllPropertiesEqual_when_usingUserWithAllPropertiesSet() {
            val sourceUser = createUserWithAllPropertiesSet();

            val result = User.flatCopyOf(sourceUser);

            Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(sourceUser);
        }

        private User createUserWithAllPropertiesSet() {
            val user = new User("username", "password", "email", true, true, "wahltagID", LocalDate.now(), "wahlbezirkID", "wahlbezirkNummer",
                    Wahlbezirksart.UWB, "PIN", Collections.emptySet(), "wbid_wahlnummer");
            user.setId(UUID.randomUUID());
            return user;
        }
    }

}
