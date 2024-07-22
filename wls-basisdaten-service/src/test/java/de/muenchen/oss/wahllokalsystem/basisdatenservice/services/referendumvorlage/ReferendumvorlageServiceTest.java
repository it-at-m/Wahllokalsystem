package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlage;

import static org.mockito.ArgumentMatchers.eq;

import ch.qos.logback.classic.Level;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.ReferendumvorlageRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Referendumvorlagen;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.ReferendumvorlagenRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.LoggerExtension;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Collections;
import java.util.Optional;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReferendumvorlageServiceTest {

    @Mock
    ReferendumvorlageValidator referendumvorlageValidator;

    @Mock
    ReferendumvorlageModelMapper referendumvorlageModelMapper;

    @Mock
    ReferendumvorlagenClient referendumvorlagenClient;

    @Mock
    ReferendumvorlageRepository referendumvorlageRepository;

    @Mock
    ReferendumvorlagenRepository referendumvorlagenRepository;

    @InjectMocks
    ReferendumvorlageService unitUnderTest;

    @RegisterExtension
    public LoggerExtension loggerExtension = new LoggerExtension();

    @Nested
    class LoadReferendumvorlagen {

        @Test
        void dataImportedAndSavedWhenNoDataExists() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val referendumvorlagenReference = new ReferendumvorlagenReferenceModel(wahlID, wahlbezirkID);

            val mockedBezirkUndWahlID = new BezirkUndWahlID();
            val mockedClientRerefendumvorlagenModel = new ReferendumvorlagenModel("szgID", Collections.emptySet());
            val mockedMappendModelAsEntity = new Referendumvorlagen(mockedBezirkUndWahlID, "szgID", Collections.emptySet());

            Mockito.when(referendumvorlageModelMapper.toBezirkUndWahlID(referendumvorlagenReference)).thenReturn(mockedBezirkUndWahlID);
            Mockito.when(referendumvorlageModelMapper.toEntity(eq(mockedClientRerefendumvorlagenModel), eq(mockedBezirkUndWahlID)))
                    .thenReturn(mockedMappendModelAsEntity);
            Mockito.when(referendumvorlagenRepository.findById(mockedBezirkUndWahlID)).thenReturn(Optional.empty());
            Mockito.when(referendumvorlagenClient.getReferendumvorlagen(referendumvorlagenReference)).thenReturn(mockedClientRerefendumvorlagenModel);

            val result = unitUnderTest.loadReferendumvorlagen(referendumvorlagenReference);

            Assertions.assertThat(result).isEqualTo(mockedClientRerefendumvorlagenModel);

            Mockito.verify(referendumvorlagenRepository).save(mockedMappendModelAsEntity);
            Mockito.verify(referendumvorlageRepository).saveAll(Collections.emptySet());
        }

        @Test
        void returnExistingDataWithoutImport() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val referendumvorlagenReference = new ReferendumvorlagenReferenceModel(wahlID, wahlbezirkID);

            val mockedBezirkUndWahlID = new BezirkUndWahlID();
            val mockedRepoEntity = new Referendumvorlagen(mockedBezirkUndWahlID, "szgID", Collections.emptySet());
            val mockedRepoEntityAsModel = new ReferendumvorlagenModel("szgID", Collections.emptySet());

            Mockito.when(referendumvorlageModelMapper.toBezirkUndWahlID(referendumvorlagenReference)).thenReturn(mockedBezirkUndWahlID);
            Mockito.when(referendumvorlageModelMapper.toModel(mockedRepoEntity)).thenReturn(mockedRepoEntityAsModel);
            Mockito.when(referendumvorlagenRepository.findById(mockedBezirkUndWahlID)).thenReturn(Optional.of(mockedRepoEntity));

            val result = unitUnderTest.loadReferendumvorlagen(referendumvorlagenReference);

            Assertions.assertThat(result).isEqualTo(mockedRepoEntityAsModel);
            Mockito.verifyNoMoreInteractions(referendumvorlageRepository, referendumvorlagenRepository, referendumvorlagenClient);
            Assertions.assertThat(loggerExtension.getLoggedEventsStream().filter(event -> event.getLevel() == Level.ERROR).count()).isEqualTo(0);
        }

        @Test
        void noExceptionWhenSaveOfImportedFailed() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val referendumvorlagenReference = new ReferendumvorlagenReferenceModel(wahlID, wahlbezirkID);

            val mockedBezirkUndWahlID = new BezirkUndWahlID();
            val mockedClientRerefendumvorlagenModel = new ReferendumvorlagenModel("szgID", Collections.emptySet());
            val mockedMappendModelAsEntity = new Referendumvorlagen(mockedBezirkUndWahlID, "szgID", Collections.emptySet());
            val mockedRepoException = new RuntimeException("saving failed");

            Mockito.when(referendumvorlageModelMapper.toBezirkUndWahlID(referendumvorlagenReference)).thenReturn(mockedBezirkUndWahlID);
            Mockito.when(referendumvorlageModelMapper.toEntity(eq(mockedClientRerefendumvorlagenModel), eq(mockedBezirkUndWahlID)))
                    .thenReturn(mockedMappendModelAsEntity);
            Mockito.when(referendumvorlagenRepository.findById(mockedBezirkUndWahlID)).thenReturn(Optional.empty());
            Mockito.when(referendumvorlagenClient.getReferendumvorlagen(referendumvorlagenReference)).thenReturn(mockedClientRerefendumvorlagenModel);
            Mockito.doThrow(mockedRepoException).when(referendumvorlagenRepository).save(mockedMappendModelAsEntity);

            val result = unitUnderTest.loadReferendumvorlagen(referendumvorlagenReference);

            Assertions.assertThat(result).isEqualTo(mockedClientRerefendumvorlagenModel);

            Mockito.verifyNoMoreInteractions(referendumvorlageRepository, referendumvorlagenRepository);
            Assertions.assertThat(loggerExtension.getLoggedEventsStream().filter(event -> event.getLevel() == Level.ERROR).count()).isEqualTo(1);
        }
    }

}
