package de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.konfiguration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.konfiguration.dto.KennbuchstabenListenDTO;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.konfiguration.dto.KonfigurationDTO;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.konfiguration.dto.KonfigurationSetDTO;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.KonfigurationService;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KennbuchstabenListenModel;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KonfigurationKonfigKey;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KonfigurationModel;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KonfigurationSetModel;
import java.util.Collections;
import java.util.List;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class KonfigurationControllerTest {

    @Mock
    KonfigurationService konfigurationService;

    @Mock
    KonfigurationDTOMapper konfigurationDTOMapper;

    @InjectMocks
    KonfigurationController unitUnderTest;

    @Nested
    class GetKonfiguration {

        @Test
        void serviceCalledWithDataFound() {
            val konfigurationKeyParameter = KonfigurationKey.KENNBUCHSTABEN;

            val mockedMapperResultToModelKey = KonfigurationKonfigKey.WILLKOMMENSTEXT;
            val mockedMapperResultToDTO = KonfigurationDTO.builder().build();
            val mockedServiceResponseModel = KonfigurationModel.builder().build();

            Mockito.when(konfigurationDTOMapper.toModelKey(konfigurationKeyParameter)).thenReturn(mockedMapperResultToModelKey);
            Mockito.when(konfigurationService.getKonfiguration(mockedMapperResultToModelKey)).thenReturn(Optional.of(mockedServiceResponseModel));
            Mockito.when(konfigurationDTOMapper.toDTO(mockedServiceResponseModel)).thenReturn(mockedMapperResultToDTO);

            val result = unitUnderTest.getKonfiguration(KonfigurationKey.KENNBUCHSTABEN);

            Assertions.assertThat(result).isEqualTo(ResponseEntity.ok(mockedMapperResultToDTO));
        }

        @Test
        void serviceCalledWithNoDataFound() {
            val konfigurationKeyParameter = KonfigurationKey.KENNBUCHSTABEN;

            val mockedMapperResultToModelKey = KonfigurationKonfigKey.WILLKOMMENSTEXT;

            Mockito.when(konfigurationDTOMapper.toModelKey(konfigurationKeyParameter)).thenReturn(mockedMapperResultToModelKey);
            Mockito.when(konfigurationService.getKonfiguration(mockedMapperResultToModelKey)).thenReturn(Optional.empty());

            val result = unitUnderTest.getKonfiguration(KonfigurationKey.KENNBUCHSTABEN);

            Assertions.assertThat(result).isEqualTo(ResponseEntity.noContent().build());
        }

    }

    @Nested
    class PostKonfiguration {

        @Test
        void serviceCalledWithMappedData() {
            val konfigKey = KonfigurationKey.KENNBUCHSTABEN;
            val requestDTO = new KonfigurationSetDTO("wert", "beschreibung", "standardwert");

            val mockedServiceSetModel = new KonfigurationSetModel(konfigKey.name(), requestDTO.wert(), requestDTO.beschreibung(), requestDTO.standardwert());

            Mockito.when(konfigurationDTOMapper.toSetModel(eq(konfigKey), eq(requestDTO))).thenReturn(mockedServiceSetModel);
            Mockito.doNothing().when(konfigurationService).setKonfiguration(mockedServiceSetModel);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postKonfiguration(konfigKey, requestDTO));
        }
    }

    @Nested
    class GetKonfigurationen {

        @Test
        void serviceCalledOkWhenDataFound() {
            val mockedServiceResponseModel = List.of(
                    KonfigurationModel.builder().build(),
                    KonfigurationModel.builder().build(),
                    KonfigurationModel.builder().build());
            val mockedMappedModelAsDTO = KonfigurationDTO.builder().build();

            Mockito.when(konfigurationService.getAllKonfigurations()).thenReturn(mockedServiceResponseModel);
            Mockito.when(konfigurationDTOMapper.toDTO(any(KonfigurationModel.class))).thenReturn(mockedMappedModelAsDTO);

            val result = unitUnderTest.getKonfigurations();

            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            Assertions.assertThat(result.getBody()).hasSize(mockedServiceResponseModel.size());
        }

        @Test
        void serviceCalledNoContentWhenNoDataFound() {
            Mockito.when(konfigurationService.getAllKonfigurations()).thenReturn(null);

            val result = unitUnderTest.getKonfigurations();

            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            Assertions.assertThat(result.getBody()).isNull();
        }
    }

    @Nested
    class GetKennbuchstabenListen {

        @Test
        void serviceCalled() {
            val mockedServiceResponse = new KennbuchstabenListenModel(Collections.emptyList());
            val mockedMappedResponseAsDTO = new KennbuchstabenListenDTO(Collections.emptyList());

            Mockito.when(konfigurationService.getKennbuchstabenListen()).thenReturn(mockedServiceResponse);
            Mockito.when(konfigurationDTOMapper.toDTO(mockedServiceResponse)).thenReturn(mockedMappedResponseAsDTO);

            val result = unitUnderTest.getKennbuchstabenListen();

            Assertions.assertThat(result).isSameAs(mockedMappedResponseAsDTO);
        }
    }

    @Nested
    class GetKonfigurationUnauthorized {

        @Test
        void okWhenDataWasFound() {
            val konfigKey = KonfigurationKey.KENNBUCHSTABEN;
            val mockedKonfigKeyAsModel = KonfigurationKonfigKey.KENNBUCHSTABEN;
            val mockedServiceResponseModel = KonfigurationModel.builder().build();
            val mockedMappedModelAsDTO = KonfigurationDTO.builder().build();
            Mockito.when(konfigurationService.getKonfigurationUnauthorized(mockedKonfigKeyAsModel)).thenReturn(Optional.of(mockedServiceResponseModel));
            Mockito.when(konfigurationDTOMapper.toModelKey(konfigKey)).thenReturn(mockedKonfigKeyAsModel);
            Mockito.when(konfigurationDTOMapper.toDTO(mockedServiceResponseModel)).thenReturn(mockedMappedModelAsDTO);

            val result = unitUnderTest.getKonfigurationUnauthorized(konfigKey);

            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            Assertions.assertThat(result.getBody()).isSameAs(mockedMappedModelAsDTO);
        }

        @Test
        void noContentWhenNoDataFound() {
            val konfigKey = KonfigurationKey.KENNBUCHSTABEN;
            val mockedKonfigKeyAsModel = KonfigurationKonfigKey.KENNBUCHSTABEN;

            Mockito.when(konfigurationService.getKonfigurationUnauthorized(mockedKonfigKeyAsModel)).thenReturn(Optional.empty());
            Mockito.when(konfigurationDTOMapper.toModelKey(konfigKey)).thenReturn(mockedKonfigKeyAsModel);

            val result = unitUnderTest.getKonfigurationUnauthorized(konfigKey);

            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            Assertions.assertThat(result.getBody()).isNull();
        }
    }

}
