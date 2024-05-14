package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.common.JWTHandler;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.common.security.AuthenticationHandler;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.konfiguration.Konfiguration;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.konfiguration.KonfigurationRepository;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ServiceIDFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
    ServiceIDFormatter serviceIDFormatter;

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

    @Nested
    class SetKonfiguration {

        @Test
        void isSaved() {
            val konfigurationSetModel = KonfigurationSetModel.builder().build();

            val mockedKonfigurationEntity = new Konfiguration();

            Mockito.doNothing().when(konfigurationModelValidator).valideOrThrowSetKonfiguration(konfigurationSetModel);
            Mockito.when(konfigurationModelMapper.toEntity(konfigurationSetModel)).thenReturn(mockedKonfigurationEntity);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.setKonfiguration(konfigurationSetModel));

            Mockito.verify(konfigurationRepository).save(mockedKonfigurationEntity);
        }

        @Test
        void exceptionFromValidationIsUnhandled() {
            val konfigurationSetModel = KonfigurationSetModel.builder().build();

            val mockedValidatorException = new IllegalArgumentException("WRONG!!!");

            Mockito.doThrow(mockedValidatorException).when(konfigurationModelValidator).valideOrThrowSetKonfiguration(konfigurationSetModel);

            Assertions.assertThatThrownBy(() -> unitUnderTest.setKonfiguration(konfigurationSetModel)).isSameAs(mockedValidatorException);
        }

        @Test
        void exceptionFromRepositoryIsHandled() {
            val konfigurationSetModel = KonfigurationSetModel.builder().build();

            val mockedRepositoryException = new IllegalArgumentException("i cant saved");
            val mockedKonfigurationEntity = new Konfiguration();
            val mockedServiceID = "serviceID";

            Mockito.doNothing().when(konfigurationModelValidator).valideOrThrowSetKonfiguration(konfigurationSetModel);
            Mockito.when(konfigurationModelMapper.toEntity(konfigurationSetModel)).thenReturn(mockedKonfigurationEntity);
            Mockito.when(konfigurationRepository.save(mockedKonfigurationEntity)).thenThrow(mockedRepositoryException);
            Mockito.when(serviceIDFormatter.getId()).thenReturn(mockedServiceID);

            val exceptionThrown = Assertions.catchException(() -> unitUnderTest.setKonfiguration(konfigurationSetModel));

            val expectedException = TechnischeWlsException.withCode("101").inService(mockedServiceID).buildWithMessage("");

            Assertions.assertThat(exceptionThrown).usingRecursiveComparison().ignoringFields("message").isEqualTo(expectedException);
            Assertions.assertThat(exceptionThrown.getMessage()).isNotNull();
        }
    }

    @Nested
    class GetAllKonfigurations {

        @Test
        void dataFromRepository() {
            val mockedRepositoryResponse = List.of(new Konfiguration(), new Konfiguration());
            val mockedMappedEntityAsModel = KonfigurationModel.builder().build();

            Mockito.when(konfigurationRepository.findAll()).thenReturn(mockedRepositoryResponse);
            Mockito.when(konfigurationModelMapper.toModel(any())).thenReturn(mockedMappedEntityAsModel);

            val result = unitUnderTest.getAllKonfigurations();

            Assertions.assertThat(result).hasSize(mockedRepositoryResponse.size());
        }

        @Test
        void noDataFromRepository() {

            Mockito.when(konfigurationRepository.findAll()).thenReturn(Collections.emptyList());

            val result = unitUnderTest.getAllKonfigurations();

            Assertions.assertThat(result).isEmpty();
        }
    }

    @Nested
    class GetKennbuchstabenListen {

        @Test
        void kennbuchstabenFound() {
            val konfigurationWert = "wert";
            val mockedRepoResponse = new Konfiguration();
            mockedRepoResponse.setWert(konfigurationWert);
            val mockedMappedEntityAsModel = KennbuchstabenListenModel.builder().build();

            Mockito.when(konfigurationRepository.findById("KENNBUCHSTABEN")).thenReturn(Optional.of(mockedRepoResponse));
            Mockito.when(konfigurationModelMapper.toKennbuchstabenListenModel(konfigurationWert)).thenReturn(mockedMappedEntityAsModel);

            val result = unitUnderTest.getKennbuchstabenListen();

            Assertions.assertThat(result).isSameAs(mockedMappedEntityAsModel);
        }

        @Test
        void exceptionWhenNoKennbuchtabenFoundInRepo() {
            val mockedServiceID = "serviceID";

            Mockito.when(serviceIDFormatter.getId()).thenReturn(mockedServiceID);
            Mockito.when(konfigurationRepository.findById("KENNBUCHSTABEN")).thenReturn(Optional.empty());

            val exceptionThrown = Assertions.catchException(() -> unitUnderTest.getKennbuchstabenListen());

            val expectedException = FachlicheWlsException.withCode("103").inService(mockedServiceID).buildWithMessage("");

            Assertions.assertThat(exceptionThrown).usingRecursiveComparison().ignoringFields("message").isEqualTo(expectedException);
            Assertions.assertThat(exceptionThrown.getMessage()).isNotNull();

        }
    }

    @Nested
    class GetKonfigurationUnauthorized {

        private static final List<KonfigurationKonfigKey> SUPPORTED_KEYS = List.of(KonfigurationKonfigKey.FRUEHESTE_LOGIN_UHRZEIT,
                KonfigurationKonfigKey.SPAETESTE_LOGIN_UHRZEIT, KonfigurationKonfigKey.WILLKOMMENSTEXT);

        @ParameterizedTest
        @MethodSource("getNonSupporetedKeys")
        void verifyRepoNotCalled(final KonfigurationKonfigKey konfigurationKonfigKey) {
            val result = unitUnderTest.getKonfigurationUnauthorized(konfigurationKonfigKey);

            Assertions.assertThat(result).isEmpty();
            Mockito.verifyNoInteractions(konfigurationRepository);
        }

        @Test
        void whenParameterIsNullThenReturnsEmpty() {
            Assertions.assertThat(unitUnderTest.getKonfigurationUnauthorized(null)).isEmpty();
        }

        @Test
        void fruehesteLoginUhrzeitIsReadFromRepo() {
            val mockedRepoResponse = new Konfiguration();
            val mockedMappedEntityAsModel = KonfigurationModel.builder().build();

            Mockito.when(konfigurationRepository.getFruehesteLoginUhrzeit()).thenReturn(Optional.of(mockedRepoResponse));
            Mockito.when(konfigurationModelMapper.toModel(mockedRepoResponse)).thenReturn(mockedMappedEntityAsModel);

            val result = unitUnderTest.getKonfigurationUnauthorized(KonfigurationKonfigKey.FRUEHESTE_LOGIN_UHRZEIT);

            Assertions.assertThat(result.get()).isSameAs(mockedMappedEntityAsModel);
        }

        @Test
        void spaetesteLoginUhrzeitIsReadFromRepo() {
            val mockedRepoResponse = new Konfiguration();
            val mockedMappedEntityAsModel = KonfigurationModel.builder().build();

            Mockito.when(konfigurationRepository.getSpaetesteLoginUhrzeit()).thenReturn(Optional.of(mockedRepoResponse));
            Mockito.when(konfigurationModelMapper.toModel(mockedRepoResponse)).thenReturn(mockedMappedEntityAsModel);

            val result = unitUnderTest.getKonfigurationUnauthorized(KonfigurationKonfigKey.SPAETESTE_LOGIN_UHRZEIT);

            Assertions.assertThat(result.get()).isSameAs(mockedMappedEntityAsModel);
        }

        @Test
        void willkommenstextIsReadFromRepo() {
            val mockedRepoResponse = new Konfiguration();
            val mockedMappedEntityAsModel = KonfigurationModel.builder().build();

            Mockito.when(konfigurationRepository.getWillkommenstext()).thenReturn(Optional.of(mockedRepoResponse));
            Mockito.when(konfigurationModelMapper.toModel(mockedRepoResponse)).thenReturn(mockedMappedEntityAsModel);

            val result = unitUnderTest.getKonfigurationUnauthorized(KonfigurationKonfigKey.WILLKOMMENSTEXT);

            Assertions.assertThat(result.get()).isSameAs(mockedMappedEntityAsModel);
        }

        private static Stream<Arguments> getNonSupporetedKeys() {
            return Arrays.stream(KonfigurationKonfigKey.values()).filter(konfigKey -> !SUPPORTED_KEYS.contains(konfigKey)).map(Arguments::of);
        }

        private static Stream<Arguments> getSupportedKeys() {
            return SUPPORTED_KEYS.stream().map(Arguments::of);
        }

    }

}
