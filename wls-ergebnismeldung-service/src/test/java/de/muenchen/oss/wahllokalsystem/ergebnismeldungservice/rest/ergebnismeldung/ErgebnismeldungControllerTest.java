package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ergebnismeldung;

import static org.mockito.ArgumentMatchers.any;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.common.MeldungsartDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.ErgebnismeldungService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.ErgebnisseToSendCriteriaModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.DTOMapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ErgebnismeldungControllerTest {

    @Mock
    ErgebnismeldungService ergebnismeldungService;

    @Mock
    DTOMapper dtoMapper;

    @InjectMocks
    ErgebnismeldungController unitUnderTest;

    @Nested
    class SendErgebnisse {

        @Test
        void should_callUpdateSendungszeiten_when_forceIsTrueInAllLowerCase() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            unitUnderTest.sendErgebnisse("true", wahlID, wahlbezirkID, null, null, null);

            Mockito.verify(ergebnismeldungService).updateSendungszeiten(new BezirkUndWahlID(wahlID, wahlbezirkID));
        }

        @Test
        void should_callUpdateSendungszeiten_when_forceIsTrueInAllUpperCase() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            unitUnderTest.sendErgebnisse("TRUE", wahlID, wahlbezirkID, null, null, null);

            Mockito.verify(ergebnismeldungService).updateSendungszeiten(new BezirkUndWahlID(wahlID, wahlbezirkID));
        }

        @ParameterizedTest
        @MethodSource("argumentsThatRepresentForceIsNotSet")
        void should_callSendErgebnisse_when_forceUpdateIsNotSet(final ArgumentsAccessor arguments) {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val waehlverzeichnisNummer = 1L;
            val meldungsart = MeldungsartDTO.V1;
            val hauptwahlbezirk = "hauptwahlbezirk";

            unitUnderTest.sendErgebnisse(arguments.get(0, String.class), wahlID, wahlbezirkID, waehlverzeichnisNummer, meldungsart, hauptwahlbezirk);

            Mockito.verify(ergebnismeldungService)
                    .sendErgebnisse(new ErgebnisseToSendCriteriaModel(wahlID, wahlbezirkID, waehlverzeichnisNummer, MeldungsartModel.V1, hauptwahlbezirk));
        }

        @Test
        void should_returnHttpStatusOk_when_ergebnismeldungServiceReturnTrue() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val waehlverzeichnisNummer = 1L;
            val meldungsart = MeldungsartDTO.V1;
            val hauptwahlbezirk = "hauptwahlbezirk";

            Mockito.when(ergebnismeldungService.sendErgebnisse(any())).thenReturn(true);

            val result = unitUnderTest.sendErgebnisse("false", wahlID, wahlbezirkID, waehlverzeichnisNummer, meldungsart, hauptwahlbezirk);

            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        void should_returnHttpStatusConflict_when_ergebnismeldungServiceReturnFalse() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val waehlverzeichnisNummer = 1L;
            val meldungsart = MeldungsartDTO.V1;
            val hauptwahlbezirk = "hauptwahlbezirk";

            Mockito.when(ergebnismeldungService.sendErgebnisse(any())).thenReturn(false);

            val result = unitUnderTest.sendErgebnisse("false", wahlID, wahlbezirkID, waehlverzeichnisNummer, meldungsart, hauptwahlbezirk);

            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        }

        @Test
        void should_returnHttpStatusConflictWithBody_when_wlsExceptionOccurredInErgebnismeldungService() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val waehlverzeichnisNummer = 1L;
            val meldungsart = MeldungsartDTO.V1;
            val hauptwahlbezirk = "hauptwahlbezirk";

            val mockedWlsException = FachlicheWlsException.withCode("123").inService("service").buildWithMessage("error");
            val mockedWlsExceptionAsDTO = new WlsExceptionDTO(WlsExceptionCategory.F, "123", "service", "error");

            Mockito.doThrow(mockedWlsException).when(ergebnismeldungService).sendErgebnisse(any());
            Mockito.when(dtoMapper.toDTO(mockedWlsException)).thenReturn(mockedWlsExceptionAsDTO);

            val result = unitUnderTest.sendErgebnisse("false", wahlID, wahlbezirkID, waehlverzeichnisNummer, meldungsart, hauptwahlbezirk);

            Assertions.assertThat(result).isEqualTo(ResponseEntity.status(HttpStatus.CONFLICT).body(mockedWlsExceptionAsDTO));
        }

        @Test
        void should_returnHttpStatusConflict_when_nonWlsExceptionOccurredInErgebnismeldungService() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val waehlverzeichnisNummer = 1L;
            val meldungsart = MeldungsartDTO.V1;
            val hauptwahlbezirk = "hauptwahlbezirk";

            Mockito.doThrow(new IllegalArgumentException("sth failed")).when(ergebnismeldungService).sendErgebnisse(any());

            val result = unitUnderTest.sendErgebnisse("false", wahlID, wahlbezirkID, waehlverzeichnisNummer, meldungsart, hauptwahlbezirk);

            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        }

        public static Stream<Arguments> argumentsThatRepresentForceIsNotSet() {
            return Stream.of(
                    Arguments.of("false"),
                    Arguments.of("FALSE"),
                    Arguments.of("not a true value"),
                    Arguments.of((String) null));
        }
    }
}
