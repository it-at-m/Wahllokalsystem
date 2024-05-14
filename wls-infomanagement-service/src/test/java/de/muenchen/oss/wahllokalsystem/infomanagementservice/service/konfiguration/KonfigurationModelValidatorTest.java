package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KonfigurationKonfigKey;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KonfigurationSetModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class KonfigurationModelValidatorTest {

    private static final String SERVICE_ID = "serviceID";

    private final KonfigurationModelValidator unitUnderTest = new KonfigurationModelValidator(SERVICE_ID);

    @Nested
    class ValidOrThrowGetKonfigurationByKey {

        @Test
        void noExceptionOnNonNull() {
            Assertions.assertThatNoException()
                    .isThrownBy(() -> unitUnderTest.validOrThrowGetKonfigurationByKey(KonfigurationKonfigKey.WILLKOMMENSTEXT));
        }

        @Test
        void exceptionOnNullKey() {

            val exceptionThrown = Assertions.catchException(() -> unitUnderTest.validOrThrowGetKonfigurationByKey(null));

            val expectedException = FachlicheWlsException.withCode("102").inService(SERVICE_ID).buildWithMessage("");

            Assertions.assertThat(exceptionThrown).usingRecursiveComparison().ignoringFields("message").isEqualTo(expectedException);
            Assertions.assertThat(exceptionThrown.getMessage()).isNotNull();
        }
    }

    @Nested
    class ValidOrThrowSetKonfiguration {

        @Test
        void noExceptionWhenValid() {
            val validModel = initValidModel().build();

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validOrThrowSetKonfiguration(validModel));
        }

        @Test
        void exceptionWhenModelIsNull() {
            final KonfigurationSetModel modelIsNull = null;

            val exceptionThrown = Assertions.catchException(() -> unitUnderTest.validOrThrowSetKonfiguration(modelIsNull));

            val expectedException = FachlicheWlsException.withCode("100").inService(SERVICE_ID).buildWithMessage("");

            Assertions.assertThat(exceptionThrown).usingRecursiveComparison().ignoringFields("message").isEqualTo(expectedException);
            Assertions.assertThat(exceptionThrown.getMessage()).isNotNull();
        }

        @Test
        void exceptionWhenSchluesselIsNull() {
            val invalidModel = initValidModel().schluessel(null).build();

            val exceptionThrown = Assertions.catchException(() -> unitUnderTest.validOrThrowSetKonfiguration(invalidModel));

            val expectedException = FachlicheWlsException.withCode("100").inService(SERVICE_ID).buildWithMessage("");

            Assertions.assertThat(exceptionThrown).usingRecursiveComparison().ignoringFields("message").isEqualTo(expectedException);
            Assertions.assertThat(exceptionThrown.getMessage()).isNotNull();
        }

        private KonfigurationSetModel.KonfigurationSetModelBuilder initValidModel() {
            return KonfigurationSetModel.builder().schluessel("schluessel");
        }
    }

}
