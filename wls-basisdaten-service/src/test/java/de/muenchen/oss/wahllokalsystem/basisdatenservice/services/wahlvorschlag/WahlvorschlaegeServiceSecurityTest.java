package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlag;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahlvorschlaegeRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.KandidatDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlvorschlaegeDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlvorschlagDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
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
public class WahlvorschlaegeServiceSecurityTest {

    @Autowired
    WahlvorschlaegeService wahlvorschlaegeService;

    @Autowired
    WahlvorschlaegeRepository wahlvorschlaegeRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    class GetWahlvorschlaege {

        @AfterEach
        void tearDown() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_DELETE_WAHLVORSCHLAEGE);
            wahlvorschlaegeRepository.deleteAll();
        }

        @Test
        void accessGranted() throws Exception {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_WAHLVORSCHLAEGE);

            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            val eaiWahlvorschlaege = createClientWahlvorschlaegeDTO();
            WireMock.stubFor(WireMock.get("/vorschlaege/wahl/" + wahlID + "/" + wahlbezirkID)
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(eaiWahlvorschlaege))));

            Assertions.assertThatNoException().isThrownBy(() -> wahlvorschlaegeService.getWahlvorschlaege(new BezirkUndWahlID(wahlID, wahlbezirkID)));
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariations")
        void missingAuthorityCausesFailWithAccessDenied(final ArgumentsAccessor argumentsAccessor) throws Exception {
            SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));

            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            val eaiWahlvorschlaege = createClientWahlvorschlaegeDTO();
            WireMock.stubFor(WireMock.get("/vorschlaege/wahl/" + wahlID + "/" + wahlbezirkID)
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(eaiWahlvorschlaege))));

            Assertions.assertThatException().isThrownBy(() -> wahlvorschlaegeService.getWahlvorschlaege(new BezirkUndWahlID(wahlID, wahlbezirkID)))
                    .isInstanceOf(
                            AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariations() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_DELETE_WAHLVORSCHLAEGE);
        }

        private WahlvorschlaegeDTO createClientWahlvorschlaegeDTO() {
            val dto = new WahlvorschlaegeDTO();

            dto.setStimmzettelgebietID("stimmzettelgebietID");
            dto.setWahlbezirkID("wahlbezirkID");
            dto.setWahlID("wahlID");

            val wahlvorschlag1 = new WahlvorschlagDTO();
            wahlvorschlag1.setIdentifikator("wahlvorschlagID");
            wahlvorschlag1.setKurzname("kurzname");
            wahlvorschlag1.setOrdnungszahl(1L);
            wahlvorschlag1.setErhaeltStimmen(true);

            val kandidat1 = new KandidatDTO();
            kandidat1.setName("kandidat");
            kandidat1.setListenposition(1L);
            kandidat1.setEinzelbewerber(true);
            kandidat1.setDirektkandidat(true);
            kandidat1.setIdentifikator("kandidatID");
            wahlvorschlag1.setKandidaten(Set.of(kandidat1));
            dto.setWahlvorschlaege(Set.of(wahlvorschlag1));

            return dto;
        }
    }
}
