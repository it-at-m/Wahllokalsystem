package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahldaten;

import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Stimmzettelgebiet;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.StimmzettelgebietRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahl;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.WahlRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahlbezirk;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.WahlbezirkRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahltag;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.WahltageRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.BasisdatenDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.BasisstrukturdatenDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.StimmzettelgebietDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlberechtigteDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlbezirkDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahltagDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.IDConverter;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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
class WahldatenServiceTest {

    @Mock
    WahldatenMapper wahldatenMapper;

    @Mock
    WahldatenValidator wahldatenValidator;

    @Mock
    IDConverter idConverter;

    @Mock
    WahltageRepository wahltageRepository;

    @Mock
    WahlRepository wahlRepository;

    @Mock
    WahlbezirkRepository wahlbezirkRepository;

    @Mock
    StimmzettelgebietRepository stimmzettelgebietRepository;

    @InjectMocks
    WahldatenService unitUnderTest;

    @Nested
    class GetWahltage {

        @Test
        void dataFound() {
            val dateSince = LocalDate.now();

            val mockedRepoEntity = new Wahltag();
            val mockedMappedEntity = WahltagDTO.builder().build();

            Mockito.when(wahltageRepository.findByTagAfterOrTagEquals(eq(dateSince), eq(dateSince))).thenReturn(List.of(mockedRepoEntity));
            Mockito.when(wahldatenMapper.toDTO(mockedRepoEntity)).thenReturn(mockedMappedEntity);

            val result = unitUnderTest.getWahltage(dateSince);

            Assertions.assertThat(result).isEqualTo(Set.of(mockedMappedEntity));
        }

        @Test
        void noDataFound() {
            val dateSince = LocalDate.now();

            Mockito.when(wahltageRepository.findByTagAfterOrTagEquals(eq(dateSince), eq(dateSince))).thenReturn(Collections.emptyList());

            val result = unitUnderTest.getWahltage(dateSince);

            Assertions.assertThat(result).isEmpty();
        }

