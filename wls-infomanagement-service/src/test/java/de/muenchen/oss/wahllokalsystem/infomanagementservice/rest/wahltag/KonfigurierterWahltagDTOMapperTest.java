package de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.wahltag;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.wahltag.KonfigurierterWahltagModel;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class KonfigurierterWahltagDTOMapperTest {

    private final KonfigurierterWahltagDTOMapper unitUnderTest = Mappers.getMapper(KonfigurierterWahltagDTOMapper.class);

    @Nested
    class ToDTO {

        @Test
        void nullInNullOut() {
            Assertions.assertThat(unitUnderTest.toModel(null)).isNull();
        }

        @Test
        void isMappedToDTO() {
            val wahltag = LocalDate.now();
            val wahltagID = "1-2-3";
            val wahltagStatus = WahltagStatus.INAKTIV;
            val nummer = "4711";

            val modelInput = new KonfigurierterWahltagModel(wahltag, wahltagID, wahltagStatus, nummer);
            val dtoExpected = new KonfigurierterWahltagDTO(wahltag, wahltagID, wahltagStatus, nummer);

            val result = unitUnderTest.toDTO(modelInput);
            Assertions.assertThat(result).isEqualTo(dtoExpected);
        }

    }

    @Nested
    class ToModel {

        @Test
        void nullInNullOut() {
            Assertions.assertThat(unitUnderTest.toDTO(null)).isNull();
        }

        @Test
        void isMappedToModel() {
            val wahltag = LocalDate.now();
            val wahltagID = "1-2-3";
            val wahltagStatus = WahltagStatus.INAKTIV;
            val nummer = "4711";

            val dtoInput = new KonfigurierterWahltagDTO(wahltag, wahltagID, wahltagStatus, nummer);
            val modelExpected = new KonfigurierterWahltagModel(wahltag, wahltagID, wahltagStatus, nummer);

            val result = unitUnderTest.toModel(dtoInput);
            Assertions.assertThat(result).isEqualTo(modelExpected);
        }
    }

    @Nested
    class ToDTOList {
        @Test
        void nullInNullOut() {
            Assertions.assertThat(unitUnderTest.toDTOList(null)).isNull();
        }

        @Test
        void isMappedToDTOList() {
            val wahltag_1 = LocalDate.now();
            val wahltagID_1 = "1-2-3";
            val wahltagStatus_1 = WahltagStatus.INAKTIV;
            val nummer_1 = "4711";

            val wahltag_2 = LocalDate.now();
            val wahltagID_2 = "4-5-6";
            val wahltagStatus_2 = WahltagStatus.AKTIV;
            val nummer_2 = "089";

            val wahltag_3 = LocalDate.now();
            val wahltagID_3 = "7-8-9";
            val wahltagStatus_3 = WahltagStatus.INAKTIV;
            val nummer_3 = "0190";

            val modelInput_1 = new KonfigurierterWahltagModel(wahltag_1, wahltagID_1, wahltagStatus_1, nummer_1);
            val modelInput_2 = new KonfigurierterWahltagModel(wahltag_2, wahltagID_2, wahltagStatus_2, nummer_2);
            val modelInput_3 = new KonfigurierterWahltagModel(wahltag_3, wahltagID_3, wahltagStatus_3, nummer_3);
            KonfigurierterWahltagModel[] modelArr = { modelInput_1, modelInput_2, modelInput_3 };
            List<KonfigurierterWahltagModel> konfigurierteWahltageModelListInput = Arrays.asList(modelArr);

            val dtoExpected_1 = new KonfigurierterWahltagDTO(wahltag_1, wahltagID_1, wahltagStatus_1, nummer_1);
            val dtoExpected_2 = new KonfigurierterWahltagDTO(wahltag_2, wahltagID_2, wahltagStatus_2, nummer_2);
            val dtoExpected_3 = new KonfigurierterWahltagDTO(wahltag_3, wahltagID_3, wahltagStatus_3, nummer_3);
            KonfigurierterWahltagDTO[] dtoArr = { dtoExpected_1, dtoExpected_2, dtoExpected_3 };
            List<KonfigurierterWahltagDTO> konfigurierteWahltageDTOListExpected = Arrays.asList(dtoArr);

            val result = unitUnderTest.toDTOList(konfigurierteWahltageModelListInput);
            Assertions.assertThat(result).isEqualTo(konfigurierteWahltageDTOListExpected);
        }
    }
}
