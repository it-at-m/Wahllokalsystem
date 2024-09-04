package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlenService;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlenWriteModel;
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

@ExtendWith(MockitoExtension.class)
class WahlenControllerTest {

    @Mock
    WahlenService wahlenService;

    @Mock
    WahlDTOMapper wahlDTOMapper;

    @InjectMocks
    WahlenController unitUnderTest;

    @Nested
    class GetWahlen {

        @Test
        void serviceIsCalledWithoutExceptions() {
            val wahltagID = "wahltagID";

            val mockedServiceResponse = List.of(Mockito.mock(WahlModel.class), Mockito.mock(WahlModel.class));
            val mockedMappedServiceResponse = List.of(Mockito.mock(WahlDTO.class), Mockito.mock(WahlDTO.class));

            Mockito.when(wahlenService.getWahlen(wahltagID)).thenReturn(mockedServiceResponse);
            Mockito.when(wahlDTOMapper.fromListOfWahlModelToListOfWahlDTO(mockedServiceResponse)).thenReturn(mockedMappedServiceResponse);

            val result = unitUnderTest.getWahlen(wahltagID);

            Assertions.assertThat(result).isSameAs(mockedMappedServiceResponse);
        }
    }

    @Nested
    class PostWahlen {

        @Test
        void serviceIsCalledWithoutExceptions() {
            val wahltagID = "wahltagID";
            val requestBody = List.of(Mockito.mock(WahlDTO.class));

            val mockedMappedRequest = List.of(Mockito.mock(WahlModel.class));

            Mockito.when(wahlDTOMapper.fromListOfWahlDTOtoListOfWahlModel(requestBody)).thenReturn(mockedMappedRequest);

            unitUnderTest.postWahlen(wahltagID, requestBody);

            Mockito.verify(wahlenService).postWahlen(new WahlenWriteModel(wahltagID, mockedMappedRequest));
        }
    }

    @Nested
    class ResetWahlen {

        @Test
        void serviceIsCalledWithoutExceptions() {
            unitUnderTest.resetWahlen();

            Mockito.verify(wahlenService).resetWahlen();
        }
    }
}
