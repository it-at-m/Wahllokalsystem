package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlvorschlag;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.Kandidat;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.Referendumoption;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.Referendumvorlage;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.Referendumvorlagen;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.Wahlvorschlaege;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.WahlvorschlaegeListe;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.Wahlvorschlag;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto.KandidatDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto.ReferendumoptionDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto.ReferendumvorlageDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto.ReferendumvorlagenDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto.WahlvorschlaegeDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto.WahlvorschlaegeListeDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto.WahlvorschlagDTO;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WahlvorschlagMapperTest {

    private final WahlvorschlagMapper unitUnderTest = Mappers.getMapper(WahlvorschlagMapper.class);

    @Nested
    class WahlvorschlagMapping {

        @Test
        void wahlvorschlagToDTO() {
            val kandidat1 = new Kandidat("name1", 1, false, 1, false);
            kandidat1.setId(UUID.randomUUID());
            val kandidat2 = new Kandidat("name2", 2, true, 2, true);
            kandidat2.setId(UUID.randomUUID());
            val kandidaten = Set.of(kandidat1, kandidat2);
            val entityToMap = new Wahlvorschlag(1, "wahlvorschlag1", true, kandidaten);
            entityToMap.setId(UUID.randomUUID());

            val result = unitUnderTest.toDTO(entityToMap);

            val expectedKandidaten = Set.of(
                    new KandidatDTO(kandidat1.getId().toString(), kandidat1.getName(), kandidat1.getListenposition(), kandidat1.isDirektkandidat(),
                            kandidat1.getTabellenSpalteInNiederschrift(), kandidat1.isEinzelbewerber()),
                    new KandidatDTO(kandidat2.getId().toString(), kandidat2.getName(), kandidat2.getListenposition(), kandidat2.isDirektkandidat(),
                            kandidat2.getTabellenSpalteInNiederschrift(), kandidat2.isEinzelbewerber()));
            val expectedResult = new WahlvorschlagDTO(entityToMap.getId().toString(), entityToMap.getOrdnungszahl(), entityToMap.getKurzname(),
                    entityToMap.isErhaeltStimmen(), expectedKandidaten);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void wahlvorschlaegeToDTO() {
            val kandidat1 = new Kandidat("name1", 1, false, 1, false);
            kandidat1.setId(UUID.randomUUID());
            val kandidat2 = new Kandidat("name2", 2, true, 2, true);
            kandidat2.setId(UUID.randomUUID());
            val kandidaten1 = Set.of(kandidat1, kandidat2);
            val wahlvorschlag1 = new Wahlvorschlag(1, "wahlvorschlag1", true, kandidaten1);
            wahlvorschlag1.setId(UUID.randomUUID());

            val kandidat3 = new Kandidat("name3", 1, false, 1, false);
            kandidat3.setId(UUID.randomUUID());
            val kandidat4 = new Kandidat("name4", 2, true, 2, true);
            kandidat4.setId(UUID.randomUUID());
            val kandidaten2 = Set.of(kandidat3, kandidat4);
            val wahlvorschlag2 = new Wahlvorschlag(1, "wahlvorschlag2", true, kandidaten2);
            wahlvorschlag2.setId(UUID.randomUUID());

            val wahlvorschlaege = new Wahlvorschlaege("wahlbezirkID", "wahlID", "stimmzettelgebietID", Set.of(wahlvorschlag1, wahlvorschlag2));
            wahlvorschlaege.setId(UUID.randomUUID());
            val result = unitUnderTest.toDTO(wahlvorschlaege);

            val expectedKandidaten1 = Set.of(
                    new KandidatDTO(kandidat1.getId().toString(), kandidat1.getName(), kandidat1.getListenposition(), kandidat1.isDirektkandidat(),
                            kandidat1.getTabellenSpalteInNiederschrift(), kandidat1.isEinzelbewerber()),
                    new KandidatDTO(kandidat2.getId().toString(), kandidat2.getName(), kandidat2.getListenposition(), kandidat2.isDirektkandidat(),
                            kandidat2.getTabellenSpalteInNiederschrift(), kandidat2.isEinzelbewerber()));
            val expectedWahlvorschlag1 = new WahlvorschlagDTO(wahlvorschlag1.getId().toString(), wahlvorschlag1.getOrdnungszahl(), wahlvorschlag1.getKurzname(),
                    wahlvorschlag1.isErhaeltStimmen(), expectedKandidaten1);
            val expectedKandidaten2 = Set.of(
                    new KandidatDTO(kandidat3.getId().toString(), kandidat3.getName(), kandidat3.getListenposition(), kandidat3.isDirektkandidat(),
                            kandidat3.getTabellenSpalteInNiederschrift(), kandidat3.isEinzelbewerber()),
                    new KandidatDTO(kandidat4.getId().toString(), kandidat4.getName(), kandidat4.getListenposition(), kandidat4.isDirektkandidat(),
                            kandidat4.getTabellenSpalteInNiederschrift(), kandidat4.isEinzelbewerber()));
            val expectedWahlvorschlag2 = new WahlvorschlagDTO(wahlvorschlag2.getId().toString(), wahlvorschlag2.getOrdnungszahl(), wahlvorschlag2.getKurzname(),
                    wahlvorschlag2.isErhaeltStimmen(), expectedKandidaten2);

            val expectedResult = new WahlvorschlaegeDTO(wahlvorschlaege.getWahlbezirkID(), wahlvorschlaege.getWahlID(),
                    wahlvorschlaege.getStimmzettelgebietID(),
                    Set.of(expectedWahlvorschlag1, expectedWahlvorschlag2));
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        void wahlvorschlaegeListeToDTO() {
            val kandidat1 = new Kandidat("name1", 1, false, 1, false);
            kandidat1.setId(UUID.randomUUID());
            val kandidat2 = new Kandidat("name2", 2, true, 2, true);
            kandidat2.setId(UUID.randomUUID());
            val kandidaten1 = Set.of(kandidat1, kandidat2);
            val wahlvorschlag1 = new Wahlvorschlag(1, "wahlvorschlag1", true, kandidaten1);
            wahlvorschlag1.setId(UUID.randomUUID());

            val kandidat3 = new Kandidat("name3", 1, false, 1, false);
            kandidat3.setId(UUID.randomUUID());
            val kandidat4 = new Kandidat("name4", 2, true, 2, true);
            kandidat4.setId(UUID.randomUUID());
            val kandidaten2 = Set.of(kandidat3, kandidat4);
            val wahlvorschlag2 = new Wahlvorschlag(1, "wahlvorschlag2", true, kandidaten2);
            wahlvorschlag2.setId(UUID.randomUUID());

            val wahlvorschlaege = new Wahlvorschlaege("wahlbezirkID", "wahlID", "stimmzettelgebietID", Set.of(wahlvorschlag1, wahlvorschlag2));
            wahlvorschlaege.setId(UUID.randomUUID());

            val wahlvorschlaegeListe = new WahlvorschlaegeListe(LocalDate.of(2024, 10, 10), "wahlID", Set.of(wahlvorschlaege));
            val result = unitUnderTest.toDTO(wahlvorschlaegeListe);

            val expectedKandidaten1 = Set.of(
                    new KandidatDTO(kandidat1.getId().toString(), kandidat1.getName(), kandidat1.getListenposition(), kandidat1.isDirektkandidat(),
                            kandidat1.getTabellenSpalteInNiederschrift(), kandidat1.isEinzelbewerber()),
                    new KandidatDTO(kandidat2.getId().toString(), kandidat2.getName(), kandidat2.getListenposition(), kandidat2.isDirektkandidat(),
                            kandidat2.getTabellenSpalteInNiederschrift(), kandidat2.isEinzelbewerber()));
            val expectedWahlvorschlag1 = new WahlvorschlagDTO(wahlvorschlag1.getId().toString(), wahlvorschlag1.getOrdnungszahl(), wahlvorschlag1.getKurzname(),
                    wahlvorschlag1.isErhaeltStimmen(), expectedKandidaten1);
            val expectedKandidaten2 = Set.of(
                    new KandidatDTO(kandidat3.getId().toString(), kandidat3.getName(), kandidat3.getListenposition(), kandidat3.isDirektkandidat(),
                            kandidat3.getTabellenSpalteInNiederschrift(), kandidat3.isEinzelbewerber()),
                    new KandidatDTO(kandidat4.getId().toString(), kandidat4.getName(), kandidat4.getListenposition(), kandidat4.isDirektkandidat(),
                            kandidat4.getTabellenSpalteInNiederschrift(), kandidat4.isEinzelbewerber()));
            val expectedWahlvorschlag2 = new WahlvorschlagDTO(wahlvorschlag2.getId().toString(), wahlvorschlag2.getOrdnungszahl(), wahlvorschlag2.getKurzname(),
                    wahlvorschlag2.isErhaeltStimmen(), expectedKandidaten2);
            val expectedWahlvorschlaege = new WahlvorschlaegeDTO(wahlvorschlaege.getWahlbezirkID(), wahlvorschlaege.getWahlID(),
                    wahlvorschlaege.getStimmzettelgebietID(),
                    Set.of(expectedWahlvorschlag1, expectedWahlvorschlag2));

            val expectedResult = new WahlvorschlaegeListeDTO(wahlvorschlaegeListe.getWahlID(),
                    Set.of(expectedWahlvorschlaege));

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class ReferendumMapping {
        @Test
        void referendumoptionToDTO() {
            val entityToMap = new Referendumoption("Optionsname1", Long.valueOf(1));
            entityToMap.setId(UUID.randomUUID());

            val result = unitUnderTest.toDTO(entityToMap);

            val expectedResult = new ReferendumoptionDTO(entityToMap.getId().toString(), entityToMap.getName(), entityToMap.getPosition());

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void referendumvorlageToDTO() {
            val referendumoption1 = new Referendumoption("Optionsname1", Long.valueOf(1));
            referendumoption1.setId(UUID.randomUUID());
            val referendumoption2 = new Referendumoption("Optionsname2", Long.valueOf(2));
            referendumoption2.setId(UUID.randomUUID());
            val referendumoptionen = Set.of(referendumoption1, referendumoption2);
            val entityToMap = new Referendumvorlage("wahlvorschlagID", 1, "referendum1", "Warum ist die Banane krumm?", referendumoptionen);
            entityToMap.setId(UUID.randomUUID());

            val result = unitUnderTest.toDTO(entityToMap);

            val expectedReferendumoptionen = Set.of(
                    new ReferendumoptionDTO(referendumoption1.getId().toString(), referendumoption1.getName(), referendumoption1.getPosition()),
                    new ReferendumoptionDTO(referendumoption2.getId().toString(), referendumoption2.getName(), referendumoption2.getPosition()));
            val expectedResult = new ReferendumvorlageDTO(entityToMap.getWahlvorschlagID(), entityToMap.getOrdnungszahl(), entityToMap.getKurzname(),
                    entityToMap.getFrage(), expectedReferendumoptionen);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void referendumvorlagenToDTO() {
            val referendumoption1 = new Referendumoption("Optionsname1", Long.valueOf(1));
            referendumoption1.setId(UUID.randomUUID());
            val referendumoption2 = new Referendumoption("Optionsname2", Long.valueOf(2));
            referendumoption2.setId(UUID.randomUUID());
            val referendumoptionen1 = Set.of(referendumoption1, referendumoption2);
            val referendumvorlage1 = new Referendumvorlage("wahlvorschlagID1", 1, "referendum1", "Warum ist die Banane krumm?", referendumoptionen1);
            referendumvorlage1.setId(UUID.randomUUID());

            val referendumoption3 = new Referendumoption("Optionsname3", Long.valueOf(3));
            referendumoption3.setId(UUID.randomUUID());
            val referendumoption4 = new Referendumoption("Optionsname4", Long.valueOf(4));
            referendumoption4.setId(UUID.randomUUID());
            val referendumoptionen2 = Set.of(referendumoption3, referendumoption4);
            val referendumvorlage2 = new Referendumvorlage("wahlvorschlagID2", 1, "referendum1", "Ist die Erde eine Scheibe?", referendumoptionen2);
            referendumvorlage2.setId(UUID.randomUUID());

            val entityToMap = new Referendumvorlagen("wahlbezirkID", "wahlID", "stimmzettelgebietID", Set.of(referendumvorlage1, referendumvorlage2));
            entityToMap.setId(UUID.randomUUID());
            val result = unitUnderTest.toDTO(entityToMap);

            val expectedReferendumoptionen1 = Set.of(
                    new ReferendumoptionDTO(referendumoption1.getId().toString(), referendumoption1.getName(), referendumoption1.getPosition()),
                    new ReferendumoptionDTO(referendumoption2.getId().toString(), referendumoption2.getName(), referendumoption2.getPosition()));
            val expectedReferendumoptionen2 = Set.of(
                    new ReferendumoptionDTO(referendumoption3.getId().toString(), referendumoption3.getName(), referendumoption3.getPosition()),
                    new ReferendumoptionDTO(referendumoption4.getId().toString(), referendumoption4.getName(), referendumoption4.getPosition()));
            val expectedReferendumvorlage1 = new ReferendumvorlageDTO(referendumvorlage1.getWahlvorschlagID(), referendumvorlage1.getOrdnungszahl(),
                    referendumvorlage1.getKurzname(),
                    referendumvorlage1.getFrage(), expectedReferendumoptionen1);
            val expectedReferendumvorlage2 = new ReferendumvorlageDTO(referendumvorlage2.getWahlvorschlagID(), referendumvorlage2.getOrdnungszahl(),
                    referendumvorlage2.getKurzname(),
                    referendumvorlage2.getFrage(), expectedReferendumoptionen2);

            val expectedResult = new ReferendumvorlagenDTO(entityToMap.getStimmzettelgebietID(),
                    Set.of(expectedReferendumvorlage1, expectedReferendumvorlage2));

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

}
