package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahltagRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahltagDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahltageDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles(TestConstants.SPRING_TEST_PROFILE)
@AutoConfigureWireMock
public class WahltageServiceSecurityTest {

    @Autowired
    WahltageService wahltageService;

    @Autowired
    WahltagRepository wahltagRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    class GetWahltage {

        @AfterEach
        void tearDown() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_DELETE_WAHLTAGE);
            wahltagRepository.deleteAll();
        }

        @Test
        void accessGranted() throws Exception {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_WAHLTAGE);

            String requestDate = LocalDate.now().minusMonths(3).toString();
            val eaiWahltage = createClientWahltageDTO();

            WireMock.stubFor(WireMock.get("/wahldaten/wahltage?includingSince=" + requestDate)
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(eaiWahltage))));

            Assertions.assertThatNoException().isThrownBy(() -> wahltageService.getWahltage());
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariations")
        void missingAuthorityCausesFailWithAccessDenied(final ArgumentsAccessor argumentsAccessor) throws Exception {
            SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));

            String requestDate = LocalDate.now().minusMonths(3).toString();
            val eaiWahltage = createClientWahltageDTO();

            WireMock.stubFor(WireMock.get("/wahldaten/wahltage?includingSince=" + requestDate)
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(eaiWahltage))));

            Assertions.assertThatException().isThrownBy(() -> wahltageService.getWahltage())
                    .isInstanceOf(
                            AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariations() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_DELETE_WAHLTAGE);
        }

        private WahltageDTO createClientWahltageDTO() {
            val dto = new WahltageDTO();
            val wahltag1 = new WahltagDTO();
            wahltag1.setIdentifikator("identifikatorWahltag1");
            wahltag1.setBeschreibung("beschreibungWahltag1");
            wahltag1.setNummer("nummerWahltag1");
            wahltag1.setTag(LocalDate.now().minusMonths(2));

            val wahltag2 = new WahltagDTO();
            wahltag2.setIdentifikator("identifikatorWahltag2");
            wahltag2.setBeschreibung("beschreibungWahltag2");
            wahltag2.setNummer("nummerWahltag2");
            wahltag2.setTag(LocalDate.now().minusMonths(1));

            val wahltag3 = new WahltagDTO();
            wahltag3.setIdentifikator("identifikatorWahltag3");
            wahltag3.setBeschreibung("beschreibungWahltag3");
            wahltag3.setNummer("nummerWahltag3");
            wahltag3.setTag(LocalDate.now().plusMonths(1));

            dto.setWahltage(Set.of(wahltag3, wahltag2, wahltag1));

            return dto;
        }
    }
}
