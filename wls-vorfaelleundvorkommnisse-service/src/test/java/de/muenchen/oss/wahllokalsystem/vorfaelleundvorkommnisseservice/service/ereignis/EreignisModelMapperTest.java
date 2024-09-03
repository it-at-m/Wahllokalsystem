package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereigniseintrag;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisModelMapper;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.TestdataFactory;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class EreignisModelMapperTest {

    private final EreignisModelMapper unitUnderTest = Mappers.getMapper(EreignisModelMapper.class);

    @Test
    void toModel() {
        java.util.List<Ereigniseintrag> ereigniseintrag = new java.util.ArrayList<>();
        val ereignisEntity = TestdataFactory.createEreignisEntityWithData("", true, true, ereigniseintrag);
        val ereignisModelFromEntity = TestdataFactory.createEreignisModelFromEntity(ereignisEntity);


        val result = unitUnderTest.toModel(ereignisEntity);

        Assertions.assertThat(result).isEqualTo(ereignisModelFromEntity);
    }

    @Test
    void toEntity() {
        java.util.List<Ereigniseintrag> ereigniseintrag = new java.util.ArrayList<>();
        val ereignisModel = TestdataFactory.createEreignisModelWithData("", true, true, ereigniseintrag);
        val ereignisEntityFromModel = TestdataFactory.createEreignisEntityFromModel(ereignisModel);

        val result = unitUnderTest.toEntity(ereignisModel);

        Assertions.assertThat(result).isEqualTo(ereignisEntityFromModel);
    }

}