        @Test
        void validationFailed() {
            val dateSince = LocalDate.now();

            val mockedValidationException = new RuntimeException("validation failed");

            Mockito.doThrow(mockedValidationException).when(wahldatenValidator).validGetWahltageParameterOrThrow(dateSince);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahltage(dateSince)).isSameAs(mockedValidationException);
        }
    }

    @Nested
    class GetWahlen {

        @Test
        void dataFound() {
            val wahltag = LocalDate.now();
            val nummer = "nummer";

            val mockedRepoEntity = new Wahl();
            val mockedMappedEntity = WahlDTO.builder().build();

            Mockito.when(wahlRepository.findByWahltagTagAndWahltagNummer(eq(wahltag), eq(nummer))).thenReturn(List.of(mockedRepoEntity));
            Mockito.when(wahldatenMapper.toDTO(mockedRepoEntity)).thenReturn(mockedMappedEntity);

            val result = unitUnderTest.getWahlen(wahltag, nummer);

            Assertions.assertThat(result).isEqualTo(Set.of(mockedMappedEntity));
        }

        @Test
        void noDataFound() {
            val wahltag = LocalDate.now();
            val nummer = "nummer";

            Mockito.when(wahlRepository.findByWahltagTagAndWahltagNummer(eq(wahltag), eq(nummer))).thenReturn(Collections.emptyList());

            val result = unitUnderTest.getWahlen(wahltag, nummer);

            Assertions.assertThat(result).isEmpty();
        }

        @Test
        void validationFailed() {
            val wahltag = LocalDate.now();
            val nummer = "nummer";

            val mockedValidationException = new RuntimeException("validation failed");

            Mockito.doThrow(mockedValidationException).when(wahldatenValidator).validGetWahlenParameterOrThrow(wahltag, nummer);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlen(wahltag, nummer)).isSameAs(mockedValidationException);
        }

    }

    @Nested
    class GetWahlbezirke {

        @Test
        void dataFound() {
            val wahltag = LocalDate.now();
            val nummer = "nummer";

            val mockedRepoEntity = new Wahlbezirk();
            val mockedMappedEntity = WahlbezirkDTO.builder().build();

            Mockito.when(wahlbezirkRepository.findWahlbezirkeWithStimmzettelgebietAndWahlAndWahltagById(eq(wahltag), eq(nummer)))
                    .thenReturn(List.of(mockedRepoEntity));
            Mockito.when(wahldatenMapper.toDTO(mockedRepoEntity)).thenReturn(mockedMappedEntity);

            val result = unitUnderTest.getWahlbezirke(wahltag, nummer);

            Assertions.assertThat(result).isEqualTo(Set.of(mockedMappedEntity));
        }

        @Test
        void noDataFound() {
            val wahltag = LocalDate.now();
            val nummer = "nummer";

            Mockito.when(wahlbezirkRepository.findWahlbezirkeWithStimmzettelgebietAndWahlAndWahltagById(eq(wahltag), eq(nummer)))
                    .thenReturn(Collections.emptyList());

            val result = unitUnderTest.getWahlbezirke(wahltag, nummer);

            Assertions.assertThat(result).isEmpty();
        }

        @Test
        void validationFailed() {
            val wahltag = LocalDate.now();
            val nummer = "nummer";

            val mockedValidationException = new RuntimeException("validation failed");

            Mockito.doThrow(mockedValidationException).when(wahldatenValidator).validGetWahlbezirkeParameterOrThrow(wahltag, nummer);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlbezirke(wahltag, nummer)).isSameAs(mockedValidationException);
        }

    }

    @Nested
    class GetWahlberechtigte {

        @Test
        void dataFound() {
            val wahlbezirkID = "wahlbezirkID";

            val mockedWahlbezirkAsUUID = UUID.randomUUID();
            val mockedRepoEntity = new Wahlbezirk();
            val mockedMappedEntityAsWahlberechtigte = WahlberechtigteDTO.builder().build();

            Mockito.when(idConverter.convertIDToUUIDOrThrow(wahlbezirkID)).thenReturn(mockedWahlbezirkAsUUID);
            Mockito.when(wahlbezirkRepository.findWahlbezirkeWithStimmzettelgebietAndWahlAndWahltagById(mockedWahlbezirkAsUUID))
                    .thenReturn(List.of(mockedRepoEntity));
            Mockito.when(wahldatenMapper.toWahlberechtigteDTO(mockedRepoEntity)).thenReturn(mockedMappedEntityAsWahlberechtigte);

            val result = unitUnderTest.getWahlberechtigte(wahlbezirkID);

            Assertions.assertThat(result).isEqualTo(List.of(mockedMappedEntityAsWahlberechtigte));
        }

        @Test
        void noDataFound() {
            val wahlbezirkID = "wahlbezirkID";

            val mockedWahlbezirkAsUUID = UUID.randomUUID();

            Mockito.when(idConverter.convertIDToUUIDOrThrow(wahlbezirkID)).thenReturn(mockedWahlbezirkAsUUID);
            Mockito.when(wahlbezirkRepository.findWahlbezirkeWithStimmzettelgebietAndWahlAndWahltagById(mockedWahlbezirkAsUUID))
                    .thenReturn(Collections.emptyList());

            val result = unitUnderTest.getWahlberechtigte(wahlbezirkID);

            Assertions.assertThat(result).isEmpty();
        }

        @Test
        void validationFailed() {
            val wahlbezirkID = "wahlbezirkID";

            val mockedValidationException = new RuntimeException("validation failed");

            Mockito.doThrow(mockedValidationException).when(wahldatenValidator).validGetWahlberechtigteParameterOrThrow(wahlbezirkID);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlberechtigte(wahlbezirkID)).isSameAs(mockedValidationException);
        }

    }

    @Nested
    class GetBasisdaten {

        @Test
        void dataFound() {
            val wahltag = LocalDate.now();
            val nummer = "nummer";

            val mockedRepoWahlbezirk = new Wahlbezirk();
            val mockedRepoWahlbezirkMappedToDTO = WahlbezirkDTO.builder().build();
            val mockedRepoWahlbezirkAsStrukturdatemDTO = BasisstrukturdatenDTO.builder().build();
            val mockedRepoWahl = new Wahl();
            val mockedRepoWahlMappedToDTO = WahlDTO.builder().build();
            val mockedRepoStimmzettelgebiet = new Stimmzettelgebiet();
            val mockedRepoStimmzettelgebietMappedToDTO = StimmzettelgebietDTO.builder().build();

            Mockito.when(wahlbezirkRepository.findWahlbezirkeWithStimmzettelgebietAndWahlAndWahltagById(eq(wahltag), eq(nummer)))
                    .thenReturn(List.of(mockedRepoWahlbezirk));
            Mockito.when(wahlRepository.findByWahltagTagAndWahltagNummer(eq(wahltag), eq(nummer))).thenReturn(List.of(mockedRepoWahl));
            Mockito.when(stimmzettelgebietRepository.findByWahlWahltagTagAndWahlWahltagNummer(eq(wahltag), eq(nummer)))
                    .thenReturn(List.of(mockedRepoStimmzettelgebiet));
            Mockito.when(wahldatenMapper.toDTO(mockedRepoWahl)).thenReturn(mockedRepoWahlMappedToDTO);
            Mockito.when(wahldatenMapper.toDTO(mockedRepoWahlbezirk)).thenReturn(mockedRepoWahlbezirkMappedToDTO);
            Mockito.when(wahldatenMapper.toDTO(mockedRepoStimmzettelgebiet)).thenReturn(mockedRepoStimmzettelgebietMappedToDTO);
            Mockito.when(wahldatenMapper.toBasisstrukturdatenDTO(mockedRepoWahlbezirk)).thenReturn(mockedRepoWahlbezirkAsStrukturdatemDTO);

            val result = unitUnderTest.getBasisdaten(wahltag, nummer);

            Assertions.assertThat(result)
                    .isEqualTo(new BasisdatenDTO(Set.of(mockedRepoWahlbezirkAsStrukturdatemDTO), Set.of(mockedRepoWahlMappedToDTO),
                            Set.of(mockedRepoWahlbezirkMappedToDTO), Set.of(mockedRepoStimmzettelgebietMappedToDTO)));
        }

        @Test
        void noDataFound() {
            val wahltag = LocalDate.now();
            val nummer = "nummer";

            Mockito.when(wahlbezirkRepository.findWahlbezirkeWithStimmzettelgebietAndWahlAndWahltagById(eq(wahltag), eq(nummer)))
                    .thenReturn(Collections.emptyList());
            Mockito.when(wahlRepository.findByWahltagTagAndWahltagNummer(eq(wahltag), eq(nummer))).thenReturn(Collections.emptyList());
            Mockito.when(stimmzettelgebietRepository.findByWahlWahltagTagAndWahlWahltagNummer(eq(wahltag), eq(nummer))).thenReturn(Collections.emptyList());

            val result = unitUnderTest.getBasisdaten(wahltag, nummer);

            Assertions.assertThat(result)
                    .isEqualTo(new BasisdatenDTO(Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet()));
        }

        @Test
        void validationFailed() {
            val wahltag = LocalDate.now();
            val nummer = "nummer";

            val mockedValidationException = new RuntimeException("validation failed");

            Mockito.doThrow(mockedValidationException).when(wahldatenValidator).validGetBasisdatenParameterOrThrow(wahltag, nummer);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getBasisdaten(wahltag, nummer)).isSameAs(mockedValidationException);
        }
    }

}
