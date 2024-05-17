package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.wahltag;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.wahltag.KonfigurierterWahltag;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.wahltag.WahltagStatus;
import java.time.LocalDate;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class KonfigurierterWahltagModelMapperTest {

    private final KonfigurierterWahltagModelMapper unitUnderTest = Mappers.getMapper(KonfigurierterWahltagModelMapper.class);

    @Nested
    class ToModel {
        @Test
        void isMapped() {
            val wahltag = LocalDate.now();
            val wahltagID = "wahltagID";
            val wahltagStatus = WahltagStatus.AKTIV;
            val nummer = "nummer";
            val entityToMap = new KonfigurierterWahltag(wahltag, wahltagID, wahltagStatus, nummer);

            val expectedModel = new KonfigurierterWahltagModel(wahltag, wahltagID, wahltagStatus, nummer);

            Assertions.assertThat(unitUnderTest.toModel(entityToMap)).isEqualTo(expectedModel);
        }
    }

    @Nested
    class ToEntity {
        @Test
        void isMapped() {
            val wahltag = LocalDate.now();
            val wahltagID = "wahltagID";
            val wahltagStatus = WahltagStatus.AKTIV;
            val nummer = "nummer";
            val modelToMap = new KonfigurierterWahltagModel(wahltag, wahltagID, wahltagStatus, nummer);

            val expectedEntity = new KonfigurierterWahltag(wahltag, wahltagID, wahltagStatus, nummer);

            Assertions.assertThat(unitUnderTest.toEntity(modelToMap)).isEqualTo(expectedEntity);
        }

        @Test
        void usesDefaultValueForStatus() {
            val wahltag = LocalDate.now();
            val wahltagID = "wahltagID";
            val nummer = "nummer";
            val modelToMap = new KonfigurierterWahltagModel(wahltag, wahltagID, null, nummer);

            val expectedEntity = new KonfigurierterWahltag(wahltag, wahltagID, WahltagStatus.INAKTIV, nummer);

            Assertions.assertThat(unitUnderTest.toEntity(modelToMap)).isEqualTo(expectedEntity);
        }
    }

    @Nested
    class ToModelList {

        @Test
        void isMapped() {
            val wahltag1 = LocalDate.now().minusDays(1);
            val wahltagID1 = "wahltagID";
            val wahltagStatus1 = WahltagStatus.AKTIV;
            val nummer1 = "nummer";

            val wahltag2 = LocalDate.now().plusDays(1);
            val wahltagID2 = "wahltagID2";
            val wahltagStatus2 = WahltagStatus.INAKTIV;
            val nummer2 = "nummer2";

            val wahltag3 = LocalDate.now();
            val wahltagID3 = "wahltagID";
            val wahltagStatus3 = WahltagStatus.INAKTIV;
            val nummer3 = "nummer3";

            val entityListToMap = List.of(new KonfigurierterWahltag(wahltag1, wahltagID1, wahltagStatus1, nummer1),
                    new KonfigurierterWahltag(wahltag2, wahltagID2, wahltagStatus2, nummer2),
                    new KonfigurierterWahltag(wahltag3, wahltagID3, wahltagStatus3, nummer3));

            val expectedModelList = List.of(new KonfigurierterWahltagModel(wahltag1, wahltagID1, wahltagStatus1, nummer1),
                    new KonfigurierterWahltagModel(wahltag2, wahltagID2, wahltagStatus2, nummer2),
                    new KonfigurierterWahltagModel(wahltag3, wahltagID3, wahltagStatus3, nummer3));

            Assertions.assertThat(unitUnderTest.toModelList(entityListToMap)).isEqualTo(expectedModelList);
        }
    }

}
