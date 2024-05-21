package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlvorbereitung;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.UrnenwahlVorbereitung;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.UrnenwahlVorbereitungRepository;
import java.util.Optional;
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
class UrnenwahlvorbereitungServiceTest {

    @Mock
    UrnenwahlVorbereitungRepository urnenwahlVorbereitungRepository;

    @Mock
    UrnenwahlvorbereitungModelMapper urnenwahlvorbereitungModelMapper;

    @Mock
    UrnenwahlvorbereitungValidator urnenwahlvorbereitungValidator;

    @InjectMocks
    UrnenwahlvorbereitungService unitUnderTest;

    @Nested
    class getUrnenwahlvorbereitung {
        @Test
        void dataFound() {
            val wahlbezirkID = "wahlbezirkID";

            val mockedRepoResponse = new UrnenwahlVorbereitung();
            val mockedMappedRepoResponseAsModel = UrnenwahlvorbereitungModel.builder().build();

            Mockito.doNothing().when(urnenwahlvorbereitungValidator).validWahlbezirkIDOrThrow(wahlbezirkID);
            Mockito.when(urnenwahlVorbereitungRepository.findById(wahlbezirkID)).thenReturn(Optional.of(mockedRepoResponse));
            Mockito.when(urnenwahlvorbereitungModelMapper.toModel(mockedRepoResponse)).thenReturn(mockedMappedRepoResponseAsModel);

            val result = unitUnderTest.getUrnenwahlvorbereitung(wahlbezirkID);

            Assertions.assertThat(result.get()).isEqualTo(mockedMappedRepoResponseAsModel);
        }

        @Test
        void noDataFound() {
            val wahlbezirkID = "wahlbezirkID";

            Mockito.doNothing().when(urnenwahlvorbereitungValidator).validWahlbezirkIDOrThrow(wahlbezirkID);
            Mockito.when(urnenwahlVorbereitungRepository.findById(wahlbezirkID)).thenReturn(Optional.empty());

            val result = unitUnderTest.getUrnenwahlvorbereitung(wahlbezirkID);

            Assertions.assertThat(result).isEmpty();

            Mockito.verify(urnenwahlvorbereitungModelMapper, times(0)).toModel(any());
        }

        @Test
        void exceptionFromValidator() {
            val wahlbezirkID = "wahlbezirkID";

            val mockedValidatorException = new RuntimeException("validation failed");

            Mockito.doThrow(mockedValidatorException).when(urnenwahlvorbereitungValidator).validWahlbezirkIDOrThrow(wahlbezirkID);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getUrnenwahlvorbereitung(wahlbezirkID)).isSameAs(mockedValidatorException);
        }
    }

}
