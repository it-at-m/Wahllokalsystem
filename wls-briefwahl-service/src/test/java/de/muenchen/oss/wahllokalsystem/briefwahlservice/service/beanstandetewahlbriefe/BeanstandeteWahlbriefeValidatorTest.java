package de.muenchen.oss.wahllokalsystem.briefwahlservice.service.beanstandetewahlbriefe;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import java.util.HashMap;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BeanstandeteWahlbriefeValidatorTest {

    private BeanstandeteWahlbriefeValidator unitUnderTest = new BeanstandeteWahlbriefeValidator();

    @Nested
    class ValideReferenceOrThrow {

        private final String EXPECTED_CODE = "100";

        @Test
        void exceptionOnWahlbezirkIDIsNull() {
            val invalidReference = getValidReference().wahlbezirkID(null).build();

            val exceptionThrown = org.junit.jupiter.api.Assertions.assertThrowsExactly(FachlicheWlsException.class,
                    () -> unitUnderTest.valideReferenceOrThrow(invalidReference));

            Assertions.assertThat(exceptionThrown.getCode()).isEqualTo(EXPECTED_CODE);
            Assertions.assertThat(exceptionThrown.getMessage()).isNotNull();
        }

        @Test
        void exceptionOnWahlbezirkIDIsEmptyString() {
            val invalidReference = getValidReference().wahlbezirkID("").build();

            val exceptionThrown = org.junit.jupiter.api.Assertions.assertThrowsExactly(FachlicheWlsException.class,
                    () -> unitUnderTest.valideReferenceOrThrow(invalidReference));

            Assertions.assertThat(exceptionThrown.getCode()).isEqualTo(EXPECTED_CODE);
            Assertions.assertThat(exceptionThrown.getMessage()).isNotNull();
        }

        @Test
        void exceptionOnWahlbezirkIsBlankString() {
            val invalidReference = getValidReference().wahlbezirkID("   ").build();

            val exceptionThrown = org.junit.jupiter.api.Assertions.assertThrowsExactly(FachlicheWlsException.class,
                    () -> unitUnderTest.valideReferenceOrThrow(invalidReference));

            Assertions.assertThat(exceptionThrown.getCode()).isEqualTo(EXPECTED_CODE);
            Assertions.assertThat(exceptionThrown.getMessage()).isNotNull();
        }

        @Test
        void excepionOnWaehlerverzeichnisnummerIsNull() {
            val invalidReference = getValidReference().waehlerverzeichnisNummer(null).build();

            val exceptionThrown = org.junit.jupiter.api.Assertions.assertThrowsExactly(FachlicheWlsException.class,
                    () -> unitUnderTest.valideReferenceOrThrow(invalidReference));

            Assertions.assertThat(exceptionThrown.getCode()).isEqualTo(EXPECTED_CODE);
            Assertions.assertThat(exceptionThrown.getMessage()).isNotNull();
        }

        @Test
        void exceptionOnWaehlerverzeichnisnummerIsZero() {
            val invalidReference = getValidReference().waehlerverzeichnisNummer(0L).build();

            val exceptionThrown = org.junit.jupiter.api.Assertions.assertThrowsExactly(FachlicheWlsException.class,
                    () -> unitUnderTest.valideReferenceOrThrow(invalidReference));

            Assertions.assertThat(exceptionThrown.getCode()).isEqualTo(EXPECTED_CODE);
            Assertions.assertThat(exceptionThrown.getMessage()).isNotNull();
        }

        @Test
        void noExceotionOnValideReference() {
            val valideReference = getValidReference().build();

            org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> unitUnderTest.valideReferenceOrThrow(valideReference));
        }

        private BeanstandeteWahlbriefeReference.BeanstandeteWahlbriefeReferenceBuilder getValidReference() {
            return BeanstandeteWahlbriefeReference.builder().wahlbezirkID("wbzId").waehlerverzeichnisNummer(1L);
        }

    }

    @Nested
    class ValideModelOrThrow {

        private final String EXPECTED_CODE = "101";

        @Test
        void exceptionOnBeanstandeteWahlbriefeIsNull() {
            val invalidModel = getValidModel().beanstandeteWahlbriefe(null).build();

            val exceptionThrown = org.junit.jupiter.api.Assertions.assertThrowsExactly(FachlicheWlsException.class,
                    () -> unitUnderTest.valideModelOrThrow(invalidModel));

            Assertions.assertThat(exceptionThrown.getCode()).isEqualTo(EXPECTED_CODE);
            Assertions.assertThat(exceptionThrown.getMessage()).isNotNull();
        }

        @Test
        void exceptionOnWahlbezirkIDIsNull() {
            val invalidModel = getValidModel().wahlbezirkID(null).build();

            val exceptionThrown = org.junit.jupiter.api.Assertions.assertThrowsExactly(FachlicheWlsException.class,
                    () -> unitUnderTest.valideModelOrThrow(invalidModel));

            Assertions.assertThat(exceptionThrown.getCode()).isEqualTo(EXPECTED_CODE);
            Assertions.assertThat(exceptionThrown.getMessage()).isNotNull();
        }

        @Test
        void exceptionOnWahlbezirkIDIsEmptyString() {
            val invalidModel = getValidModel().wahlbezirkID("").build();

            val exceptionThrown = org.junit.jupiter.api.Assertions.assertThrowsExactly(FachlicheWlsException.class,
                    () -> unitUnderTest.valideModelOrThrow(invalidModel));

            Assertions.assertThat(exceptionThrown.getCode()).isEqualTo(EXPECTED_CODE);
            Assertions.assertThat(exceptionThrown.getMessage()).isNotNull();
        }

        @Test
        void exceptionOnWahlbezirkIDIsBlankString() {
            val invalidModel = getValidModel().wahlbezirkID("   ").build();

            val exceptionThrown = org.junit.jupiter.api.Assertions.assertThrowsExactly(FachlicheWlsException.class,
                    () -> unitUnderTest.valideModelOrThrow(invalidModel));

            Assertions.assertThat(exceptionThrown.getCode()).isEqualTo(EXPECTED_CODE);
            Assertions.assertThat(exceptionThrown.getMessage()).isNotNull();
        }

        @Test
        void exceptionOnWaehlerverzeichnisNummerIsNull() {
            val invalidModel = getValidModel().waehlerverzeichnisNummer(null).build();

            val exceptionThrown = org.junit.jupiter.api.Assertions.assertThrowsExactly(FachlicheWlsException.class,
                    () -> unitUnderTest.valideModelOrThrow(invalidModel));

            Assertions.assertThat(exceptionThrown.getCode()).isEqualTo(EXPECTED_CODE);
            Assertions.assertThat(exceptionThrown.getMessage()).isNotNull();
        }

        @Test
        void exceptionOnWaehlerverzeichnisNummerIsZero() {
            val invalidModel = getValidModel().waehlerverzeichnisNummer(0L).build();

            val exceptionThrown = org.junit.jupiter.api.Assertions.assertThrowsExactly(FachlicheWlsException.class,
                    () -> unitUnderTest.valideModelOrThrow(invalidModel));

            Assertions.assertThat(exceptionThrown.getCode()).isEqualTo(EXPECTED_CODE);
            Assertions.assertThat(exceptionThrown.getMessage()).isNotNull();
        }

        @Test
        void noExceptionOnValideModel() {
            val modelToValidate = getValidModel().build();

            org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> unitUnderTest.valideModelOrThrow(modelToValidate));
        }

        private BeanstandeteWahlbriefeModel.BeanstandeteWahlbriefeModelBuilder getValidModel() {
            return BeanstandeteWahlbriefeModel.builder().beanstandeteWahlbriefe(new HashMap<>()).waehlerverzeichnisNummer(1L)
                    .wahlbezirkID("wbzId");
        }

    }

}
