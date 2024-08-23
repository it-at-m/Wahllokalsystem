package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahltermindaten;

import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_TEST_PROFILE;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.ReferendumvorlagenClientMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.KandidatRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahlvorschlaegeRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahlvorschlagRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.ReferendumvorlageRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.ReferendumvorlagenRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.KandidatDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.ReferendumoptionDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.ReferendumvorlageDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlvorschlagDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.referendumvorlagen.ReferendumvorlagenDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.referendumvorlagen.ReferendumvorlagenDTOMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlagen.ReferendumvorlagenModelMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlagen.ReferendumvorlagenReferenceModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlagen.ReferendumvorlagenValidator;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import jakarta.transaction.Transactional;
import java.util.Set;
import javax.annotation.Nullable;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class)
@AutoConfigureMockMvc
@AutoConfigureWireMock
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
public class WahltermindatenControllerIntegrationTest {

    public static final String BUSINESS_ACTIONS_REFERENDUMVORLAGEN = "/businessActions/referendumvorlagen/";

    @Value("${service.info.oid}")
    String serviceOid;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WahlvorschlaegeRepository wahlvorschlaegeRepository;
    @Autowired
    WahlvorschlagRepository wahlvorschlagRepository;
    @Autowired
    KandidatRepository kandidatRepository;

    @Autowired
    ReferendumvorlagenRepository referendumvorlagenRepository;
    @Autowired
    ReferendumvorlageRepository referendumvorlageRepository;

    @Autowired
    ReferendumvorlagenClientMapper referendumvorlagenClientMapper;

    @Autowired
    ReferendumvorlagenModelMapper referendumvorlagenModelMapper;

    @Autowired
    ReferendumvorlagenDTOMapper referendumvorlagenDTOMapper;

    @SpyBean
    ReferendumvorlagenValidator referendumvorlagenValidator;

