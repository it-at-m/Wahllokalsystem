package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignisart;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisModelMapper;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.TestdataFactory;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

class EreignisModelMapperTest {

    private final EreignisModelMapper unitUnderTest = Mappers.getMapper(EreignisModelMapper.class);

    @Test
    void toModel() {
        val ereignisEntity = TestdataFactory.createEreignisEntityWithData("", "", LocalDateTime.now(), Ereignisart.VORFALL);
        val ereignisModelFromEntity = TestdataFactory.createEreignisModelFromEntity(ereignisEntity);

        val result = unitUnderTest.toModel(ereignisEntity);
        Assertions.assertThat(result).isEqualTo(ereignisModelFromEntity);
    }

    @Test
    void toEntity() {
        val ereignisModel = TestdataFactory.createEreignisModelWithData("", "", LocalDateTime.now(), Ereignisart.VORFALL);
        val ereignisEntityFromModel = TestdataFactory.createEreignisEntityFromModel(ereignisModel);

        val result = unitUnderTest.toEntity(ereignisModel);
        Assertions.assertThat(result).isEqualTo(ereignisEntityFromModel);
    }
}
