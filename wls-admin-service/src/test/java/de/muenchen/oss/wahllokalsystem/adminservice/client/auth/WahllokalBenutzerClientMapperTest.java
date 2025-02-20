package de.muenchen.oss.wahllokalsystem.adminservice.client.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.muenchen.oss.wahllokalsystem.adminservice.eai.auth.model.WahllokalUserInfoDTO;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahllokalbenutzer.TripleOfWahlbezirkIDWahlnummerWahlIDModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahllokalbenutzer.WahllokalBenutzerModel;
import java.time.LocalDate;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WahllokalBenutzerClientMapperTest {

    private final WahllokalBenutzerClientMapper unitUnderTest = Mappers.getMapper(WahllokalBenutzerClientMapper.class);

    @Nested
    class ToWahllokalUserInfoDTO {

        @Test
        void should_returnNull_when_nullIsGiven() {
            Assertions.assertThat(unitUnderTest.toWahllokalUserInfoDTO(null)).isNull();
        }

        @Test
        void should_mapToWahllokalUserInfoDTO_when_givenWahllokalBenutzerModel() {
            val wahllokalBenutzerModel = getListOfWahlLokalBenutzerModels().get(0);

            Assertions.assertThat(wahllokalBenutzerModel).hasNoNullFieldsOrProperties();

            val result = unitUnderTest.toWahllokalUserInfoDTO(wahllokalBenutzerModel);

            val expectedWahllokalUserInfoDTO = getListOfWahlLokalUserInfoDTO().get(0);

            Assertions.assertThat(result).isEqualTo(expectedWahllokalUserInfoDTO);
        }

        @Test
        void should_mapToDtoWithWahlbezirksartUWB_when_givenModelWithWahlbezirksartUWB() {
            val wahllokalBenutzerModel = getListOfWahlLokalBenutzerModels().get(0);

            Assertions.assertThat(wahllokalBenutzerModel).hasNoNullFieldsOrProperties();

            val result = unitUnderTest.toWahllokalUserInfoDTO(wahllokalBenutzerModel);

            val expectedWahllokalUserInfoDTO = getListOfWahlLokalUserInfoDTO().get(0);

            Assertions.assertThat(result.getWahlbezirksart()).isEqualTo(expectedWahllokalUserInfoDTO.getWahlbezirksart());
        }

        @Test
        void should_mapToDtoWithWahlbezirksartBWB_when_givenModelWithWahlbezirksartBWB() {
            val wahllokalBenutzerModel = getListOfWahlLokalBenutzerModels().get(1);

            Assertions.assertThat(wahllokalBenutzerModel).hasNoNullFieldsOrProperties();

            val result = unitUnderTest.toWahllokalUserInfoDTO(wahllokalBenutzerModel);

            val expectedWahllokalUserInfoDTO = getListOfWahlLokalUserInfoDTO().get(1);

            Assertions.assertThat(result.getWahlbezirksart()).isEqualTo(expectedWahllokalUserInfoDTO.getWahlbezirksart());
        }

        @Test
        void should_transfereSameWbidWahlnummerInformation_when_givenModelHasWbidWahlnummer() {
            val wahllokalBenutzerModel = getListOfWahlLokalBenutzerModels().get(0);

            Assertions.assertThat(wahllokalBenutzerModel.wbid_wahlnummer()).isNotEmpty();

            val result = unitUnderTest.toWahllokalUserInfoDTO(wahllokalBenutzerModel);

            val expectedWahllokalUserInfoDTO = getListOfWahlLokalUserInfoDTO().get(0);

            Assertions.assertThat(result.getWbidWahlnummer()).isEqualTo(expectedWahllokalUserInfoDTO.getWbidWahlnummer());
        }
    }

    @Nested
    class MapTripleToJsonAsString {

        @Test
        void should_returnNull_when_nullIsGiven() throws JsonProcessingException {
            Assertions.assertThat(unitUnderTest.mapTripleToJsonAsString(null)).isEqualTo("null");
        }

        @Test
        void should_mapToStringWbidWahlnummer_when_givenListOfTripleWbidWahlnummerWahlId() throws JsonProcessingException {
            val wbidWahlnummerWahlIdList = getListOfWahlLokalBenutzerModels().get(0).wbid_wahlnummer();

            Assertions.assertThat(wbidWahlnummerWahlIdList).isNotEmpty();

            val result = unitUnderTest.mapTripleToJsonAsString(wbidWahlnummerWahlIdList);

            val expectedStringOfWbidWahlnummerWahlId = getListOfWahlLokalUserInfoDTO().get(0).getWbidWahlnummer();

            Assertions.assertThat(expectedStringOfWbidWahlnummerWahlId).isEqualTo(result);
        }
    }

    @Nested
    class ToListOfWahllokalUserInfoDTO {

        @Test
        void should_returnNull_when_nullIsGiven() {
            Assertions.assertThat(unitUnderTest.toListOfWahllokalUserInfoDTO(null)).isNull();
        }

        @Test
        void should_mapToListOfWahllokalUserInfoDTO_when_givenListOfWahllokalBenutzerModel() {
            val wahllokalBenutzerModels = getListOfWahlLokalBenutzerModels();

            Assertions.assertThat(wahllokalBenutzerModels).isNotEmpty();

            val result = unitUnderTest.toListOfWahllokalUserInfoDTO(wahllokalBenutzerModels);

            val expectedWahllokalUserInfoDTOs = getListOfWahlLokalUserInfoDTO();

            Assertions.assertThat(result).isEqualTo(expectedWahllokalUserInfoDTOs);
        }
    }

    private List<WahllokalBenutzerModel> getListOfWahlLokalBenutzerModels() {
        val wahltag = LocalDate.now();
        val wahllokalBenutzerModel_1 = new WahllokalBenutzerModel(
                "wahlbezirkID1_0",
                "0001",
                wahltag,
                WahlbezirkArtModel.UWB,
                List.of(
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID1_0", "0", "wahlID0"),
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID1_1", "1", "wahlID1"),
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID1_2", "2", "wahlID2")));
        val wahllokalBenutzerModel_2 = new WahllokalBenutzerModel(
                "wahlbezirkID2_0",
                "0002",
                wahltag,
                WahlbezirkArtModel.BWB,
                List.of(
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID2_0", "0", "wahlID0"),
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID2_1", "1", "wahlID1"),
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID2_2", "2", "wahlID2")));
        val wahllokalBenutzerModel_3 = new WahllokalBenutzerModel(
                "wahlbezirkID3_0",
                "0003",
                wahltag,
                WahlbezirkArtModel.UWB,
                List.of(
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID3_0", "0", "wahlID0"),
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID3_1", "1", "wahlID1"),
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID3_2", "2", "wahlID2")));
        return List.of(wahllokalBenutzerModel_1, wahllokalBenutzerModel_2, wahllokalBenutzerModel_3);
    }

    private List<WahllokalUserInfoDTO> getListOfWahlLokalUserInfoDTO() {
        val wahltag = LocalDate.now();
        val wahllokalUserInfoDTO_1 = new WahllokalUserInfoDTO();
        wahllokalUserInfoDTO_1.setWahlbezirkID("wahlbezirkID1_0");
        wahllokalUserInfoDTO_1.setWahlbezirknummer("0001");
        wahllokalUserInfoDTO_1.setWahltag(wahltag);
        wahllokalUserInfoDTO_1.setWahlbezirksart(WahllokalUserInfoDTO.WahlbezirksartEnum.UWB);
        wahllokalUserInfoDTO_1.setWbidWahlnummer(
                "[" +
                        "{\"wahlbezirkID\":\"wahlbezirkID1_0\",\"wahlnummer\":\"0\",\"wahlID\":\"wahlID0\"}," +
                        "{\"wahlbezirkID\":\"wahlbezirkID1_1\",\"wahlnummer\":\"1\",\"wahlID\":\"wahlID1\"}," +
                        "{\"wahlbezirkID\":\"wahlbezirkID1_2\",\"wahlnummer\":\"2\",\"wahlID\":\"wahlID2\"}" +
                        "]");
        val wahllokalUserInfoDTO_2 = new WahllokalUserInfoDTO();
        wahllokalUserInfoDTO_2.setWahlbezirkID("wahlbezirkID2_0");
        wahllokalUserInfoDTO_2.setWahlbezirknummer("0002");
        wahllokalUserInfoDTO_2.setWahltag(wahltag);
        wahllokalUserInfoDTO_2.setWahlbezirksart(WahllokalUserInfoDTO.WahlbezirksartEnum.BWB);
        wahllokalUserInfoDTO_2.setWbidWahlnummer(
                "[" +
                        "{\"wahlbezirkID\":\"wahlbezirkID2_0\",\"wahlnummer\":\"0\",\"wahlID\":\"wahlID0\"}," +
                        "{\"wahlbezirkID\":\"wahlbezirkID2_1\",\"wahlnummer\":\"1\",\"wahlID\":\"wahlID1\"}," +
                        "{\"wahlbezirkID\":\"wahlbezirkID2_2\",\"wahlnummer\":\"2\",\"wahlID\":\"wahlID2\"}" +
                        "]");
        val wahllokalUserInfoDTO_3 = new WahllokalUserInfoDTO();
        wahllokalUserInfoDTO_3.setWahlbezirkID("wahlbezirkID3_0");
        wahllokalUserInfoDTO_3.setWahlbezirknummer("0003");
        wahllokalUserInfoDTO_3.setWahltag(wahltag);
        wahllokalUserInfoDTO_3.setWahlbezirksart(WahllokalUserInfoDTO.WahlbezirksartEnum.UWB);
        wahllokalUserInfoDTO_3.setWbidWahlnummer(
                "[" +
                        "{\"wahlbezirkID\":\"wahlbezirkID3_0\",\"wahlnummer\":\"0\",\"wahlID\":\"wahlID0\"}," +
                        "{\"wahlbezirkID\":\"wahlbezirkID3_1\",\"wahlnummer\":\"1\",\"wahlID\":\"wahlID1\"}," +
                        "{\"wahlbezirkID\":\"wahlbezirkID3_2\",\"wahlnummer\":\"2\",\"wahlID\":\"wahlID2\"}" +
                        "]");
        return List.of(wahllokalUserInfoDTO_1, wahllokalUserInfoDTO_2, wahllokalUserInfoDTO_3);
    }
}
