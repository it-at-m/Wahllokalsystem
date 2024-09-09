package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.EreignisRepository;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisModelMapper;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisService;

import java.util.Optional;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisValidator;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.TestdataFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
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

    @Mock
    ExceptionFactory exceptionFactory;

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

    @Nested
    class SetEreignisse {

        @Test
        void postData() {
            val ereignisModel = TestdataFactory.createEreignisModelWithNoData();
            val ereignisEntity = TestdataFactory.createEreignisEntityWithNoData();

            Mockito.when(ereignisModelMapper.toEntity(ereignisModel)).thenReturn(ereignisEntity);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postEreignis(ereignisModel));

            Mockito.verify(ereignisValidator).validEreignisAndWahlbezirkIDOrThrow(ereignisModel);
            Mockito.verify(ereignisRepository).save(ereignisEntity);
        }

        @Test
        void wlsExceptionWhenSavingFailed() {
            val ereignisModel = TestdataFactory.createEreignisModelWithNoData();
            val ereignisEntity = TestdataFactory.createEreignisEntityWithNoData();
            val mockedRepoSaveException = new RuntimeException("saving failed");
            val mockedWlsException = TechnischeWlsException.withCode("").buildWithMessage("");

            Mockito.when(ereignisModelMapper.toEntity(ereignisModel)).thenReturn(ereignisEntity);
            Mockito.doThrow(mockedRepoSaveException).when(ereignisRepository).save(ereignisEntity);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.SAVEEREIGNIS_UNSAVABLE)).thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.postEreignis(ereignisModel)).isSameAs(mockedWlsException);
        }
    }
}
