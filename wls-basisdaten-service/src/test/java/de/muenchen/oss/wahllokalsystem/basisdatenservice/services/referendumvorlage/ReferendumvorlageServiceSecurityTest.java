package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.ReferendumvorlageRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.ReferendumvorlagenRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.ReferendumoptionDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.ReferendumvorlageDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.ReferendumvorlagenDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.util.Set;
import java.util.stream.Stream;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
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
public class ReferendumvorlageServiceSecurityTest {

    @Autowired
    ReferendumvorlageService referendumvorlageService;

    @Autowired
    ReferendumvorlagenRepository referendumvorlagenRepository;

    @Autowired
    ReferendumvorlageRepository referendumvorlageRepository;

    @Autowired
    ObjectMapper objectMapper;

    @AfterEach
    void tearDown() {
        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_REFERENDUMVORLAGEN, Authorities.REPOSITORY_DELETE_REFERENDUMVORLAGE);
        referendumvorlagenRepository.deleteAll();
        referendumvorlageRepository.deleteAll();
    }

    @Nested
    class LoadReferendumvorlagen {

        @Test
        void accessGranted() throws Exception {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_REFERENDUMVORLAGEN);

            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            val eaiReferendumvorschlage = createClientReferendumvorlagenDTO();
            WireMock.stubFor(WireMock.get("/vorschlaege/referendum/" + wahlID + "/" + wahlbezirkID)
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(
                            HttpStatus.OK.value()).withBody(objectMapper.writeValueAsBytes(eaiReferendumvorschlage))));

            Assertions.assertThatNoException()
                    .isThrownBy(() -> referendumvorlageService.loadReferendumvorlagen(new ReferendumvorlagenReferenceModel(wahlID, wahlbezirkID)));
            //we have to check is data is stores because access denied exceptions are caught too
            SecurityUtils.runWith(Authorities.REPOSITORY_READ_REFERENDUMVORLAGEN);
            Assertions.assertThat(referendumvorlagenRepository.count()).isEqualTo(1);
        }

        @Test
        void missingServiceAuthorityCausesAccessDenied() throws Exception {
            SecurityUtils.runWith(Authorities.REPOSITORY_READ_REFERENDUMVORLAGEN, Authorities.REPOSITORY_WRITE_REFERENDUMVORLAGEN,
                    Authorities.REPOSITORY_WRITE_REFERENDUMVORLAGE);

            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            Assertions.assertThatThrownBy(() -> referendumvorlageService.loadReferendumvorlagen(new ReferendumvorlagenReferenceModel(wahlID, wahlbezirkID)))
                    .isInstanceOf(
                            AccessDeniedException.class);
            //we have to check is data is stores because access denied exceptions are caught too
            SecurityUtils.runWith(Authorities.REPOSITORY_READ_REFERENDUMVORLAGEN);
            Assertions.assertThat(referendumvorlagenRepository.count()).isEqualTo(0);
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingRepositoryAuthoritiesVariations")
        void missingRepositoryAuthoritiesCausesNoFailWithAccessDenied(final ArgumentsAccessor argumentsAccessor) throws Exception {
            SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));

            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            val eaiReferendumvorschlage = createClientReferendumvorlagenDTO();
            WireMock.stubFor(WireMock.get("/vorschlaege/referendum/" + wahlID + "/" + wahlbezirkID)
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(
                            HttpStatus.OK.value()).withBody(objectMapper.writeValueAsBytes(eaiReferendumvorschlage))));

            Assertions.assertThatThrownBy(() -> referendumvorlageService.loadReferendumvorlagen(new ReferendumvorlagenReferenceModel(wahlID, wahlbezirkID)))
                    .isInstanceOf(
                            AccessDeniedException.class);
            //we have to check is data is stores because access denied exceptions are caught too
            SecurityUtils.runWith(Authorities.REPOSITORY_READ_REFERENDUMVORLAGEN);
            Assertions.assertThat(referendumvorlagenRepository.count()).isEqualTo(0);
        }

        private static Stream<Arguments> getMissingRepositoryAuthoritiesVariations() {
            val serviceAuthoritiesWithoutServiceAuthority = ArrayUtils.removeElements(Authorities.ALL_AUTHORITIES_GET_REFERENDUMVORLAGEN,
                    Authorities.SERVICE_GET_REFERENDUMVORLAGEN);
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(serviceAuthoritiesWithoutServiceAuthority);
        }

        private ReferendumvorlagenDTO createClientReferendumvorlagenDTO() {
            val dto = new ReferendumvorlagenDTO();

            dto.setStimmzettelgebietID("szgID");

            val referendumOption = new ReferendumoptionDTO();
            referendumOption.setId("optionID");
            referendumOption.setName("optionName");
            referendumOption.setPosition(1L);

            val vorlage = new ReferendumvorlageDTO();
            vorlage.setFrage("frage");
            vorlage.setKurzname("kurzname");
            vorlage.setOrdnungszahl(1L);
            vorlage.setWahlvorschlagID("wahlvorschlagID");
            vorlage.setReferendumoptionen(Set.of(referendumOption));

            dto.setReferendumvorlagen(Set.of(vorlage));

            return dto;
        }

    }

}
