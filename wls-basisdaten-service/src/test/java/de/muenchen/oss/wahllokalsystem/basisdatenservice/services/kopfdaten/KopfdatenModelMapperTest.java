package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.Stimmzettelgebietsart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.StimmzettelgebietsartModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.MockDataFactory;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class KopfdatenModelMapperTest {

    private final KopfdatenModelMapper unitUnderTest = Mappers.getMapper(KopfdatenModelMapper.class);

    @Test
    void fromListOfEntitiesToListOfModels() {
        val kopfdatenEntity1 = MockDataFactory.createKopfdatenEntityFor("wahlID1", "wahlbezirkID1_1",
                Stimmzettelgebietsart.SG, "Munich1", "120",
                "Bundestagswahl", "1201");
        val kopfdatenEntity2 = MockDataFactory.createKopfdatenEntityFor("wahlID2", "wahlbezirkID1_2",
                Stimmzettelgebietsart.SG, "Munich2", "121",
                "Bundestagswahl", "1202");
        val entitiesToMap = List.of(kopfdatenEntity1, kopfdatenEntity2);
        val result = unitUnderTest.fromListOfEntitiesToListOfModels(entitiesToMap);

        val kopfdatenModel1 = MockDataFactory.createKopfdatenModelFor("wahlID1", "wahlbezirkID1_1",
                StimmzettelgebietsartModel.SG, "120","Munich1",
                "Bundestagswahl", "1201");
        val kopfdatenModel2 = MockDataFactory.createKopfdatenModelFor("wahlID2", "wahlbezirkID1_2",
                StimmzettelgebietsartModel.SG, "121","Munich2",
                "Bundestagswahl", "1202");
        val expectedResult = List.of(kopfdatenModel1, kopfdatenModel2);

        Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void toModel(){
        val kopfdatenEntity1 = MockDataFactory.createKopfdatenEntityFor("wahlID1", "wahlbezirkID1_1",
                Stimmzettelgebietsart.SG, "Munich1", "120",
                "Bundestagswahl", "1201");
        val result = unitUnderTest.toModel(kopfdatenEntity1);

        val kopfdatenModel1 = MockDataFactory.createKopfdatenModelFor("wahlID1", "wahlbezirkID1_1",
                StimmzettelgebietsartModel.SG, "120","Munich1",
                "Bundestagswahl", "1201");

        Assertions.assertThat(result).isEqualTo(kopfdatenModel1);
    }

    @Test
    void toEntity(){
        val kopfdatenModel1 = MockDataFactory.createKopfdatenModelFor("wahlID1", "wahlbezirkID1_1",
                StimmzettelgebietsartModel.SG, "120","Munich1",
                "Bundestagswahl", "1201");
        val result = unitUnderTest.toEntity(kopfdatenModel1);

        val kopfdatenEntity1 = MockDataFactory.createKopfdatenEntityFor("wahlID1", "wahlbezirkID1_1",
                Stimmzettelgebietsart.SG, "Munich1", "120",
                "Bundestagswahl", "1201");

        Assertions.assertThat(result).isEqualTo(kopfdatenEntity1);
    }
}
