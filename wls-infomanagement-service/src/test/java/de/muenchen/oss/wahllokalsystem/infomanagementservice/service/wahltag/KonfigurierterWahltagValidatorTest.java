package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.wahltag;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ServiceIDFormatter;
import java.time.LocalDate;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KonfigurierterWahltagValidatorTest {

    private static final String SERVICE_ID = "serviceID";

    @Mock
    ServiceIDFormatter serviceIDFormatter;

    @InjectMocks
    KonfigurierterWahltagValidator unitUnderTest;

    @Nested
    class ValidPostModelOrThrow {

        @Test
        void noExceptionOnValidModel() {
            val validModel = initValidModel().build();

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validPostModelOrThrow(validModel));
        }

        @Test
        void exceptionWhenModelIsNull() {
            Mockito.when(serviceIDFormatter.getId()).thenReturn(SERVICE_ID);

            val expectedException = FachlicheWlsException.withCode("100").inService(SERVICE_ID)
                    .buildWithMessage("postKonfigurierterWahltag: Suchkriterien unvollständig.");

            Assertions.assertThatThrownBy(() -> unitUnderTest.validPostModelOrThrow(null)).usingRecursiveComparison().isEqualTo(expectedException);
        }

        @Test
        void exceptionWhenWahltagIsNull() {
            val invalidModel = initValidModel().wahltag(null).build();

            Mockito.when(serviceIDFormatter.getId()).thenReturn(SERVICE_ID);

            val expectedException = FachlicheWlsException.withCode("100").inService(SERVICE_ID)
                    .buildWithMessage("postKonfigurierterWahltag: Suchkriterien unvollständig.");

            Assertions.assertThatThrownBy(() -> unitUnderTest.validPostModelOrThrow(invalidModel)).usingRecursiveComparison().isEqualTo(expectedException);
        }

        @Test
        void exceptionWhenWahltagIDIsNull() {
            val invalidModel = initValidModel().wahltagID(null).build();

            Mockito.when(serviceIDFormatter.getId()).thenReturn(SERVICE_ID);

            val expectedException = FachlicheWlsException.withCode("100").inService(SERVICE_ID)
                    .buildWithMessage("postKonfigurierterWahltag: Suchkriterien unvollständig.");

            Assertions.assertThatThrownBy(() -> unitUnderTest.validPostModelOrThrow(invalidModel)).usingRecursiveComparison().isEqualTo(expectedException);
        }
    }

    @Nested
    class ValidDeleteModelOrThrow {

        @Test
        void noExceptionOnValidModel() {
            val validModel = initValidModel().build();

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validDeleteModelOrThrow(validModel));
        }

        @Test
        void exceptionWhenWahltagIDIsNul() {
            val invalidModel = initValidModel().wahltagID(null).build();

            Mockito.when(serviceIDFormatter.getId()).thenReturn(SERVICE_ID);

            val expectedException = FachlicheWlsException.withCode("104").inService(SERVICE_ID)
                    .buildWithMessage("deleteKonfigurierterWahltag: Suchkriterien unvollständig.");

            Assertions.assertThatThrownBy(() -> unitUnderTest.validDeleteModelOrThrow(invalidModel)).usingRecursiveComparison().isEqualTo(expectedException);
        }

        @Test
        void exceptionWhenWahltagIDIsEmpty() {
            val invalidModel = initValidModel().wahltagID("").build();

            Mockito.when(serviceIDFormatter.getId()).thenReturn(SERVICE_ID);

            val expectedException = FachlicheWlsException.withCode("104").inService(SERVICE_ID)
                    .buildWithMessage("deleteKonfigurierterWahltag: Suchkriterien unvollständig.");

            Assertions.assertThatThrownBy(() -> unitUnderTest.validDeleteModelOrThrow(invalidModel)).usingRecursiveComparison().isEqualTo(expectedException);
        }
    }

    private KonfigurierterWahltagModel.KonfigurierterWahltagModelBuilder initValidModel() {
        return KonfigurierterWahltagModel.builder().wahltag(LocalDate.now()).wahltagID("wahltagID").active(true).nummer("nummer");
    }

}
