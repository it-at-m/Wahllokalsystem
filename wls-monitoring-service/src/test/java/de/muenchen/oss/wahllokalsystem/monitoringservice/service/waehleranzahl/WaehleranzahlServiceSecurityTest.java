package de.muenchen.oss.wahllokalsystem.monitoringservice.service.waehleranzahl;

import static org.springframework.security.core.context.SecurityContextHolder.clearContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.monitoringservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.monitoringservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.monitoringservice.domain.waehleranzahl.Waehleranzahl;
import de.muenchen.oss.wahllokalsystem.monitoringservice.domain.waehleranzahl.WaehleranzahlRepository;
import de.muenchen.oss.wahllokalsystem.monitoringservice.rest.waehleranzahl.WaehleranzahlDTOMapper;
import de.muenchen.oss.wahllokalsystem.monitoringservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles({ TestConstants.SPRING_TEST_PROFILE })
@AutoConfigureWireMock
public class WaehleranzahlServiceSecurityTest {

    @Autowired
    WaehleranzahlService waehleranzahlService;

    @Autowired
    WaehleranzahlRepository waehleranzahlRepository;

    @Autowired
    WaehleranzahlDTOMapper waehleranzahlDTOMapper;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    WaehleranzahlValidator waehleranzahlValidator;

    @BeforeEach
    void setup() {
        clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_WAEHLERANZAHL);
        waehleranzahlRepository.deleteAll();
    }

    @Nested
    class GetWahlbeteiligung {

        @Test
        void should_grantAccessAndThrowNoException_when_AuthoritiesAreValid() {
            BezirkUndWahlID bezirkUndWahlID = new BezirkUndWahlID("wahlID01", "wahlbezirkID01");
            SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_WAEHLERANZAHL);
            waehleranzahlRepository.save(new Waehleranzahl(bezirkUndWahlID, 99, LocalDateTime.now()));

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_WAEHLERANZAHL);

            Assertions.assertThatNoException().isThrownBy(() -> waehleranzahlService.getWahlbeteiligung(bezirkUndWahlID));
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariations")
        void should_FailWithAccessDeniedException_when_AnyAuthorityIsMissing(final ArgumentsAccessor argumentsAccessor) {
            SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));
            BezirkUndWahlID bezirkUndWahlID = new BezirkUndWahlID("wahlID01", "wahlbezirkID01");
            Assertions.assertThatThrownBy(() -> waehleranzahlService.getWahlbeteiligung(bezirkUndWahlID)).isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariations() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_GET_WAEHLERANZAHL);
        }
    }

    @Nested
    class PostWahlbeteiligung {

        @Test
        void should_grantAccessAndThrowNoException_when_AuthoritiesAreValid() throws Exception {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_SET_WAEHLERANZAHL);
            String wahlID = "wahlID01";
            String wahlbezirkID = "wahlbezirkID01";
            BezirkUndWahlID bezirkUndWahlID = new BezirkUndWahlID(wahlID, wahlbezirkID);
            val waehleranzahlToSave = new WaehleranzahlModel(bezirkUndWahlID, 99L, LocalDateTime.now());

            val waehleranzahlDTO = waehleranzahlDTOMapper.toDTO(waehleranzahlToSave);
            WireMock.stubFor(WireMock.post("/wahlbeteiligung")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(waehleranzahlDTO))));

            Assertions.assertThatNoException().isThrownBy(() -> waehleranzahlService.postWahlbeteiligung(waehleranzahlToSave));
        }

        @Test
        void should_FailWithAccessDeniedException_when_ServiceAuthorityIsMissing() throws Exception {
            SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_WAEHLERANZAHL);

            String wahlID = "wahlID01";
            String wahlbezirkID = "wahlbezirkID01";
            BezirkUndWahlID bezirkUndWahlID = new BezirkUndWahlID(wahlID, wahlbezirkID);
            val waehleranzahlToSave = new WaehleranzahlModel(bezirkUndWahlID, 99L, LocalDateTime.now());

            val waehleranzahlDTO = waehleranzahlDTOMapper.toDTO(waehleranzahlToSave);
            WireMock.stubFor(WireMock.post("/wahlbeteiligung")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(waehleranzahlDTO))));

            Assertions.assertThatThrownBy(() -> waehleranzahlService.postWahlbeteiligung(waehleranzahlToSave)).isInstanceOf(AccessDeniedException.class);
        }

        @Test
        void should_FailWithTechnischeWlsException_when_RepoAuthorityIsMissing() throws Exception {
            SecurityUtils.runWith(Authorities.SERVICE_POST_WAEHLERANZAHL);

            String wahlID = "wahlID01";
            String wahlbezirkID = "wahlbezirkID01";
            BezirkUndWahlID bezirkUndWahlID = new BezirkUndWahlID(wahlID, wahlbezirkID);
            val waehleranzahlToSave = new WaehleranzahlModel(bezirkUndWahlID, 99L, LocalDateTime.now());

            val waehleranzahlDTO = waehleranzahlDTOMapper.toDTO(waehleranzahlToSave);
            WireMock.stubFor(WireMock.post("/wahlbeteiligung")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(waehleranzahlDTO))));

            Assertions.assertThatThrownBy(() -> waehleranzahlService.postWahlbeteiligung(waehleranzahlToSave)).isInstanceOf(TechnischeWlsException.class);
        }

    }

}
