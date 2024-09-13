package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignis;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.EreignisRepository;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignisart;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisModel;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisModelMapper;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisService;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisValidator;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.TestdataFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        void should_return_EreignisseModel_when_given_valid_wahlbezirkid() {
            val wahlbezirkID = "wahlbezirkID";
            val keineVorfaelle = false;
            val keineVorkommnisse = true;
            val ereignis = TestdataFactory.createEreignisEntityWithData(wahlbezirkID, "beschreibung", LocalDateTime.now().withNano(0), Ereignisart.VORFALL);
            val ereignisModel = TestdataFactory.createEreignisModelFromEntity(ereignis);
            List<EreignisModel> ereignisModelList = new ArrayList<>();
            ereignisModelList.add(TestdataFactory.createEreignisModelWithData("beschreibung", LocalDateTime.now().withNano(0), Ereignisart.VORFALL));
            val ereignisseModel = TestdataFactory.createEreignisseModelWithData(wahlbezirkID, keineVorfaelle, keineVorkommnisse, ereignisModelList);

            Mockito.doNothing().when(ereignisValidator).validWahlbezirkIDOrThrow(wahlbezirkID);
            Mockito.when(ereignisRepository.findByWahlbezirkID(wahlbezirkID)).thenReturn(List.of(ereignis));
            Mockito.when(ereignisModelMapper.toModel(ereignis)).thenReturn(ereignisModel);
            Mockito.when(ereignisModelMapper.toEreignisseModel(wahlbezirkID, keineVorfaelle, keineVorkommnisse, ereignisModelList)).thenReturn(ereignisseModel);

            val result = unitUnderTest.getEreignis(wahlbezirkID);
            Assertions.assertThat(result).isEqualTo(Optional.of(ereignisseModel));
        }

        @Test
        void should_return_empty_response_when_no_data_found() {
            val wahlbezirkID = "wahlbezirkID";

            Mockito.when(ereignisRepository.findByWahlbezirkID(wahlbezirkID)).thenReturn(Collections.emptyList());

            val result = unitUnderTest.getEreignis(wahlbezirkID);
            Assertions.assertThat(result).isEmpty();
        }
    }

    @Nested
    class SetEreignisse {

        @Test
        void should_not_throw_Exception_when_new_data_is_saved() {
            val wahlbezirkID = "wahlbezirkID";
            List<EreignisModel> ereignisModelList = new ArrayList<>();
            ereignisModelList.add(TestdataFactory.createEreignisModelWithData("beschreibung", LocalDateTime.now().withNano(0), Ereignisart.VORFALL));
            val ereignisseWriteModel = TestdataFactory.createEreignisseWriteModelWithData(wahlbezirkID, ereignisModelList);
            List<Ereignis> ereignisList = TestdataFactory.createEreignisEntityListFromModel(ereignisseWriteModel);

            Mockito.when(ereignisModelMapper.toEntity(ereignisseWriteModel)).thenReturn(ereignisList);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postEreignis(ereignisseWriteModel));

            Mockito.verify(ereignisValidator).validEreignisAndWahlbezirkIDOrThrow(ereignisseWriteModel);
            Mockito.verify(ereignisRepository).deleteByWahlbezirkID(ereignisseWriteModel.wahlbezirkID());
            Mockito.verify(ereignisRepository).saveAll(ereignisList);
        }

        @Test
        void should_throw_WlsException_when_saving_failed() {
            val wahlbezirkID = "wahlbezirkID";
            List<EreignisModel> ereignisModelList = new ArrayList<>();
            ereignisModelList.add(TestdataFactory.createEreignisModelWithData("beschreibung", LocalDateTime.now().withNano(0), Ereignisart.VORFALL));
            val ereignisseWriteModel = TestdataFactory.createEreignisseWriteModelWithData(wahlbezirkID, ereignisModelList);
            List<Ereignis> ereignisList = TestdataFactory.createEreignisEntityListFromModel(ereignisseWriteModel);

            val mockedRepoSaveException = new RuntimeException("saving failed");
            val mockedWlsException = TechnischeWlsException.withCode("").buildWithMessage("");

            Mockito.when(ereignisModelMapper.toEntity(ereignisseWriteModel)).thenReturn(ereignisList);
            Mockito.doThrow(mockedRepoSaveException).when(ereignisRepository).saveAll(ereignisList);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.SAVEEREIGNIS_UNSAVABLE)).thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.postEreignis(ereignisseWriteModel)).isSameAs(mockedWlsException);
        }
    }
}
