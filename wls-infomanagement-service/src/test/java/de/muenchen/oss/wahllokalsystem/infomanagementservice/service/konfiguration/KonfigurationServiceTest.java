package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.common.JWTHandler;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.common.security.AuthenticationHandler;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.konfiguration.Konfiguration;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.konfiguration.KonfigurationRepository;
import java.util.ArrayList;
import java.util.Optional;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class KonfigurationServiceTest {

    @Mock
    KonfigurationRepository konfigurationRepository;

    @Mock
    KonfigurationModelMapper konfigurationModelMapper;

    @Mock
    KonfigurationModelValidator konfigurationModelValidator;

    @Mock
    JWTHandler jwtHandler;

    @Spy
    ArrayList<AuthenticationHandler> authenticationHandlers;

    @InjectMocks
    KonfigurationService unitUnderTest;

    @BeforeEach
    void setup() {
        authenticationHandlers.clear();
        authenticationHandlers.add(jwtHandler);
    }

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
            Mockito.when(jwtHandler.canHandle(any())).thenReturn(true);
            Mockito.when(jwtHandler.getDetail(eq(JWT_DETAIL_WAHLBEZIRKSART_KEY), any())).thenReturn(Optional.of("BWB"));
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
            Mockito.when(jwtHandler.canHandle(any())).thenReturn(true);
            Mockito.when(jwtHandler.getDetail(eq(JWT_DETAIL_WAHLBEZIRKSART_KEY), any())).thenReturn(Optional.of("BWB"));
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
            Mockito.when(jwtHandler.canHandle(any())).thenReturn(true);
            Mockito.when(jwtHandler.getDetail(eq(JWT_DETAIL_WAHLBEZIRKSART_KEY), any())).thenReturn(Optional.of("BWB"));
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
            Mockito.when(jwtHandler.canHandle(any())).thenReturn(true);
            Mockito.when(jwtHandler.getDetail(eq(JWT_DETAIL_WAHLBEZIRKSART_KEY), any())).thenReturn(Optional.empty());
            Mockito.when(konfigurationModelMapper.getAlternativKey(keyForRequestedKonfiguration, WahlbezirkArt.UWB)).thenReturn(Optional.empty());
            Mockito.when(konfigurationModelMapper.toModel(mockedKonfigurationFromRepo)).thenReturn(mappedMockedKonfiguration);
            Mockito.when(konfigurationRepository.findById(konfigKeyAsString)).thenReturn(mockedKonfigurationFromRepoAsOptional);

            val result = unitUnderTest.getKonfiguration(keyForRequestedKonfiguration);

            Assertions.assertThat(result.get()).isSameAs(mappedMockedKonfiguration);
        }

        @Test
        void noAuthenticationHandlerFound() {
            val konfigKeyAsString = "ABSCHLUSSTEXT";
            val keyForRequestedKonfiguration = KonfigurationKonfigKey.valueOf(konfigKeyAsString);

            val mockedKonfigurationFromRepo = new Konfiguration();
            val mockedKonfigurationFromRepoAsOptional = Optional.of(mockedKonfigurationFromRepo);
            val mappedMockedKonfiguration = KonfigurationModel.builder().build();

            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("princiap", "credential"));

            Mockito.doNothing().when(konfigurationModelValidator).valideOrThrowGetKonfigurationByKey(keyForRequestedKonfiguration);
            Mockito.when(jwtHandler.canHandle(any())).thenReturn(false);
            Mockito.when(konfigurationModelMapper.getAlternativKey(keyForRequestedKonfiguration, WahlbezirkArt.UWB)).thenReturn(Optional.empty());
            Mockito.when(konfigurationModelMapper.toModel(mockedKonfigurationFromRepo)).thenReturn(mappedMockedKonfiguration);
            Mockito.when(konfigurationRepository.findById(konfigKeyAsString)).thenReturn(mockedKonfigurationFromRepoAsOptional);

            val result = unitUnderTest.getKonfiguration(keyForRequestedKonfiguration);

            Assertions.assertThat(result.get()).isSameAs(mappedMockedKonfiguration);
        }
    }

}
