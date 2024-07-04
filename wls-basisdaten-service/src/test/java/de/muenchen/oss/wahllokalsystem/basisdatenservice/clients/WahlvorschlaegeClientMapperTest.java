package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.KandidatDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlvorschlaegeDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlvorschlagDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlag.KandidatModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlag.WahlvorschlaegeModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlag.WahlvorschlagModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Set;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WahlvorschlaegeClientMapperTest {

    private final WahlvorschlaegeClientMapper unitUnderTest = Mappers.getMapper(WahlvorschlaegeClientMapper.class);

    @Nested
    class ToModel {

        @Test
        void isMapped() {
            val stimmzettelgebietID = "stimmzettelgebietID";
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            val dtoToMap = new WahlvorschlaegeDTO();
            dtoToMap.setStimmzettelgebietID(stimmzettelgebietID);
            dtoToMap.setWahlID(wahlID);
            dtoToMap.setWahlbezirkID(wahlbezirkID);

            val wahlvorschlag1 = new WahlvorschlagDTO();
            wahlvorschlag1.setErhaeltStimmen(true);
            wahlvorschlag1.setIdentifikator("identifikator1");
            wahlvorschlag1.setKurzname("kurzname1");
            wahlvorschlag1.setOrdnungszahl(1L);

            val kandidat11 = new KandidatDTO();
            kandidat11.setIdentifikator("kandidat11");
            kandidat11.setDirektkandidat(true);
            kandidat11.setEinzelbewerber(true);
            kandidat11.setName("name11");
            kandidat11.setListenposition(1L);
            kandidat11.setTabellenSpalteInNiederschrift(1L);

            val kandidat12 = new KandidatDTO();
            kandidat12.setIdentifikator("kandidat12");
            kandidat12.setDirektkandidat(false);
            kandidat12.setEinzelbewerber(false);
            kandidat12.setName("name12");
            kandidat12.setListenposition(2L);
            kandidat12.setTabellenSpalteInNiederschrift(2L);
            wahlvorschlag1.setKandidaten(Set.of(kandidat11, kandidat12));

            val wahlvorschlag2 = new WahlvorschlagDTO();
            wahlvorschlag2.setErhaeltStimmen(false);
            wahlvorschlag2.setIdentifikator("identifikator2");
            wahlvorschlag2.setKurzname("kurzname2");
            wahlvorschlag2.setOrdnungszahl(2L);

            val kandidat21 = new KandidatDTO();
            kandidat21.setIdentifikator("kandidat21");
            kandidat21.setDirektkandidat(true);
            kandidat21.setEinzelbewerber(true);
            kandidat21.setName("name21");
            kandidat21.setListenposition(3L);
            kandidat21.setTabellenSpalteInNiederschrift(3L);

            val kandidat22 = new KandidatDTO();
            kandidat22.setIdentifikator("kandidat22");
            kandidat22.setDirektkandidat(false);
            kandidat22.setEinzelbewerber(false);
            kandidat22.setName("name22");
            kandidat22.setListenposition(4L);
            kandidat22.setTabellenSpalteInNiederschrift(4L);
            wahlvorschlag2.setKandidaten(Set.of(kandidat21, kandidat22));

            val wahlvorschlaege = Set.of(wahlvorschlag1, wahlvorschlag2);
            dtoToMap.setWahlvorschlaege(wahlvorschlaege);
            Assertions.assertThat(dtoToMap).hasNoNullFieldsOrProperties();

            val result = unitUnderTest.toModel(dtoToMap);

            val expectedWahlvorschlaege = Set.of(new WahlvorschlagModel("identifikator1", 1L, "kurzname1", true, Set.of(
                            new KandidatModel("kandidat11", "name11", 1L, true, 1L, true),
                            new KandidatModel("kandidat12", "name12", 2L, false, 2L, false))),
                    new WahlvorschlagModel("identifikator2", 2L, "kurzname2", false, Set.of(
                            new KandidatModel("kandidat21", "name21", 3L, true, 3L, true),
                            new KandidatModel("kandidat22", "name22", 4L, false, 4L, false))));
            val expectedResult = new WahlvorschlaegeModel(new BezirkUndWahlID(wahlID, wahlbezirkID), stimmzettelgebietID, expectedWahlvorschlaege);

            Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
        }
    }
}
