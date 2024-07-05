package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlvorschlag;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.Referendumvorlagen;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.ReferendumvorlagenRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.Wahlvorschlaege;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.WahlvorschlaegeListe;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.WahlvorschlaegeListeRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.WahlvorschlagRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.exception.NoSearchResultFoundException;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto.ReferendumvorlagenDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto.WahlvorschlaegeDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto.WahlvorschlaegeListeDTO;
import java.time.LocalDate;
import java.util.Collections;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
class WahlvorschlagServiceTest {

    @Mock
    WahlvorschlagRepository wahlvorschlagRepository;

    @Mock
    ReferendumvorlagenRepository referendumvorlagenRepository;

    @Mock
    WahlvorschlaegeListeRepository wahlvorschlaegeListeRepository;

    @Mock
    WahlvorschlagMapper wahlvorschlagMapper;

    @InjectMocks
    WahlvorschlagService unitUnderTest;

    @Nested
    class GetWahlvorschlaege {

        @Test
        void foundData() {
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";

            val mockedEntity = new Wahlvorschlaege("wahlbezirkID", "wahlID", "stimmzettelgebietID", Collections.emptySet());
            val mockedMappedEntity = new WahlvorschlaegeDTO(wahlbezirkID, wahlID, "stimmzettelgebietID", Collections.emptySet());
            Mockito.when(wahlvorschlagRepository.findFirstByWahlbezirkIDAndWahlID(wahlbezirkID, wahlID)).thenReturn(Optional.of(mockedEntity));
            Mockito.when(wahlvorschlagMapper.toDTO(mockedEntity)).thenReturn(mockedMappedEntity);

            val result = unitUnderTest.getWahlvorschlaegeForWahlAndWahlbezirk(wahlID, wahlbezirkID);

            Assertions.assertThat(result).isSameAs(mockedMappedEntity);
        }

        @Test
        void foundNoData() {
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";

            Mockito.when(wahlvorschlagRepository.findFirstByWahlbezirkIDAndWahlID(wahlbezirkID, wahlID)).thenReturn(Optional.empty());

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlvorschlaegeForWahlAndWahlbezirk(wahlID, wahlbezirkID))
                    .usingRecursiveComparison().isEqualTo(new NoSearchResultFoundException(Wahlvorschlaege.class, wahlbezirkID, wahlID));
        }

    }

    @Nested
    class GetWahlvorschlaegeListe {

        @Test
        void foundData() {
            val wahltag = LocalDate.of(2024, 10, 10);
            val wahlID = "wahlID";

            val mockedEntity = new WahlvorschlaegeListe(wahltag, wahlID, Collections.emptySet());
            val mockedMappedEntity = new WahlvorschlaegeListeDTO(wahltag.toString(), wahlID, Collections.emptySet());
            Mockito.when(wahlvorschlaegeListeRepository.findFirstByWahltagAndWahlID(wahltag, wahlID)).thenReturn(Optional.of(mockedEntity));
            Mockito.when(wahlvorschlagMapper.toDTO(mockedEntity)).thenReturn(mockedMappedEntity);

            val result = unitUnderTest.getWahlvorschlaegeListeForWahltagAndWahlID(wahltag, wahlID);

            Assertions.assertThat(result).isSameAs(mockedMappedEntity);
        }

        @Test
        void foundNoData() {
            val wahltag = LocalDate.of(2024, 10, 10);
            val wahlID = "wahlID";

            Mockito.when(wahlvorschlaegeListeRepository.findFirstByWahltagAndWahlID(wahltag, wahlID)).thenReturn(Optional.empty());

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlvorschlaegeListeForWahltagAndWahlID(wahltag, wahlID))
                    .usingRecursiveComparison().isEqualTo(new NoSearchResultFoundException(WahlvorschlaegeListe.class, wahltag, wahlID));
        }

    }

    @Nested
    class GetReferendumvorlagen {

        @Test
        void foundData() {
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";

            val mockedEntity = new Referendumvorlagen("wahlbezirkID", "wahlID", "stimmzettelgebietID", Collections.emptySet());
            val mockedMappedEntity = new ReferendumvorlagenDTO(wahlbezirkID, wahlID, "stimmzettelgebietID", Collections.emptySet());
            Mockito.when(referendumvorlagenRepository.findFirstByWahlbezirkIDAndWahlID(wahlbezirkID, wahlID)).thenReturn(Optional.of(mockedEntity));
            Mockito.when(wahlvorschlagMapper.toDTO(mockedEntity)).thenReturn(mockedMappedEntity);

            val result = unitUnderTest.getReferendumvorlagenForWahlAndWahlbezirk(wahlID, wahlbezirkID);

            Assertions.assertThat(result).isSameAs(mockedMappedEntity);
        }

        @Test
        void foundNoData() {
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";

            Mockito.when(referendumvorlagenRepository.findFirstByWahlbezirkIDAndWahlID(wahlbezirkID, wahlID)).thenReturn(Optional.empty());

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getReferendumvorlagenForWahlAndWahlbezirk(wahlID, wahlbezirkID))
                    .usingRecursiveComparison().isEqualTo(new NoSearchResultFoundException(Referendumvorlagen.class, wahlbezirkID, wahlID));
        }

    }

}
