package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.EreignisRepository;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisModelMapper;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisService;

import java.util.Optional;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisValidator;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.TestdataFactory;
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
public class EreignisServiceTest {

    @Mock
    EreignisRepository ereignisRepository;

    @Mock
    EreignisModelMapper ereignisModelMapper;

    @Mock
    EreignisValidator ereignisValidator;

    @InjectMocks
    EreignisService unitUnderTest;

    @Nested
    class GetEreignisse {

        @Test
        void foundData() {
            val wahlbezirkID = "wahlbezirkID";
            val mockedEntity_ereignisse = TestdataFactory.createEreignisEntityWithNoData();
            val mockedEntity_ereignisModel = TestdataFactory.createEreignisModelWithNoData();

            Mockito.doNothing().when(ereignisValidator).validWahlbezirkIDOrThrow(wahlbezirkID);
            Mockito.when(ereignisRepository.findById(wahlbezirkID)).thenReturn(Optional.of(mockedEntity_ereignisse));
            Mockito.when(ereignisModelMapper.toModel(mockedEntity_ereignisse)).thenReturn(mockedEntity_ereignisModel);

            val result = unitUnderTest.getEreignis(wahlbezirkID);

            Assertions.assertThat(result).isEqualTo(Optional.of(mockedEntity_ereignisModel));
        }

        @Test
        void foundNoData() {
            val wahlbezirkID = "wahlbezirkID";

            Mockito.when(ereignisRepository.findById(wahlbezirkID)).thenReturn(Optional.empty());

            val result = unitUnderTest.getEreignis(wahlbezirkID);

            Assertions.assertThat(result).isEmpty();
        }
    }
}
