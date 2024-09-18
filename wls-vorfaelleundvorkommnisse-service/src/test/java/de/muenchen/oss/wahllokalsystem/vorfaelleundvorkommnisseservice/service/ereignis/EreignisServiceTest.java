package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.EreignisRepository;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignisart;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisModelMapper;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisService;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisValidator;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.TestdataFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
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

            val mockedEreignis = TestdataFactory.CreateEreignisEntity.withData(wahlbezirkID);
            val mockedEreignisModel = TestdataFactory.CreateEreignisModel.fromEntity(mockedEreignis);
            val mockedEeignisModelList = List.of(TestdataFactory.CreateEreignisModel.withEreignisart(Ereignisart.VORFALL));
            val expectedEreignisseModel = TestdataFactory.CreateEreignisseModel.withData(wahlbezirkID, keineVorfaelle, keineVorkommnisse,
                    mockedEeignisModelList);

            Mockito.when(ereignisRepository.findByWahlbezirkID(wahlbezirkID)).thenReturn(List.of(mockedEreignis));
            Mockito.when(ereignisModelMapper.toModel(mockedEreignis)).thenReturn(mockedEreignisModel);
            Mockito.when(ereignisModelMapper.toEreignisseModel(wahlbezirkID, keineVorfaelle, keineVorkommnisse, mockedEeignisModelList))
                    .thenReturn(expectedEreignisseModel);

            val result = unitUnderTest.getEreignis(wahlbezirkID);
            Assertions.assertThat(result).isEqualTo(Optional.of(expectedEreignisseModel));

            Mockito.verify(ereignisValidator).validWahlbezirkIDOrThrow(wahlbezirkID);
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
        void should_not_throw_exception_when_new_data_is_saved() {
            val mockedEreignisModelList = List.of(TestdataFactory.CreateEreignisModel.withData());
            val mockedEreignisseWriteModel = TestdataFactory.CreateEreignisseWriteModel.withData("wahlbezirkID", mockedEreignisModelList);
            val mockedEreignisList = TestdataFactory.CreateEreignisEntity.listFromModel(mockedEreignisseWriteModel);

            Mockito.when(ereignisModelMapper.toEntity(mockedEreignisseWriteModel)).thenReturn(mockedEreignisList);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postEreignis(mockedEreignisseWriteModel));

            Mockito.verify(ereignisValidator).validEreignisAndWahlbezirkIDOrThrow(mockedEreignisseWriteModel);
            Mockito.verify(ereignisRepository).deleteByWahlbezirkID(mockedEreignisseWriteModel.wahlbezirkID());
            Mockito.verify(ereignisRepository).saveAll(mockedEreignisList);
        }

        @Test
        void should_throw_WlsException_when_saving_failed() {
            val mockedEreignisModelList = List.of(TestdataFactory.CreateEreignisModel.withData());
            val mockedEreignisseWriteModel = TestdataFactory.CreateEreignisseWriteModel.withData("wahlbezirkID", mockedEreignisModelList);
            val mockedEreignisList = TestdataFactory.CreateEreignisEntity.listFromModel(mockedEreignisseWriteModel);
            val mockedRepoSaveException = new RuntimeException("saving failed");
            val mockedWlsException = TechnischeWlsException.withCode("").buildWithMessage("");

            Mockito.when(ereignisModelMapper.toEntity(mockedEreignisseWriteModel)).thenReturn(mockedEreignisList);
            Mockito.doThrow(mockedRepoSaveException).when(ereignisRepository).saveAll(mockedEreignisList);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.SAVEEREIGNIS_UNSAVABLE)).thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.postEreignis(mockedEreignisseWriteModel)).isSameAs(mockedWlsException);
        }
    }
}
