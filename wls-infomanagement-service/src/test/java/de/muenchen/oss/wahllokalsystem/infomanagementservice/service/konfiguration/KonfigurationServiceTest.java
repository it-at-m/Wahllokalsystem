package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.common.security.AuthenticationHandler;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.common.security.JWTHandler;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.konfiguration.Konfiguration;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.konfiguration.KonfigurationRepository;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KennbuchstabenListenModel;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KonfigurationKonfigKey;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KonfigurationModel;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KonfigurationSetModel;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.WahlbezirkArt;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
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
    ExceptionFactory exceptionFactory;

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
        void should_returnKonfigurationModel_when_foundInRepoWithAlternativKey() {
            val konfigKeyAsString = "ABSCHLUSSTEXT";
            val keyForRequestedKonfiguration = KonfigurationKonfigKey.valueOf(konfigKeyAsString);
            val alternativeKey = KonfigurationKonfigKey.ABSCHLUSSTEXT;

            val mockedKonfigurationFromRepo = new Konfiguration();
            val mockedKonfigurationFromRepoAsOptional = Optional.of(mockedKonfigurationFromRepo);
            val mappedMockedKonfiguration = KonfigurationModel.builder().build();

            Mockito.doNothing().when(konfigurationModelValidator).validOrThrowGetKonfigurationByKey(alternativeKey);
            Mockito.when(jwtHandler.canHandle(any())).thenReturn(true);
            Mockito.when(jwtHandler.getDetail(eq(JWT_DETAIL_WAHLBEZIRKSART_KEY), any())).thenReturn(Optional.of("BWB"));
            Mockito.when(konfigurationModelMapper.getAlternativeKey(keyForRequestedKonfiguration, WahlbezirkArt.BWB)).thenReturn(Optional.of(alternativeKey));
            Mockito.when(konfigurationModelMapper.toModel(mockedKonfigurationFromRepo)).thenReturn(mappedMockedKonfiguration);
            Mockito.when(konfigurationRepository.findById(alternativeKey.name())).thenReturn(mockedKonfigurationFromRepoAsOptional);

            val result = unitUnderTest.getKonfiguration(keyForRequestedKonfiguration);

            Assertions.assertThat(result.get()).isSameAs(mappedMockedKonfiguration);
        }

        @Test
        void should_returnKonfigurationModel_when_foundInRepoWithoutAlternativeKey() {
            val konfigKeyAsString = "ABSCHLUSSTEXT";
            val keyForRequestedKonfiguration = KonfigurationKonfigKey.valueOf(konfigKeyAsString);

            val mockedKonfigurationFromRepo = new Konfiguration();
            val mockedKonfigurationFromRepoAsOptional = Optional.of(mockedKonfigurationFromRepo);
            val mappedMockedKonfiguration = KonfigurationModel.builder().build();

            Mockito.doNothing().when(konfigurationModelValidator).validOrThrowGetKonfigurationByKey(keyForRequestedKonfiguration);
            Mockito.when(jwtHandler.canHandle(any())).thenReturn(true);
            Mockito.when(jwtHandler.getDetail(eq(JWT_DETAIL_WAHLBEZIRKSART_KEY), any())).thenReturn(Optional.of("BWB"));
            Mockito.when(konfigurationModelMapper.getAlternativeKey(keyForRequestedKonfiguration, WahlbezirkArt.BWB)).thenReturn(Optional.empty());
            Mockito.when(konfigurationModelMapper.toModel(mockedKonfigurationFromRepo)).thenReturn(mappedMockedKonfiguration);
            Mockito.when(konfigurationRepository.findById(konfigKeyAsString)).thenReturn(mockedKonfigurationFromRepoAsOptional);

            val result = unitUnderTest.getKonfiguration(keyForRequestedKonfiguration);

            Assertions.assertThat(result.get()).isSameAs(mappedMockedKonfiguration);
        }

        @Test
        void should_returnEmptyOptional_when_repoIsEmpty() {
            val konfigKeyAsString = "ABSCHLUSSTEXT";
            val keyForRequestedKonfiguration = KonfigurationKonfigKey.valueOf(konfigKeyAsString);

            final Optional<Konfiguration> mockedKonfigurationFromRepoAsOptional = Optional.empty();

            Mockito.doNothing().when(konfigurationModelValidator).validOrThrowGetKonfigurationByKey(keyForRequestedKonfiguration);
            Mockito.when(jwtHandler.canHandle(any())).thenReturn(true);
            Mockito.when(jwtHandler.getDetail(eq(JWT_DETAIL_WAHLBEZIRKSART_KEY), any())).thenReturn(Optional.of("BWB"));
            Mockito.when(konfigurationModelMapper.getAlternativeKey(keyForRequestedKonfiguration, WahlbezirkArt.BWB)).thenReturn(Optional.empty());
            Mockito.when(konfigurationRepository.findById(konfigKeyAsString)).thenReturn(mockedKonfigurationFromRepoAsOptional);

            val result = unitUnderTest.getKonfiguration(keyForRequestedKonfiguration);

            Assertions.assertThat(result).isEmpty();
        }

        @Test
        void should_throwException_when_validationFails() {
            val konfigKeyAsString = "ABSCHLUSSTEXT";
            val keyForRequestedKonfiguration = KonfigurationKonfigKey.valueOf(konfigKeyAsString);

            val validationException = new IllegalArgumentException("sth failed");

            Mockito.doThrow(validationException).when(konfigurationModelValidator).validOrThrowGetKonfigurationByKey(keyForRequestedKonfiguration);

            Assertions.assertThatThrownBy(() -> unitUnderTest.getKonfiguration(keyForRequestedKonfiguration)).isSameAs(validationException);
        }

        @Test
        void should_returnKonfigurationModel_when_wahlbezirksArtISNotPartOfJWT() {
            val konfigKeyAsString = "ABSCHLUSSTEXT";
            val keyForRequestedKonfiguration = KonfigurationKonfigKey.valueOf(konfigKeyAsString);

            val mockedKonfigurationFromRepo = new Konfiguration();
            val mockedKonfigurationFromRepoAsOptional = Optional.of(mockedKonfigurationFromRepo);
            val mappedMockedKonfiguration = KonfigurationModel.builder().build();

            Mockito.doNothing().when(konfigurationModelValidator).validOrThrowGetKonfigurationByKey(keyForRequestedKonfiguration);
            Mockito.when(jwtHandler.canHandle(any())).thenReturn(true);
            Mockito.when(jwtHandler.getDetail(eq(JWT_DETAIL_WAHLBEZIRKSART_KEY), any())).thenReturn(Optional.empty());
            Mockito.when(konfigurationModelMapper.getAlternativeKey(keyForRequestedKonfiguration, WahlbezirkArt.UWB)).thenReturn(Optional.empty());
            Mockito.when(konfigurationModelMapper.toModel(mockedKonfigurationFromRepo)).thenReturn(mappedMockedKonfiguration);
            Mockito.when(konfigurationRepository.findById(konfigKeyAsString)).thenReturn(mockedKonfigurationFromRepoAsOptional);

            val result = unitUnderTest.getKonfiguration(keyForRequestedKonfiguration);

            Assertions.assertThat(result.get()).isSameAs(mappedMockedKonfiguration);
        }

        @Test
        void should_returnKonfigurationModel_when_noAuthenticationHandlerIsFound() {
            val konfigKeyAsString = "ABSCHLUSSTEXT";
            val keyForRequestedKonfiguration = KonfigurationKonfigKey.valueOf(konfigKeyAsString);

            val mockedKonfigurationFromRepo = new Konfiguration();
            val mockedKonfigurationFromRepoAsOptional = Optional.of(mockedKonfigurationFromRepo);
            val mappedMockedKonfiguration = KonfigurationModel.builder().build();

            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("princiap", "credential"));

            Mockito.doNothing().when(konfigurationModelValidator).validOrThrowGetKonfigurationByKey(keyForRequestedKonfiguration);
            Mockito.when(jwtHandler.canHandle(any())).thenReturn(false);
            Mockito.when(konfigurationModelMapper.getAlternativeKey(keyForRequestedKonfiguration, WahlbezirkArt.UWB)).thenReturn(Optional.empty());
            Mockito.when(konfigurationModelMapper.toModel(mockedKonfigurationFromRepo)).thenReturn(mappedMockedKonfiguration);
            Mockito.when(konfigurationRepository.findById(konfigKeyAsString)).thenReturn(mockedKonfigurationFromRepoAsOptional);

            val result = unitUnderTest.getKonfiguration(keyForRequestedKonfiguration);

            Assertions.assertThat(result.get()).isSameAs(mappedMockedKonfiguration);
        }
    }

    @Nested
    class SetKonfiguration {

        @Test
        void should_saveKonfiguration_when_called() {
            val konfigurationSetModel = KonfigurationSetModel.builder().build();

            val mockedKonfigurationEntity = new Konfiguration();

            Mockito.doNothing().when(konfigurationModelValidator).validOrThrowSetKonfiguration(konfigurationSetModel);
            Mockito.when(konfigurationModelMapper.toEntity(konfigurationSetModel)).thenReturn(mockedKonfigurationEntity);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.setKonfiguration(konfigurationSetModel));

            Mockito.verify(konfigurationRepository).save(mockedKonfigurationEntity);
        }

        @Test
        void should_throwException_when_validationFails() {
            val konfigurationSetModel = KonfigurationSetModel.builder().build();

            val mockedValidatorException = new IllegalArgumentException("WRONG!!!");

            Mockito.doThrow(mockedValidatorException).when(konfigurationModelValidator).validOrThrowSetKonfiguration(konfigurationSetModel);

            Assertions.assertThatThrownBy(() -> unitUnderTest.setKonfiguration(konfigurationSetModel)).isSameAs(mockedValidatorException);
        }

        @Test
        void should_throwTechnischeWlsException_when_repoFails() {
            val konfigurationSetModel = KonfigurationSetModel.builder().build();

            val mockedRepositoryException = new IllegalArgumentException("i cant saved");
            val mockedExceptionFactoryWlsException = TechnischeWlsException.withCode("").buildWithMessage("");
            val mockedKonfigurationEntity = new Konfiguration();

            Mockito.doNothing().when(konfigurationModelValidator).validOrThrowSetKonfiguration(konfigurationSetModel);
            Mockito.when(konfigurationModelMapper.toEntity(konfigurationSetModel)).thenReturn(mockedKonfigurationEntity);
            Mockito.when(konfigurationRepository.save(mockedKonfigurationEntity)).thenThrow(mockedRepositoryException);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.POSTKONFIGURATION_NOT_SAVEABLE))
                    .thenReturn(mockedExceptionFactoryWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setKonfiguration(konfigurationSetModel))
                    .isSameAs(mockedExceptionFactoryWlsException);
        }
    }

    @Nested
    class GetAllKonfigurations {

        @Test
        void should_returnAllKonfigurationModel_when_dataFoundInRepo() {
            val mockedRepositoryResponse = List.of(new Konfiguration(), new Konfiguration());
            val mockedMappedEntityAsModel = KonfigurationModel.builder().build();

            Mockito.when(konfigurationRepository.findAll()).thenReturn(mockedRepositoryResponse);
            Mockito.when(konfigurationModelMapper.toModel(any())).thenReturn(mockedMappedEntityAsModel);

            val result = unitUnderTest.getAllKonfigurations();

            Assertions.assertThat(result).hasSize(mockedRepositoryResponse.size());
        }

        @Test
        void should_returnEmptyList_when_noDataFoundInRepo() {
            Mockito.when(konfigurationRepository.findAll()).thenReturn(Collections.emptyList());

            val result = unitUnderTest.getAllKonfigurations();

            Assertions.assertThat(result).isEmpty();
        }
    }

    @Nested
    class GetKennbuchstabenListen {

        @Test
        void should_returnKennbuchstabenListenModel_when_dataFoundInRepo() {
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
        void should_returnFachlicheWlsException_when_noDataFoundInRepo() {
            val mockedExceptionFactoryWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(konfigurationRepository.findById("KENNBUCHSTABEN")).thenReturn(Optional.empty());
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETKENNBUCHSTABENLISTEN_KONFIGURATION_NOT_FOUND))
                    .thenReturn(mockedExceptionFactoryWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getKennbuchstabenListen()).isSameAs(mockedExceptionFactoryWlsException);

        }
    }

    @Nested
    class GetKonfigurationUnauthorized {

        private static final List<KonfigurationKonfigKey> SUPPORTED_KEYS = List.of(KonfigurationKonfigKey.FRUEHESTE_LOGIN_UHRZEIT,
                KonfigurationKonfigKey.SPAETESTE_LOGIN_UHRZEIT, KonfigurationKonfigKey.WILLKOMMENSTEXT);

        @ParameterizedTest
        @MethodSource("getNonSupportedKeys")
        void should_verifyThatRepoIsNotCalled_when_nonSupportedKeysAreGiven(final KonfigurationKonfigKey konfigurationKonfigKey) {
            val result = unitUnderTest.getKonfigurationUnauthorized(konfigurationKonfigKey);

            Assertions.assertThat(result).isEmpty();
            Mockito.verifyNoInteractions(konfigurationRepository);
        }

        @Test
        void should_returnEmptyOptional_when_nullIsGiven() {
            Assertions.assertThat(unitUnderTest.getKonfigurationUnauthorized(null)).isEmpty();
        }

        @Test
        void should_returnKonfigurationModel_when_fruehesteLoginUhrzeitIsGiven() {
            val mockedRepoResponse = new Konfiguration();
            val mockedMappedEntityAsModel = KonfigurationModel.builder().build();

            Mockito.when(konfigurationRepository.getFruehesteLoginUhrzeit()).thenReturn(Optional.of(mockedRepoResponse));
            Mockito.when(konfigurationModelMapper.toModel(mockedRepoResponse)).thenReturn(mockedMappedEntityAsModel);

            val result = unitUnderTest.getKonfigurationUnauthorized(KonfigurationKonfigKey.FRUEHESTE_LOGIN_UHRZEIT);

            Assertions.assertThat(result.get()).isSameAs(mockedMappedEntityAsModel);
        }

        @Test
        void should_returnKonfigurationModel_when_spaetesteLoginUhrzeitIsGiven() {
            val mockedRepoResponse = new Konfiguration();
            val mockedMappedEntityAsModel = KonfigurationModel.builder().build();

            Mockito.when(konfigurationRepository.getSpaetesteLoginUhrzeit()).thenReturn(Optional.of(mockedRepoResponse));
            Mockito.when(konfigurationModelMapper.toModel(mockedRepoResponse)).thenReturn(mockedMappedEntityAsModel);

            val result = unitUnderTest.getKonfigurationUnauthorized(KonfigurationKonfigKey.SPAETESTE_LOGIN_UHRZEIT);

            Assertions.assertThat(result.get()).isSameAs(mockedMappedEntityAsModel);
        }

        @Test
        void should_returnKonfigurationModel_when_willkommenstextIsGiven() {
            val mockedRepoResponse = new Konfiguration();
            val mockedMappedEntityAsModel = KonfigurationModel.builder().build();

            Mockito.when(konfigurationRepository.getWillkommenstext()).thenReturn(Optional.of(mockedRepoResponse));
            Mockito.when(konfigurationModelMapper.toModel(mockedRepoResponse)).thenReturn(mockedMappedEntityAsModel);

            val result = unitUnderTest.getKonfigurationUnauthorized(KonfigurationKonfigKey.WILLKOMMENSTEXT);

            Assertions.assertThat(result.get()).isSameAs(mockedMappedEntityAsModel);
        }

        private static Stream<Arguments> getNonSupportedKeys() {
            return Arrays.stream(KonfigurationKonfigKey.values()).filter(konfigKey -> !SUPPORTED_KEYS.contains(konfigKey)).map(Arguments::of);
        }
    }
}
