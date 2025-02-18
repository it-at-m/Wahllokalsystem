package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.rest.wahlvorstand;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.WahlvorstandService;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.utils.TestDataFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
public class WahlvorstandControllerTest {

    @Mock
    WahlvorstandService wahlvorstandService;

    @Mock
    WahlvorstandDTOMapper wahlvorstandDTOMapper;

    @InjectMocks
    WahlvorstandController unitUnderTest;

    @Nested
    class GetWahlvorstand {

        @ParameterizedTest(name = "{index} - forceUpdate is {0}")
        @MethodSource("getForceUpdateHeaderVariations")
        void should_returnWahlvorstandDTO_when_givenValidWahlbezirkIdAndForceUpdateIsFalseorNull(final ArgumentsAccessor argumentsAccessor) {
            val wahlbezirkID = "wahlbezirkID";
            val forceUpdate = argumentsAccessor.get(0, Boolean.class);
            val mockedWahlvorstandModel = TestDataFactory.CreateWahlvorstandModel.withData();
            val expectedWahlvorstandDto = TestDataFactory.CreateWahlvorstandDto.fromModel(mockedWahlvorstandModel);

            Mockito.when(wahlvorstandService.getWahlvorstand(wahlbezirkID)).thenReturn(Optional.of(mockedWahlvorstandModel));
            Mockito.when(wahlvorstandDTOMapper.toDTO(mockedWahlvorstandModel)).thenReturn(expectedWahlvorstandDto);

            val result = unitUnderTest.getWahlvorstand(forceUpdate, wahlbezirkID);
            Assertions.assertThat(result.getBody()).isEqualTo(expectedWahlvorstandDto);
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        void should_returnWahlvorstandDTO_when_givenValidWahlbezirkIdAndForceUpdateIsTrue() {
            val wahlbezirkID = "wahlbezirkID";
            val forceUpdate = true;
            val mockedWahlvorstandModel = TestDataFactory.CreateWahlvorstandModel.withData();
            val expectedWahlvorstandDto = TestDataFactory.CreateWahlvorstandDto.fromModel(mockedWahlvorstandModel);

            Mockito.when(wahlvorstandService.updateWahlvorstand(wahlbezirkID)).thenReturn(Optional.of(mockedWahlvorstandModel));
            Mockito.when(wahlvorstandDTOMapper.toDTO(mockedWahlvorstandModel)).thenReturn(expectedWahlvorstandDto);

            val result = unitUnderTest.getWahlvorstand(forceUpdate, wahlbezirkID);
            Assertions.assertThat(result.getBody()).isEqualTo(expectedWahlvorstandDto);
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        void should_returnFallbackWahlvorstandDTO_when_WahlvorstandDoesNotExist() {
            val wahlbezirkID = "wahlbezirkID";
            val forceUpdate = false;
            val mockedFallbackWahlvorstandModel = TestDataFactory.CreateWahlvorstandModel.fallback(wahlbezirkID);
            val expectedWahlvorstandDto = TestDataFactory.CreateWahlvorstandDto.fromModel(mockedFallbackWahlvorstandModel);

            Mockito.when(wahlvorstandService.getWahlvorstand(wahlbezirkID)).thenReturn(Optional.empty());
            Mockito.when(wahlvorstandService.getFallbackWahlvorstand(wahlbezirkID)).thenReturn(Optional.of(mockedFallbackWahlvorstandModel));
            Mockito.when(wahlvorstandDTOMapper.toDTO(mockedFallbackWahlvorstandModel)).thenReturn(expectedWahlvorstandDto);

            val result = unitUnderTest.getWahlvorstand(forceUpdate, wahlbezirkID);
            Assertions.assertThat(result.getBody()).isEqualTo(expectedWahlvorstandDto);
            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        private static List<Boolean> getForceUpdateHeaderVariations() {
            return Arrays.asList(false, null);
        }
    }

    @Nested
    class PostWahlvorstand {

        @Test
        void should_notThrowException_when_newDataSaved() {
            val wahlbezirkID = "wahlbezirkID";
            val mockedWahlvorstandDto = TestDataFactory.CreateWahlvorstandDto.withData();
            val mockedWahlvorstandModel = TestDataFactory.CreateWahlvorstandModel.fromDto(wahlbezirkID, mockedWahlvorstandDto);

            Mockito.when(wahlvorstandDTOMapper.toModel(wahlbezirkID, mockedWahlvorstandDto)).thenReturn(mockedWahlvorstandModel);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postWahlvorstand(wahlbezirkID, mockedWahlvorstandDto));
            Mockito.verify(wahlvorstandService).postWahlvorstand(mockedWahlvorstandModel);
        }
    }
}
