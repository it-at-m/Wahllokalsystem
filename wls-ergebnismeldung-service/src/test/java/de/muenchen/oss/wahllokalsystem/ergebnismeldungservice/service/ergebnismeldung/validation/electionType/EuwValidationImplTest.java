package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.validation.electionType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.WahlartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.validation.DefaultElectionTypeValidator;
import java.util.Arrays;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EuwValidationImplTest {

    @Mock
    DefaultElectionTypeValidator defaultElectionTypeValidator;

    @InjectMocks
    EuwValidationImpl unitUnderTest;

    @Captor
    ArgumentCaptor<List<Stapelart>> captorStapelList;

    @Nested
    class Supports {

        @Test
        void should_returnTrue_when_wahlartIsEUW() {
            Assertions.assertThat(unitUnderTest.supports(WahlartModel.EUW)).isTrue();
        }

        @ParameterizedTest
        @MethodSource("argumentsForNonEUWWahlart")
        void should_returnFalse_when_wahlartIsNotEUW(final ArgumentsAccessor arguments) {
            Assertions.assertThat(unitUnderTest.supports(arguments.get(0, WahlartModel.class))).isFalse();
        }

        public static Stream<Arguments> argumentsForNonEUWWahlart() {
            return Arrays.stream(WahlartModel.values()).filter(wahlart -> !WahlartModel.EUW.equals(wahlart)).map(Arguments::of);
        }
    }

    @Nested
    class IsValidUwb {

        @Test
        void should_callDefaultValidatorWithBTWStapel_when_isCalled() {
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";
            val waehlerverzeichnisNummer = 0L;
            val meldungsart = MeldungsartModel.V1;

            unitUnderTest.isValidUwb(wahlbezirkID, wahlID, waehlerverzeichnisNummer, meldungsart);

            Mockito.verify(defaultElectionTypeValidator)
                    .checkValidation(eq(WahlbezirkArtModel.UWB), eq(wahlbezirkID), eq(wahlID), eq(waehlerverzeichnisNummer), captorStapelList.capture());

            val expectedStapel = Arrays.stream(Stapelart.values())
                    .filter(stapelart -> !Stapelart.EUW_B_LEER.equals(stapelart))
                    .filter(stapelart -> stapelart.name().startsWith("EUW_"))
                    .toList().toArray(new Stapelart[0]);

            Assertions.assertThat(captorStapelList.getValue()).containsExactlyInAnyOrder(expectedStapel);
        }

        @Test
        void should_returnResponseOfDefaultValidator_when_isCalled() {
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";
            val waehlerverzeichnisNummer = 0L;
            val meldungsart = MeldungsartModel.V1;

            val mockedValidatorResponse = true;
            Mockito.when(defaultElectionTypeValidator.checkValidation(eq(WahlbezirkArtModel.UWB), anyString(), anyString(), any(), any()))
                    .thenReturn(mockedValidatorResponse);

            val result = unitUnderTest.isValidUwb(wahlbezirkID, wahlID, waehlerverzeichnisNummer, meldungsart);

            Assertions.assertThat(result).isEqualTo(mockedValidatorResponse);
        }
    }

    @Nested
    class IsValidBwb {

        @Test
        void should_callDefaultValidatorWithBTWStapel_when_isCalled() {
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";
            val waehlerverzeichnisNummer = 0L;
            val meldungsart = MeldungsartModel.V1;

            unitUnderTest.isValidBwb(wahlbezirkID, wahlID, waehlerverzeichnisNummer, meldungsart);

            Mockito.verify(defaultElectionTypeValidator)
                    .checkValidation(eq(WahlbezirkArtModel.BWB), eq(wahlbezirkID), eq(wahlID), eq(waehlerverzeichnisNummer), captorStapelList.capture());

            val expectedStapel = Arrays.stream(Stapelart.values())
                    .filter(stapelart -> stapelart.name().startsWith("EUW_"))
                    .toList().toArray(new Stapelart[0]);

            Assertions.assertThat(captorStapelList.getValue()).containsExactlyInAnyOrder(expectedStapel);
        }

        @Test
        void should_returnResponseOfDefaultValidator_when_isCalled() {
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";
            val waehlerverzeichnisNummer = 0L;
            val meldungsart = MeldungsartModel.V1;

            val mockedValidatorResponse = true;
            Mockito.when(defaultElectionTypeValidator.checkValidation(eq(WahlbezirkArtModel.BWB), anyString(), anyString(), any(), any()))
                    .thenReturn(mockedValidatorResponse);

            val result = unitUnderTest.isValidBwb(wahlbezirkID, wahlID, waehlerverzeichnisNummer, meldungsart);

            Assertions.assertThat(result).isEqualTo(mockedValidatorResponse);
        }
    }

}
