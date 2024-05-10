package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.common.JWTService;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.konfiguration.Konfiguration;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.konfiguration.KonfigurationRepository;
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
class KonfigurationServiceTest {

    @Mock
    KonfigurationRepository konfigurationRepository;

    @Mock
    KonfigurationModelMapper konfigurationModelMapper;

    @Mock
    KonfigurationModelValidator konfigurationModelValidator;

    @Mock
    JWTService jwtService;

    @InjectMocks
    KonfigurationService unitUnderTest;

    @Nested
    class GetKonfiguration {

        private static final String JWT_DETAIL_WAHLBEZIRKSART_KEY = "wahlbezirksArt";

        @Test
        void konfigurationFoundInRepoWithAlternativKey() {
            val konfigKeyAsString = "ABSCHLUSSTEXT";
            val keyForRequestedKonfiguration = KonfigurationKonfigKey.valueOf(konfigKeyAsString);
            val alternativeKey = KonfigurationKonfigKey.ABSCHLUSSTEXT;

            val mockedKonfigurationFromRepo = new Konfiguration();
            val mockedKonfigurationFromRepoAsOptional = Optional.of(mockedKonfigurationFromRepo);
            val mappedMockedKonfiguration = KonfigurationModel.builder().build();

            Mockito.doNothing().when(konfigurationModelValidator).valideOrThrowGetKonfigurationByKey(alternativeKey);
            Mockito.when(jwtService.getDetail(JWT_DETAIL_WAHLBEZIRKSART_KEY)).thenReturn(Optional.of("BWB"));
            Mockito.when(konfigurationModelMapper.getAlternativKey(keyForRequestedKonfiguration, WahlbezirkArt.BWB)).thenReturn(Optional.of(alternativeKey));
            Mockito.when(konfigurationModelMapper.toModel(mockedKonfigurationFromRepo)).thenReturn(mappedMockedKonfiguration);
            Mockito.when(konfigurationRepository.findById(alternativeKey.name())).thenReturn(mockedKonfigurationFromRepoAsOptional);

            val result = unitUnderTest.getKonfiguration(keyForRequestedKonfiguration);

            Assertions.assertThat(result.get()).isSameAs(mappedMockedKonfiguration);
        }

        @Test
        void konfigurationFoundInRepoWithoutAlternativeKey() {
            val konfigKeyAsString = "ABSCHLUSSTEXT";
            val keyForRequestedKonfiguration = KonfigurationKonfigKey.valueOf(konfigKeyAsString);

            val mockedKonfigurationFromRepo = new Konfiguration();
            val mockedKonfigurationFromRepoAsOptional = Optional.of(mockedKonfigurationFromRepo);
            val mappedMockedKonfiguration = KonfigurationModel.builder().build();

            Mockito.doNothing().when(konfigurationModelValidator).valideOrThrowGetKonfigurationByKey(keyForRequestedKonfiguration);
            Mockito.when(jwtService.getDetail(JWT_DETAIL_WAHLBEZIRKSART_KEY)).thenReturn(Optional.of("BWB"));
            Mockito.when(konfigurationModelMapper.getAlternativKey(keyForRequestedKonfiguration, WahlbezirkArt.BWB)).thenReturn(Optional.empty());
            Mockito.when(konfigurationModelMapper.toModel(mockedKonfigurationFromRepo)).thenReturn(mappedMockedKonfiguration);
            Mockito.when(konfigurationRepository.findById(konfigKeyAsString)).thenReturn(mockedKonfigurationFromRepoAsOptional);

            val result = unitUnderTest.getKonfiguration(keyForRequestedKonfiguration);

            Assertions.assertThat(result.get()).isSameAs(mappedMockedKonfiguration);
        }

        @Test
        void noKonfigurationFoundInRepo() {
            val konfigKeyAsString = "ABSCHLUSSTEXT";
            val keyForRequestedKonfiguration = KonfigurationKonfigKey.valueOf(konfigKeyAsString);

            final Optional<Konfiguration> mockedKonfigurationFromRepoAsOptional = Optional.empty();

            Mockito.doNothing().when(konfigurationModelValidator).valideOrThrowGetKonfigurationByKey(keyForRequestedKonfiguration);
            Mockito.when(jwtService.getDetail(JWT_DETAIL_WAHLBEZIRKSART_KEY)).thenReturn(Optional.of("BWB"));
            Mockito.when(konfigurationModelMapper.getAlternativKey(keyForRequestedKonfiguration, WahlbezirkArt.BWB)).thenReturn(Optional.empty());
            Mockito.when(konfigurationRepository.findById(konfigKeyAsString)).thenReturn(mockedKonfigurationFromRepoAsOptional);

            val result = unitUnderTest.getKonfiguration(keyForRequestedKonfiguration);

            Assertions.assertThat(result).isEmpty();
        }

        @Test
        void validationOfParameterFailed() {
            val konfigKeyAsString = "ABSCHLUSSTEXT";
            val keyForRequestedKonfiguration = KonfigurationKonfigKey.valueOf(konfigKeyAsString);

            val validationException = new IllegalArgumentException("sth failed");

            Mockito.doThrow(validationException).when(konfigurationModelValidator).valideOrThrowGetKonfigurationByKey(keyForRequestedKonfiguration);

            Assertions.assertThatThrownBy(() -> unitUnderTest.getKonfiguration(keyForRequestedKonfiguration)).isSameAs(validationException);
        }

        @Test
        void useDefaultValueAsWahlbezirksArtWhenNotPartOfJWT() {
            val konfigKeyAsString = "ABSCHLUSSTEXT";
            val keyForRequestedKonfiguration = KonfigurationKonfigKey.valueOf(konfigKeyAsString);

            val mockedKonfigurationFromRepo = new Konfiguration();
            val mockedKonfigurationFromRepoAsOptional = Optional.of(mockedKonfigurationFromRepo);
            val mappedMockedKonfiguration = KonfigurationModel.builder().build();

            Mockito.doNothing().when(konfigurationModelValidator).valideOrThrowGetKonfigurationByKey(keyForRequestedKonfiguration);
            Mockito.when(jwtService.getDetail(JWT_DETAIL_WAHLBEZIRKSART_KEY)).thenReturn(Optional.empty());
            Mockito.when(konfigurationModelMapper.getAlternativKey(keyForRequestedKonfiguration, WahlbezirkArt.UWB)).thenReturn(Optional.empty());
            Mockito.when(konfigurationModelMapper.toModel(mockedKonfigurationFromRepo)).thenReturn(mappedMockedKonfiguration);
            Mockito.when(konfigurationRepository.findById(konfigKeyAsString)).thenReturn(mockedKonfigurationFromRepoAsOptional);

            val result = unitUnderTest.getKonfiguration(keyForRequestedKonfiguration);

            Assertions.assertThat(result.get()).isSameAs(mappedMockedKonfiguration);
        }
    }

}