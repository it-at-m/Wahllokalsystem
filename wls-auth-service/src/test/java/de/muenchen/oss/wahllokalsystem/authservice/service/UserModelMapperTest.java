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
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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
            Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
        }

        private WahllokalUserInfoModel createWahlbezirksartModelWithAllDataSet(final WahlbezirksartModel wahlbezirkArt) {
            val userInfoModel = new WahllokalUserInfoModel("wahlbezirkNummer", LocalDate.now(), "wahlbezirkID", wahlbezirkArt, "wbid_wahlnummer");
            Assertions.assertThat(userInfoModel).hasNoNullFieldsOrProperties();
            return userInfoModel;
        }
    }

    @Nested
    class ToStringSecurityUser {

        @Test
        void should_mapToUser_when_allTargetRequiredPropertiesAreGiven() {
            val username = "username";
            val password = "password";

            val userTopMap = new User();
            userTopMap.setUsername(username);
            userTopMap.setPassword(password);
            userTopMap.setAccountNonLocked(true);
            userTopMap.setAuthorities(Set.of(new Authority("authority1", Collections.emptySet(), Collections.emptySet()),
                    new Authority("authority2", Collections.emptySet(), Collections.emptySet())));

            val result = unitUnderTest.toStringSecurityUser(userTopMap);

            val expectedAuthorities = Set.of(new SimpleGrantedAuthority("authority1"), new SimpleGrantedAuthority("authority2"));
            val expectedResult = new org.springframework.security.core.userdetails.User(username, password, true, true, true, true, expectedAuthorities);

            Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
        }

        @Test
        void should_mapToUserWithEmptyStringForPassword_when_userPasswordIsNull() {
            val userTopMap = new User();
            userTopMap.setUsername("username");
            userTopMap.setPassword(null);
            userTopMap.setAuthorities(Collections.emptySet());

            val result = unitUnderTest.toStringSecurityUser(userTopMap);

            Assertions.assertThat(result.getPassword()).isEmpty();
        }
    }

    @Nested
    class AuthoritiesToGrantedAuthorities {

        @Test
        void should_returnSetOfGrantedAuthorities_when_setOfAuthoritiesIsGiven() {
            val authoritiesToMap = Set.of(new Authority("authority1", Collections.emptySet(), Collections.emptySet()),
                    new Authority("authority2", Collections.emptySet(), Collections.emptySet()));

            val result = unitUnderTest.authoritiesToGrantedAuthorities(authoritiesToMap);

            val expectedResult = Set.of(new SimpleGrantedAuthority("authority1"), new SimpleGrantedAuthority("authority2"));
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void should_returnEmptySet_when_parameterIsNull() {
            Assertions.assertThat(unitUnderTest.authoritiesToGrantedAuthorities(null)).isEmpty();
        }
    }

}
