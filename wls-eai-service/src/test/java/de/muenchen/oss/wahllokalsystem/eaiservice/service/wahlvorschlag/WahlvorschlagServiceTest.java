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

    @Mock
    WahlvorschlagValidator wahlvorschlagValidator;

    @InjectMocks
    WahlvorschlagService unitUnderTest;

    @Nested
    class GetWahlvorschlaege {

        @Test
        void should_returnWahlvorschlaegeDTO_when_givenValidWahlbezirkIDAndWahlID() {
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
        void should_throwNoSearchResultFoundException_when_noDataFound() {
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
        void should_returnWahlvorschlaegeListeDTO_when_givenValidWahlbezirkIDAndWahlID() {
            val wahltag = LocalDate.of(2024, 10, 10);
            val wahlID = "wahlID";

            val mockedEntity = new WahlvorschlaegeListe(wahltag, wahlID, Collections.emptySet());
            val mockedMappedEntity = new WahlvorschlaegeListeDTO(wahlID, Collections.emptySet());
            Mockito.when(wahlvorschlaegeListeRepository.findFirstByWahltagAndWahlID(wahltag, wahlID)).thenReturn(Optional.of(mockedEntity));
            Mockito.when(wahlvorschlagMapper.toDTO(mockedEntity)).thenReturn(mockedMappedEntity);

            val result = unitUnderTest.getWahlvorschlaegeListeForWahltagAndWahlID(wahltag, wahlID);

            Assertions.assertThat(result).isSameAs(mockedMappedEntity);
        }

        @Test
        void should_throwNoSearchResultFoundException_when_noDataFound() {
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
        void should_returnReferendumvorlagenDTO_when_givenValidWahlbezirkIDAndWahlID() {
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";

            val mockedEntity = new Referendumvorlagen("wahlbezirkID", "wahlID", "stimmzettelgebietID", Collections.emptySet());
            val mockedMappedEntity = new ReferendumvorlagenDTO("stimmzettelgebietID", Collections.emptySet());
            Mockito.when(referendumvorlagenRepository.findFirstByWahlbezirkIDAndWahlID(wahlbezirkID, wahlID)).thenReturn(Optional.of(mockedEntity));
            Mockito.when(wahlvorschlagMapper.toDTO(mockedEntity)).thenReturn(mockedMappedEntity);

            val result = unitUnderTest.getReferendumvorlagenForWahlAndWahlbezirk(wahlID, wahlbezirkID);

            Assertions.assertThat(result).isSameAs(mockedMappedEntity);
        }

        @Test
        void should_throwNoSearchResultFoundException_when_noDataFound() {
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";

            Mockito.when(referendumvorlagenRepository.findFirstByWahlbezirkIDAndWahlID(wahlbezirkID, wahlID)).thenReturn(Optional.empty());

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getReferendumvorlagenForWahlAndWahlbezirk(wahlID, wahlbezirkID))
                    .usingRecursiveComparison().isEqualTo(new NoSearchResultFoundException(Referendumvorlagen.class, wahlbezirkID, wahlID));
        }
    }
}
