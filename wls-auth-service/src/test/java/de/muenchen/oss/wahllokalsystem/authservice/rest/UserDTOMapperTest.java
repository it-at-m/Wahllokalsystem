package de.muenchen.oss.wahllokalsystem.authservice.rest;

import de.muenchen.oss.wahllokalsystem.authservice.service.UserModel;
import de.muenchen.oss.wahllokalsystem.authservice.service.WahlbezirksartModel;
import java.time.LocalDate;
import java.util.Set;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class UserDTOMapperTest {

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
}
