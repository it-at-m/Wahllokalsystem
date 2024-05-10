package de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.konfiguration;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.KonfigurationKonfigKey;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.KonfigurationModel;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.KonfigurationService;
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

}