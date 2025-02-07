package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client.basisdaten;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.basisdaten.client.WahlenControllerApi;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.basisdaten.model.WahlDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.infomanagement.client.KonfigurierterWahltagControllerApi;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.infomanagement.model.KonfigurierterWahltagDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.WahlartModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasisdatenClientImplTest {

    @Mock
    KonfigurierterWahltagControllerApi konfigurierterWahltagControllerApi;
    @Mock
    WahlenControllerApi wahlenControllerApi;
    @Mock
    ExceptionFactory exceptionFactory;
    @Mock
    BasisdatenClientMapper basisdatenClientMapper;

    @InjectMocks
    BasisdatenClientImpl unitUnderTest;

    @Nested
    class GetWahlartOfCurrentWahltag {

        private final String wahlID = "wahlID";

        @Test
        void should_returnWahlartOfWahl_when_wahlWithIDExistsOnCurrentWahltag() {
            val mockedWahltagID = "mockedWahltagID";
            val mockedKonfigurierterWahltag = new KonfigurierterWahltagDTO().wahltagID(mockedWahltagID);
            val mockedWahlen = List.of(new WahlDTO().wahlart(WahlDTO.WahlartEnum.BTW).wahlID("other" + wahlID),
                    new WahlDTO().wahlart(WahlDTO.WahlartEnum.LTW).wahlID(wahlID),
                    new WahlDTO().wahlart(WahlDTO.WahlartEnum.EUW).wahlID("another" + wahlID));

            Mockito.when(konfigurierterWahltagControllerApi.getKonfigurierterWahltag()).thenReturn(mockedKonfigurierterWahltag);
            Mockito.when(wahlenControllerApi.getWahlen(mockedWahltagID)).thenReturn(mockedWahlen);
            Mockito.when(basisdatenClientMapper.toModel(WahlDTO.WahlartEnum.LTW)).thenReturn(WahlartModel.LTW);

            val result = unitUnderTest.getWahlartOfCurrentWahltag(wahlID);

            Assertions.assertThat(result).isEqualTo(WahlartModel.LTW);
        }

        @Test
        void should_throwFachlicheWlsException_when_noWahlWithGivenIDExistsOnCurrentWahltag() {
            val mockedWahltagID = "mockedWahltagID";
            val mockedKonfigurierterWahltag = new KonfigurierterWahltagDTO().wahltagID(mockedWahltagID);
            val mockedWahlen = List.of(new WahlDTO().wahlart(WahlDTO.WahlartEnum.BTW).wahlID("other" + wahlID),
                    new WahlDTO().wahlart(WahlDTO.WahlartEnum.EUW).wahlID("another" + wahlID));
            val mockedWlsException = FachlicheWlsException.withCode("000").buildWithMessage("no wahl with wahlID given");

            Mockito.when(konfigurierterWahltagControllerApi.getKonfigurierterWahltag()).thenReturn(mockedKonfigurierterWahltag);
            Mockito.when(wahlenControllerApi.getWahlen(mockedWahltagID)).thenReturn(mockedWahlen);
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.BASISDATEN_WAHL_NOT_FOUND)).thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlartOfCurrentWahltag(wahlID)).isSameAs(mockedWlsException);
        }

        @ParameterizedTest(name = "{1}")
        @MethodSource("argumentsOfWahltagApiWithNoCurrentWahltag")
        void should_throwFachlicheWlsException_when_noCurrentWahltagExists(final ArgumentsAccessor arguments) {
            val mockedWlsException = FachlicheWlsException.withCode("000").buildWithMessage("no current wahltag");

            Mockito.when(konfigurierterWahltagControllerApi.getKonfigurierterWahltag()).thenReturn(arguments.get(0, KonfigurierterWahltagDTO.class));
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.INFOMANAGEMENT_WAHLTAG_NULL_OR_EMPTY)).thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlartOfCurrentWahltag(wahlID)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwFachlicheWlsException_when_noWahlenExistsOnCurrentWahltag() {
            val mockedWahltagID = "mockedWahltagID";
            val mockedKonfigurierterWahltag = new KonfigurierterWahltagDTO().wahltagID(mockedWahltagID);
            final List<WahlDTO> mockedWahlen = Collections.emptyList();
            val mockedWlsException = FachlicheWlsException.withCode("000").buildWithMessage("no wahlen on current wahltag");

            Mockito.when(konfigurierterWahltagControllerApi.getKonfigurierterWahltag()).thenReturn(mockedKonfigurierterWahltag);
            Mockito.when(wahlenControllerApi.getWahlen(mockedWahltagID)).thenReturn(mockedWahlen);
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.BASISDATEN_WAHLEN_EMPTY)).thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlartOfCurrentWahltag(wahlID)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwTechnischeWlsException_when_nonWlsExceptionIsThrownFromWahltagApi() {
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with wahltag api failed");

            Mockito.doThrow(new RuntimeException("api call failed")).when(konfigurierterWahltagControllerApi).getKonfigurierterWahltag();
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_INFOMANAGEMENT))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlartOfCurrentWahltag(wahlID)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwTechnischeWlsException_when_nonWlsExceptionIsThrownFromWahlenApi() {
            val mockedWahltagID = "mockedWahltagID";
            val mockedKonfigurierterWahltag = new KonfigurierterWahltagDTO().wahltagID(mockedWahltagID);
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with wahlen api failed");

            Mockito.when(konfigurierterWahltagControllerApi.getKonfigurierterWahltag()).thenReturn(mockedKonfigurierterWahltag);
            Mockito.doThrow(new RuntimeException("api call failed")).when(wahlenControllerApi).getWahlen(mockedWahltagID);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_BASISDATEN))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlartOfCurrentWahltag(wahlID)).isSameAs(mockedWlsException);
        }

        @Test
        void should_rethrowWlsException_when_wahltagApiThrewWlsException() {
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with wahltag api failed");

            Mockito.doThrow(mockedWlsException).when(konfigurierterWahltagControllerApi).getKonfigurierterWahltag();

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlartOfCurrentWahltag(wahlID)).isSameAs(mockedWlsException);
        }

        @Test
        void should_rethrowWlsException_when_wlsExceptionIsThrownFromWahlenApi() {
            val mockedWahltagID = "mockedWahltagID";
            val mockedKonfigurierterWahltag = new KonfigurierterWahltagDTO().wahltagID(mockedWahltagID);
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with wahlen api failed");

            Mockito.when(konfigurierterWahltagControllerApi.getKonfigurierterWahltag()).thenReturn(mockedKonfigurierterWahltag);
            Mockito.doThrow(mockedWlsException).when(wahlenControllerApi).getWahlen(mockedWahltagID);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlartOfCurrentWahltag(wahlID)).isSameAs(mockedWlsException);
        }

        public static Stream<Arguments> argumentsOfWahltagApiWithNoCurrentWahltag() {
            return Stream.of(
                    Arguments.of(null, "response is null"),
                    Arguments.of(new KonfigurierterWahltagDTO().wahltagID(null), "response has no wahltagID"));
        }
    }

}
