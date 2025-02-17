package de.muenchen.oss.wahllokalsystem.adminservice.service.wahlen;

import static org.mockito.Mockito.times;

import de.muenchen.oss.wahllokalsystem.adminservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDate;
import java.util.List;
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
class WahlenServiceTest {

    @Mock
    WahlenClient wahlenClient;

    @Mock
    WahlenValidator wahlenValidator;

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    WahlenService unitUnderTest;

    @Nested
    class GetWahlen {

        @Test
        void should_callGetWahlenClient_when_serviceIsCalled() {
            val wahlID = "wahlID";

            Mockito.doNothing().when(wahlenValidator).validWahlIDParamOrThrow(wahlID);
            unitUnderTest.getWahlen(wahlID);

            Mockito.verify(wahlenClient, times(1)).getWahlen(wahlID);
        }
    }

    @Nested
    class UpdateWahlen {

        @Test
        void should_callPostWahlenClient_when_serviceIsCalled() {
            val wahlID = "wahlID";
            List<WahlModel> mockedListOfModel = createWahlModelList("name");

            unitUnderTest.updateWahlen(mockedListOfModel, wahlID);

            Mockito.verify(wahlenClient, times(1)).postWahlen(wahlID, mockedListOfModel);
        }

        @Test
        void should_throwFachlicheWlsException_when_serviceParameterWahlenIsNull() {
            val wahlID = "wahlID";

            val mockedFachlicheWlsException = FachlicheWlsException.withCode("000").buildWithMessage("missing argument");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.MISSING_ARGUMENT)).thenReturn(mockedFachlicheWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.updateWahlen(null, wahlID))
                    .isSameAs(mockedFachlicheWlsException);
        }
    }

    private List<WahlModel> createWahlModelList(final String namePraefix) {
        return List.of(
                new WahlModel("wahlID", namePraefix + "wahl1", 1L,
                        1L, LocalDate.now().plusMonths(1),
                        WahlartModel.BAW, new FarbeModel(1, 1, 1)),
                new WahlModel("wahlID", namePraefix + "wahl2", 2L,
                        2L, LocalDate.now().plusMonths(2),
                        WahlartModel.LTW, new FarbeModel(2, 2, 2)),
                new WahlModel("wahlID", namePraefix + "wahl3", 3L,
                        3L, LocalDate.now().plusMonths(3),
                        WahlartModel.LTW, new FarbeModel(3, 3, 3)));
    }
}
