package de.muenchen.oss.wahllokalsystem.authservice.service;

import de.muenchen.oss.wahllokalsystem.authservice.domain.Authority;
import de.muenchen.oss.wahllokalsystem.authservice.domain.User;
import de.muenchen.oss.wahllokalsystem.authservice.domain.Wahlbezirksart;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class UserModelMapperTest {

    UserModelMapper unitUnderTest = Mappers.getMapper(UserModelMapper.class);

    @Nested
    class ToUser {

        @Test
        void should_mapToEntity_when_allParametersAreGiven() {
            val wahltagID = "wahltagID";
            final Set<Authority> authoritiesToLink = Collections.emptySet();
            val pin = "pin";
            val username = "username";
            val userInfo = createWahlbezirksartModelWithAllDataSet(WahlbezirksartModel.UWB);

            val result = unitUnderTest.toUser(wahltagID, userInfo, authoritiesToLink, pin, username);

            val expectedResult = new User(username, "dummy", "dummy@dummy.local", false, true, wahltagID, userInfo.wahltag(), userInfo.wahlbezirkID(),
                    userInfo.wahlbezirknummer(), Wahlbezirksart.UWB, pin, authoritiesToLink, userInfo.wbid_wahlnummer());
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        private WahllokalUserInfoModel createWahlbezirksartModelWithAllDataSet(final WahlbezirksartModel wahlbezirkArt) {
            val userInfoModel = new WahllokalUserInfoModel("wahlbezirkNummer", LocalDate.now(), "wahlbezirkID", wahlbezirkArt, "wbid_wahlnummer");
            Assertions.assertThat(userInfoModel).hasNoNullFieldsOrProperties();
            return userInfoModel;
        }
    }

}
