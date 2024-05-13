package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class KonfigurationModelValidatorTest {

    private static final String SERVICE_ID = "serviceID";

    private final KonfigurationModelValidator unitUnderTest = new KonfigurationModelValidator(SERVICE_ID);

    @Nested
    class ValideOrThrowGetKonfigurationByKey {

        @Test
        void noExceptionOnNonNull() {
            Assertions.assertThatNoException()
                    .isThrownBy(() -> unitUnderTest.valideOrThrowGetKonfigurationByKey(KonfigurationKonfigKey.WILLKOMMENSTEXT));
        }

        @Test
        void exceptionOnNullKey() {

            val exceptionThrown = Assertions.catchException(() -> unitUnderTest.valideOrThrowGetKonfigurationByKey(null));

            val expectedException = FachlicheWlsException.withCode("102").inService(SERVICE_ID).buildWithMessage("");

            Assertions.assertThat(exceptionThrown).usingRecursiveComparison().ignoringFields("message").isEqualTo(expectedException);
            Assertions.assertThat(exceptionThrown.getMessage()).isNotNull();
        }
    }

}
