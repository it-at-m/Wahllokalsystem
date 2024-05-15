package de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.wahltag;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.wahltag.KonfigurierterWahltagModel;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.wahltag.KonfigurierterWahltagService;
import java.time.LocalDate;
import java.util.List;
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
class KonfigurierterWahltagControllerTest {

    @InjectMocks
    KonfigurierterWahltagController unitUnderTest;

    @Mock
    KonfigurierterWahltagService konfigurierterWahltagService;

    @Mock
    KonfigurierterWahltagDTOMapper konfigurierterWahltagDTOMapper;

    @Nested
    class GetKonfigurierterWahltag {

        @Test
        void serviceCalledWithDataFound() {
            val mockDTO = KonfigurierterWahltagDTO.builder().wahltag(LocalDate.now()).wahltagStatus(WahltagStatus.INAKTIV).wahltagID("1-2-3").nummer("4711")
                .build();
            val mockModel = KonfigurierterWahltagModel.builder().build();

            Mockito.when(konfigurierterWahltagDTOMapper.toDTO(mockModel)).thenReturn(mockDTO);
            Mockito.when(konfigurierterWahltagService.getKonfigurierterWahltag()).thenReturn(mockModel);

            val result = unitUnderTest.getKonfigurierterWahltag();

            Assertions.assertThat(result).isEqualTo(ResponseEntity.ok(mockDTO));
        }

        @Test
        void serviceCalledWithNoDataFound() {
            KonfigurierterWahltagDTO mockDTO = null;
            KonfigurierterWahltagModel mockModel = null;

            Mockito.when(konfigurierterWahltagDTOMapper.toDTO(mockModel)).thenReturn(mockDTO);
            Mockito.when(konfigurierterWahltagService.getKonfigurierterWahltag()).thenReturn(mockModel);

            val result = unitUnderTest.getKonfigurierterWahltag();

            Assertions.assertThat(result).isEqualTo(ResponseEntity.noContent().build());
        }
    }

    @Nested
    class SetKonfigurierterWahltag {

        @Test
        void serviceCalledWithPostDataOK() {

            val wahltag = LocalDate.now();
            val wahltagID = "1-2-3";
            val wahltagStatus = WahltagStatus.INAKTIV;
            val nummer = "4711";

            val postMockDTO = new KonfigurierterWahltagDTO(wahltag, wahltagID, wahltagStatus, nummer);
            val mockModel = new KonfigurierterWahltagModel(wahltag, wahltagID, wahltagStatus, nummer);

            Mockito.when(konfigurierterWahltagDTOMapper.toModel(postMockDTO)).thenReturn(mockModel);
            Mockito.doNothing().when(konfigurierterWahltagService).setKonfigurierterWahltag(mockModel);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.setKonfigurierterWahltag(postMockDTO));
        }
    }

    @Nested
    class DeleteKonfigurierterWahltag {

        @Test
        void serviceCalledWithDeleteDataOK() {

            val wahltagID = "1-2-3";
            val mockModel = new KonfigurierterWahltagModel(null, wahltagID, null, null);

            Mockito.doNothing().when(konfigurierterWahltagService).deleteKonfigurierterWahltag(mockModel);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.deleteKonfigurierterWahltag(wahltagID));
        }
    }

    @Nested
    class GetKonfigurierteWahltage {

        @Test
        void serviceCalledWithDataFound() {

            val mockDTOList = List.of(
                KonfigurierterWahltagDTO.builder().build(),
                KonfigurierterWahltagDTO.builder().build(),
                KonfigurierterWahltagDTO.builder().build());

            val mockModelList = List.of(
                KonfigurierterWahltagModel.builder().build(),
                KonfigurierterWahltagModel.builder().build(),
                KonfigurierterWahltagModel.builder().build());

            Mockito.when(konfigurierterWahltagDTOMapper.toDTOList(mockModelList)).thenReturn(mockDTOList);
            Mockito.when(konfigurierterWahltagService.getKonfigurierteWahltage()).thenReturn(mockModelList);

            val result = unitUnderTest.getKonfigurierteWahltage();

            Assertions.assertThat(result).isEqualTo(ResponseEntity.ok(mockDTOList));

        }

        @Test
        void serviceCalledWithNoDataFound() {

            Mockito.when(konfigurierterWahltagDTOMapper.toDTOList(null)).thenReturn(null);
            Mockito.when(konfigurierterWahltagService.getKonfigurierteWahltage()).thenReturn(null);

            val result = unitUnderTest.getKonfigurierteWahltage();

            Assertions.assertThat(result).isEqualTo(ResponseEntity.noContent().build());
        }
    }

    @Nested
    class IsWahltagActive {

        @Test
        void serviceCalledWithActiveWahltagId() {

            val wahltagID = "1-2-3";
            val mockModel = new KonfigurierterWahltagModel(null, wahltagID, null, null);

            Mockito.when(konfigurierterWahltagService.isWahltagActive(mockModel)).thenReturn(true);

            val result = unitUnderTest.isWahltagActive(wahltagID);

            Assertions.assertThat(result).isEqualTo(ResponseEntity.ok(true));
        }

        @Test
        void serviceCalledWithInactiveWahltagId() {

            val wahltagID = "4-5-6";
            val mockModel = new KonfigurierterWahltagModel(null, wahltagID, null, null);

            Mockito.when(konfigurierterWahltagService.isWahltagActive(mockModel)).thenReturn(false);

            val result = unitUnderTest.isWahltagActive(wahltagID);

            Assertions.assertThat(result).isEqualTo(ResponseEntity.ok(false));
        }
    }
}