    @AfterEach
    void tearDown() {
        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_REFERENDUMVORLAGEN, Authorities.REPOSITORY_DELETE_WAHLVORSCHLAEGE);
        wahlvorschlaegeRepository.deleteAll();
        referendumvorlagenRepository.deleteAll();
    }

    @Nested
    class DeleteWahltermindaten {

        @Test
        @Transactional
        void deletesReferendumvorlagenAndAllChildren_onlyForGivenWahlID() throws Exception {
            val wahlID_1 = "wahlID_1";
            val wahlbezirkID = "wahlbezirkID";
            val wahlID_2 = "wahlID_2";

            val eaiReferendumvorschlage1 = createClientReferendumvorlagenDTO("szgID1");
            defineStubForGetReferendumvorlage(eaiReferendumvorschlage1, wahlID_1, wahlbezirkID, HttpStatus.OK);
            val eaiReferendumvorschlage2 = createClientReferendumvorlagenDTO("szgID2");
            defineStubForGetReferendumvorlage(eaiReferendumvorschlage2, wahlID_2, wahlbezirkID, HttpStatus.OK);

            val request_1 = MockMvcRequestBuilders.get(BUSINESS_ACTIONS_REFERENDUMVORLAGEN + wahlID_1 + "/" + wahlbezirkID);
            mockMvc.perform(request_1).andExpect(status().isOk());
            val request_2 = MockMvcRequestBuilders.get(BUSINESS_ACTIONS_REFERENDUMVORLAGEN + wahlID_2 + "/" + wahlbezirkID);
            mockMvc.perform(request_2).andExpect(status().isOk());

            val referendumvorlagenEntity_1 = referendumvorlagenRepository.findByBezirkUndWahlID(new BezirkUndWahlID(wahlID_1, wahlbezirkID)).get();
            val savedSubEntities_1_inRepo = referendumvorlagenEntity_1.getReferendumvorlagen();
            Assertions.assertThat(savedSubEntities_1_inRepo.size()).isEqualTo(eaiReferendumvorschlage1.getReferendumvorlagen().size());

            val referendumvorlagenEntity_2 = referendumvorlagenRepository.findByBezirkUndWahlID(new BezirkUndWahlID(wahlID_2, wahlbezirkID)).get();
            val savedSubEntities_2_inRepo = referendumvorlagenEntity_2.getReferendumvorlagen();
            Assertions.assertThat(savedSubEntities_2_inRepo.size()).isEqualTo(eaiReferendumvorschlage2.getReferendumvorlagen().size());

            Assertions.assertThat(referendumvorlageRepository.findAll().size()).isEqualTo(savedSubEntities_2_inRepo.size() + savedSubEntities_2_inRepo.size());

            referendumvorlagenRepository.deleteAllByBezirkUndWahlID_WahlID(wahlID_1);

            val foundSubEntities_afterDelete_InRepo = referendumvorlageRepository.findAll();

            Assertions.assertThat(foundSubEntities_afterDelete_InRepo).containsExactlyInAnyOrderElementsOf(savedSubEntities_2_inRepo);
        }


        private de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.ReferendumvorlagenDTO createClientReferendumvorlagenDTO(final String stimmzettelgebietID) {
            val dto = new de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.ReferendumvorlagenDTO();

            dto.setStimmzettelgebietID(stimmzettelgebietID);

            val referendumOption1_1 = new ReferendumoptionDTO();
            referendumOption1_1.setId("optionID1_1" + stimmzettelgebietID);
            referendumOption1_1.setName("optionName1_1");
            referendumOption1_1.setPosition(1L);

            val referendumOption1_2 = new ReferendumoptionDTO();
            referendumOption1_2.setId("optionID1_2" + stimmzettelgebietID);
            referendumOption1_2.setName("optionName1_2");
            referendumOption1_2.setPosition(1L);

            val vorlage1 = new ReferendumvorlageDTO();
            vorlage1.setFrage("frage");
            vorlage1.setKurzname("kurzname_1");
            vorlage1.setOrdnungszahl(1L);
            vorlage1.setWahlvorschlagID("wahlvorschlagID");
            vorlage1.setReferendumoptionen(Set.of(referendumOption1_1, referendumOption1_2));

            val referendumOption2_1 = new ReferendumoptionDTO();
            referendumOption2_1.setId("optionID2_1" + stimmzettelgebietID);
            referendumOption2_1.setName("optionName2_1");
            referendumOption2_1.setPosition(1L);

            val referendumOption2_2 = new ReferendumoptionDTO();
            referendumOption2_2.setId("optionID2_2" + stimmzettelgebietID);
            referendumOption2_2.setName("optionName2_2");
            referendumOption2_2.setPosition(1L);

            val vorlage2 = new ReferendumvorlageDTO();
            vorlage2.setFrage("frage");
            vorlage2.setKurzname("kurzname_2");
            vorlage2.setOrdnungszahl(1L);
            vorlage2.setWahlvorschlagID("wahlvorschlagID");
            vorlage2.setReferendumoptionen(Set.of(referendumOption2_1, referendumOption2_2));

            dto.setReferendumvorlagen(Set.of(vorlage1, vorlage2));

            return dto;
        }

        private void defineStubForGetReferendumvorlage(
                @Nullable final Object wiremockPayload, final String wahlID,
                final String wahlbezirkID,
                final HttpStatus httpStatus) throws Exception {
            val wireMockResponse = WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(
                    httpStatus.value());

            if (wireMockResponse != null) {
                wireMockResponse.withBody(objectMapper.writeValueAsBytes(wiremockPayload));
            }

            WireMock.stubFor(WireMock.get("/vorschlaege/referendum/" + wahlID + "/" + wahlbezirkID)
                    .willReturn(wireMockResponse));
        }
    }

    private de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlvorschlaegeDTO createClientWahlvorschlaegeDTO(final String wahlID,
            final String wahlbezirkID) {
        val stimmzettelgebietID = "stimmzettelgebietID";

        val clientWahlvorschlaegeDTO = new de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlvorschlaegeDTO();
        clientWahlvorschlaegeDTO.setStimmzettelgebietID(stimmzettelgebietID);
        clientWahlvorschlaegeDTO.setWahlID(wahlID);
        clientWahlvorschlaegeDTO.setWahlbezirkID(wahlbezirkID);

        val wahlvorschlag1 = new de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlvorschlagDTO();
        wahlvorschlag1.setErhaeltStimmen(true);
        wahlvorschlag1.setIdentifikator("identifikator1");
        wahlvorschlag1.setKurzname("kurzname1");
        wahlvorschlag1.setOrdnungszahl(1L);

        val kandidat11 = new de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.KandidatDTO();
        kandidat11.setIdentifikator("kandidat11");
        kandidat11.setDirektkandidat(true);
        kandidat11.setEinzelbewerber(true);
        kandidat11.setName("name11");
        kandidat11.setListenposition(1L);
        kandidat11.setTabellenSpalteInNiederschrift(1L);

        val kandidat12 = new de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.KandidatDTO();
        kandidat12.setIdentifikator("kandidat12");
        kandidat12.setDirektkandidat(false);
        kandidat12.setEinzelbewerber(false);
        kandidat12.setName("name12");
        kandidat12.setListenposition(2L);
        kandidat12.setTabellenSpalteInNiederschrift(2L);
        wahlvorschlag1.setKandidaten(Set.of(kandidat11, kandidat12));

        val wahlvorschlag2 = new WahlvorschlagDTO();
        wahlvorschlag2.setErhaeltStimmen(false);
        wahlvorschlag2.setIdentifikator("identifikator2");
        wahlvorschlag2.setKurzname("kurzname2");
        wahlvorschlag2.setOrdnungszahl(2L);

        val kandidat21 = new de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.KandidatDTO();
        kandidat21.setIdentifikator("kandidat21");
        kandidat21.setDirektkandidat(true);
        kandidat21.setEinzelbewerber(true);
        kandidat21.setName("name21");
        kandidat21.setListenposition(3L);
        kandidat21.setTabellenSpalteInNiederschrift(3L);

        val kandidat22 = new KandidatDTO();
        kandidat22.setIdentifikator("kandidat22");
        kandidat22.setDirektkandidat(false);
        kandidat22.setEinzelbewerber(false);
        kandidat22.setName("name22");
        kandidat22.setListenposition(4L);
        kandidat22.setTabellenSpalteInNiederschrift(4L);
        wahlvorschlag2.setKandidaten(Set.of(kandidat21, kandidat22));

        val wahlvorschlaege = Set.of(wahlvorschlag1, wahlvorschlag2);
        clientWahlvorschlaegeDTO.setWahlvorschlaege(wahlvorschlaege);

        return clientWahlvorschlaegeDTO;
    }
}
