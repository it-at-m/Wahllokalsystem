package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.kopfdaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.StimmzettelgebietsartModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.KopfdatenModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class KopfdatenDTOMapperTest {

    private final KopfdatenDTOMapper unitUnderTest = Mappers.getMapper(KopfdatenDTOMapper.class);

    @Nested
    class FromKopfdatenModelToKopfdatenDTO {

        @Test
        void nullInNullOut() {
            Assertions.assertThat(unitUnderTest.toDTO(null)).isNull();
        }

        @Test
        void isMappedToDTO() {

            val kopfdatenModel = new KopfdatenModel(new BezirkUndWahlID("wahlID1", "wahlbezirkID1"),
                "LHM", StimmzettelgebietsartModel.SK, "szgNummer1", "szgName1",
                "wahlName1", "wbzNummer1");

            val dtoExpected = new KopfdatenDTO("wahlID1", "wahlbezirkID1", "LHM",
                StimmzettelgebietsartDTO.SK, "szgNummer1", "szgName1",
                "wahlName1", "wbzNummer1");

            val result = unitUnderTest.toDTO(kopfdatenModel);
            Assertions.assertThat(result).isEqualTo(dtoExpected);
        }
    }
}
