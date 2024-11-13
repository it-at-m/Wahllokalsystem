package de.muenchen.oss.wahllokalsystem.authservice.rest;

import de.muenchen.oss.wahllokalsystem.authservice.service.UserModel;
import de.muenchen.oss.wahllokalsystem.authservice.service.UsersOfWahltagModel;
import de.muenchen.oss.wahllokalsystem.authservice.service.WahlbezirksartModel;
import de.muenchen.oss.wahllokalsystem.authservice.service.WahllokalUserInfoModel;
import java.time.LocalDate;
import java.util.Set;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;

class UserDTOMapperTest {

    @Nested
    class ToDTO {

        UserDTOMapper unitUnderTest = Mappers.getMapper(UserDTOMapper.class);

        @Nested
        class ToDTO {

            UserDTOMapper unitUnderTest = Mappers.getMapper(UserDTOMapper.class);

            @Test
            void should_returnDTO_when_givenModel() {
                val username = "Hansi";
                val email = "hansi@nixda.com";
                val userEnabled = true;
                val wahltagID = "wahltagID";
                val wahltag = LocalDate.now();
                val wahlbezirkID = "wahlbezirkID";
                val wahlbezirkNummer = "wahlbezirkNummer";
                val wahlbezirksArt = WahlbezirksartModel.BWB;
                val pin = "123";
                val authorities = Set.of("auth1", "auth2");
                val wbid_wahlnummer = "wbid_wahlnummer";
                val modelToMap = new UserModel(username, email, userEnabled, wahltagID, wahltag, wahlbezirkID, wahlbezirkNummer,
                    wahlbezirksArt, pin, authorities, wbid_wahlnummer);

                val result = unitUnderTest.toDTO(modelToMap);

                val expectedResult = new UserDTO(username, email, userEnabled, wahltagID, wahltag, wahlbezirkID, wahlbezirkNummer,
                    WahlbezirksartDTO.BWB, pin, authorities, wbid_wahlnummer);
                Assertions.assertThat(result).isEqualTo(expectedResult);
            }
        }

        @Nested
        class ToModel {

            @Test
            void should_returnModel_when_givenDTO() {
                val wahltagID = "wahltagID";
                val wahlbezirknummer = "wahlbezirknummer";
                val wahltag = LocalDate.now();
                val wahlbezirkID = "wahlbezirkID";
                val wahlbezirksart = WahlbezirksartDTO.UWB;
                val wbidWahlnummer = "wbidWahlnummer";
                val user1 = new WahllokalUserInfoDTO(wahlbezirknummer, wahltag, wahlbezirkID, wahlbezirksart, wbidWahlnummer);

                val result = unitUnderTest.toModel(wahltagID, List.of(user1));

                val expectedResult = new UsersOfWahltagModel(wahltagID,
                    List.of(new WahllokalUserInfoModel(wahlbezirknummer, wahltag, wahlbezirkID, WahlbezirksartModel.UWB, wbidWahlnummer)));
                Assertions.assertThat(result).isEqualTo(expectedResult);
            }

            @ParameterizedTest
            @EnumSource(WahlbezirksartDTO.class)
            void should_returnModelEnumValueWithSameValueAsDTO_when_givenAnyWahlbezirkArtDTOEnumValue(final WahlbezirksartDTO wahlbezirksartDTO) {
                val dtoToMap = new WahllokalUserInfoDTO(null, null, null, wahlbezirksartDTO, null);

                val result = unitUnderTest.toModel(null, List.of(dtoToMap));

                val expectedResult = new UsersOfWahltagModel(null,
                    List.of(new WahllokalUserInfoModel(null, null, null, WahlbezirksartModel.valueOf(wahlbezirksartDTO.name()), null)));
                Assertions.assertThat(result).isEqualTo(expectedResult);
            }
        }

}
