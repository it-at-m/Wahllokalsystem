package de.muenchen.oss.wahllokalsystem.adminservice.client.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.muenchen.oss.wahllokalsystem.adminservice.eai.auth.model.WahllokalUserInfoDTO;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahllokalbenutzer.TripleOfWahlbezirkIDWahlnummerWahlIDModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahllokalbenutzer.WahllokalBenutzerModel;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WahllokalBenutzerClientMapperTest {

    private final WahllokalBenutzerClientMapper unitUnderTest = Mappers.getMapper(WahllokalBenutzerClientMapper.class);

    @Nested
    class ToDTO{

        @Nested
        class ToWahllokalUserInfoDTO {

            @Test
            void should_returnNull_when_nullIsGiven() {
                Assertions.assertThat(unitUnderTest.toWahllokalUserInfoDTO(null)).isNull();
            }

            @Test
            void should_mapToWahllokalUserInfoDTO_when_givenWahllokalBenutzerModel() {
                val wahllokalBenutzerModel = getWahllokalBenutzerModelWithBWB();

                Assertions.assertThat(wahllokalBenutzerModel).hasNoNullFieldsOrProperties();

                val result = unitUnderTest.toWahllokalUserInfoDTO(wahllokalBenutzerModel);

                val expectedWahllokalUserInfoDTO = getWahllokalUserInfoDTOWithBWB();

                Assertions.assertThat(result).isEqualTo(expectedWahllokalUserInfoDTO);
            }

            @Test
            void should_mapToDtoWithWahlbezirksartUWB_when_givenModelWithWahlbezirksartUWB() {
                val wahllokalBenutzerModel = getWahllokalBenutzerModelWithUWB();

                Assertions.assertThat(wahllokalBenutzerModel).hasNoNullFieldsOrProperties();

                val result = unitUnderTest.toWahllokalUserInfoDTO(wahllokalBenutzerModel);

                val expectedWahllokalUserInfoDTO = getWahllokalUserInfoDTOWithUWB();

                Assertions.assertThat(result.getWahlbezirksart()).isEqualTo(expectedWahllokalUserInfoDTO.getWahlbezirksart());
            }

            @Test
            void should_mapToDtoWithWahlbezirksartBWB_when_givenModelWithWahlbezirksartBWB() {
                val wahllokalBenutzerModel = getWahllokalBenutzerModelWithBWB();

                Assertions.assertThat(wahllokalBenutzerModel).hasNoNullFieldsOrProperties();

                val result = unitUnderTest.toWahllokalUserInfoDTO(wahllokalBenutzerModel);

                val expectedWahllokalUserInfoDTO = getWahllokalUserInfoDTOWithBWB();

                Assertions.assertThat(result.getWahlbezirksart()).isEqualTo(expectedWahllokalUserInfoDTO.getWahlbezirksart());
            }

            @Test
            void should_transfereSameWbidWahlnummerInformation_when_givenModelHasWbidWahlnummer() {
                val wahllokalBenutzerModel = getWahllokalBenutzerModelWithBWB();

                Assertions.assertThat(wahllokalBenutzerModel.wbid_wahlnummer()).isNotEmpty();

                val result = unitUnderTest.toWahllokalUserInfoDTO(wahllokalBenutzerModel);

                val expectedWahllokalUserInfoDTO = getWahllokalUserInfoDTOWithBWB();

                Assertions.assertThat(result.getWbidWahlnummer()).isEqualTo(expectedWahllokalUserInfoDTO.getWbidWahlnummer());
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
    }

    @Nested
    class MapTripleToJsonAsString {

        @Test
        void should_returnNull_when_nullIsGiven() throws JsonProcessingException {
            Assertions.assertThat(unitUnderTest.mapTripleToJsonAsString(null)).isEqualTo("null");
        }

        @Test
        void should_mapToStringWbidWahlnummer_when_givenListOfTripleWbidWahlnummerWahlId() throws JsonProcessingException {
            val wbidWahlnummerWahlIdList = getWahllokalBenutzerModelWithUWB().wbid_wahlnummer();

            Assertions.assertThat(wbidWahlnummerWahlIdList).isNotEmpty();

            val result = unitUnderTest.mapTripleToJsonAsString(wbidWahlnummerWahlIdList);

            val expectedStringOfWbidWahlnummerWahlId = getWahllokalUserInfoDTOWithUWB().getWbidWahlnummer();

            Assertions.assertThat(expectedStringOfWbidWahlnummerWahlId).isEqualTo(result);
        }
    }

    private List<WahllokalBenutzerModel> getListOfWahlLokalBenutzerModels() {
        val wahltag = LocalDate.now();
        List<WahllokalBenutzerModel> listOfWahlLokalBenutzerModel = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            WahlbezirkArtModel wahlbezirkModel = i % 2 == 0 ? WahlbezirkArtModel.UWB : WahlbezirkArtModel.BWB;
            val wahllokalBenutzerModel = new WahllokalBenutzerModel(
                    String.format("wahlbezirkID%s_0", i),
                            String.format("000%s", i),
                    wahltag,
                    wahlbezirkModel,
                    List.of(
                            new TripleOfWahlbezirkIDWahlnummerWahlIDModel(String.format("wahlbezirkID%s_0", i), "0", "wahlID0"),
                            new TripleOfWahlbezirkIDWahlnummerWahlIDModel(String.format("wahlbezirkID%s_1", i), "1", "wahlID1"),
                            new TripleOfWahlbezirkIDWahlnummerWahlIDModel(String.format("wahlbezirkID%s_2", i), "2", "wahlID2")));
            listOfWahlLokalBenutzerModel.add(wahllokalBenutzerModel);
        }
        return listOfWahlLokalBenutzerModel;
    }

    private List<WahllokalUserInfoDTO> getListOfWahlLokalUserInfoDTO() {
        val wahltag = LocalDate.now();
        List<WahllokalUserInfoDTO> listOfWahlLokalUserInfoDTO = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            WahllokalUserInfoDTO.WahlbezirksartEnum wahlbezirkModel = i % 2 == 0 ? WahllokalUserInfoDTO.WahlbezirksartEnum.UWB : WahllokalUserInfoDTO.WahlbezirksartEnum.BWB;
            val wahllokalUserInfoDTO = new WahllokalUserInfoDTO();
            wahllokalUserInfoDTO.setWahlbezirkID(String.format("wahlbezirkID%s_0", i));
            wahllokalUserInfoDTO.setWahlbezirknummer(String.format("000%s", i));
            wahllokalUserInfoDTO.setWahltag(wahltag);
            wahllokalUserInfoDTO.setWahlbezirksart(wahlbezirkModel);
            wahllokalUserInfoDTO.setWbidWahlnummer(
                    String.format("[" +
                            "{\"wahlbezirkID\":\"wahlbezirkID%s_0\",\"wahlnummer\":\"0\",\"wahlID\":\"wahlID0\"}," +
                            "{\"wahlbezirkID\":\"wahlbezirkID%s_1\",\"wahlnummer\":\"1\",\"wahlID\":\"wahlID1\"}," +
                            "{\"wahlbezirkID\":\"wahlbezirkID%s_2\",\"wahlnummer\":\"2\",\"wahlID\":\"wahlID2\"}" +
                            "]",i, i, i));
            listOfWahlLokalUserInfoDTO.add(wahllokalUserInfoDTO);
        }
        return listOfWahlLokalUserInfoDTO;
    }


    private WahllokalBenutzerModel getWahllokalBenutzerModelWithUWB() {
        val wahltag = LocalDate.now();
        return new WahllokalBenutzerModel(
                "wahlbezirkID1_0",
                "0001",
                wahltag,
                WahlbezirkArtModel.UWB,
                List.of(
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID1_0", "0", "wahlID0"),
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID1_1", "1", "wahlID1"),
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID1_2", "2", "wahlID2")));
    }

    private WahllokalBenutzerModel getWahllokalBenutzerModelWithBWB() {
        val wahltag = LocalDate.now();
        return new WahllokalBenutzerModel(
                "wahlbezirkID1_0",
                "0001",
                wahltag,
                WahlbezirkArtModel.BWB,
                List.of(
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID1_0", "0", "wahlID0"),
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID1_1", "1", "wahlID1"),
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID1_2", "2", "wahlID2")));
    }

    private WahllokalUserInfoDTO getWahllokalUserInfoDTOWithUWB() {
        val wahltag = LocalDate.now();
        val wahllokalUserInfoDTO = new WahllokalUserInfoDTO();
        wahllokalUserInfoDTO.setWahlbezirkID("wahlbezirkID1s_0");
        wahllokalUserInfoDTO.setWahlbezirknummer("0001");
        wahllokalUserInfoDTO.setWahltag(wahltag);
        wahllokalUserInfoDTO.setWahlbezirksart(WahllokalUserInfoDTO.WahlbezirksartEnum.UWB);
        wahllokalUserInfoDTO.setWbidWahlnummer(
                "[" +
                        "{\"wahlbezirkID\":\"wahlbezirkID1_0\",\"wahlnummer\":\"0\",\"wahlID\":\"wahlID0\"}," +
                        "{\"wahlbezirkID\":\"wahlbezirkID1_1\",\"wahlnummer\":\"1\",\"wahlID\":\"wahlID1\"}," +
                        "{\"wahlbezirkID\":\"wahlbezirkID1_2\",\"wahlnummer\":\"2\",\"wahlID\":\"wahlID2\"}" +
                        "]");
        return wahllokalUserInfoDTO;
    }

    private WahllokalUserInfoDTO getWahllokalUserInfoDTOWithBWB() {
        val wahltag = LocalDate.now();
        val wahllokalUserInfoDTO = new WahllokalUserInfoDTO();
        wahllokalUserInfoDTO.setWahlbezirkID("wahlbezirkID1_0");
        wahllokalUserInfoDTO.setWahlbezirknummer("0001");
        wahllokalUserInfoDTO.setWahltag(wahltag);
        wahllokalUserInfoDTO.setWahlbezirksart(WahllokalUserInfoDTO.WahlbezirksartEnum.BWB);
        wahllokalUserInfoDTO.setWbidWahlnummer(
                "[" +
                        "{\"wahlbezirkID\":\"wahlbezirkID1_0\",\"wahlnummer\":\"0\",\"wahlID\":\"wahlID0\"}," +
                        "{\"wahlbezirkID\":\"wahlbezirkID1_1\",\"wahlnummer\":\"1\",\"wahlID\":\"wahlID1\"}," +
                        "{\"wahlbezirkID\":\"wahlbezirkID1_2\",\"wahlnummer\":\"2\",\"wahlID\":\"wahlID2\"}" +
                        "]");
        return wahllokalUserInfoDTO;
    }
}
