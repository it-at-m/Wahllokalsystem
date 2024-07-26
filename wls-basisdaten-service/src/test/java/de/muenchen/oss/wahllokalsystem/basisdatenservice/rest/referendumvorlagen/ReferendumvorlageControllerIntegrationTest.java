package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.referendumvorlagen;

import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_TEST_PROFILE;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.ReferendumvorlagenClientMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.ReferendumvorlageRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.ReferendumvorlagenRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.ReferendumoptionDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.ReferendumvorlageDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlagen.ReferendumvorlageModelMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlagen.ReferendumvorlageValidator;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlagen.ReferendumvorlagenReferenceModel;
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
public class ReferendumvorlageControllerIntegrationTest {

    public static final String BUSINESS_ACTIONS_REFERENDUMVORLAGEN = "/businessActions/referendumvorlagen/";

    @Value("${service.info.oid}")
    String serviceOid;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ReferendumvorlagenRepository referendumvorlagenRepository;

    @SpyBean
    ReferendumvorlageRepository referendumvorlageRepository;

    @Autowired
    ReferendumvorlagenClientMapper referendumvorlagenClientMapper;

    @Autowired
    ReferendumvorlageModelMapper referendumvorlageModelMapper;

    @Autowired
    ReferendumvorlagenDTOMapper referendumvorlagenDTOMapper;

    @SpyBean
    ReferendumvorlageValidator referendumvorlageValidator;

    @AfterEach
    void tearDown() {
        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_REFERENDUMVORLAGEN);
        referendumvorlagenRepository.deleteAll();
    }

    @Nested
    class GetReferendumvorlagen {

        @Test
        void loadedFromExternal() throws Exception {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            val eaiReferendumvorschlage = createClientReferendumvorlagenDTO();
            defineStubForGetReferendumvorlage(eaiReferendumvorschlage, wahlID, wahlbezirkID, HttpStatus.OK);

            val request = MockMvcRequestBuilders.get(BUSINESS_ACTIONS_REFERENDUMVORLAGEN + wahlID + "/" + wahlbezirkID);

            val response = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsByteArray(), ReferendumvorlagenDTO.class);

            val expectedBodyDTO = referendumvorlagenDTOMapper.toDTO(referendumvorlagenClientMapper.toModel(eaiReferendumvorschlage));
            Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedBodyDTO);
        }

        @Test
        @Transactional
        void returnExistingDataWithoutImport() throws Exception {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            val eaiReferendumvorschlage = createClientReferendumvorlagenDTO();
            defineStubForGetReferendumvorlage(eaiReferendumvorschlage, wahlID, wahlbezirkID, HttpStatus.OK);

            val request = MockMvcRequestBuilders.get(BUSINESS_ACTIONS_REFERENDUMVORLAGEN + wahlID + "/" + wahlbezirkID);

            mockMvc.perform(request).andExpect(status().isOk());

            val referendumvorlagenEntity = referendumvorlagenRepository.findByBezirkUndWahlID(new BezirkUndWahlID(wahlID, wahlbezirkID)).get();

            val expectedEntity = referendumvorlageModelMapper.toEntity(referendumvorlagenClientMapper.toModel(eaiReferendumvorschlage),
                    new BezirkUndWahlID(wahlID, wahlbezirkID));
            val ignoreableFieldOfIdsAndParenEntityRefs = new String[] { "id", "referendumvorlagen.id", "referendumvorlagen.referendumvorlagen" };
            Assertions.assertThat(referendumvorlagenEntity).usingRecursiveComparison().ignoringCollectionOrder()
                    .ignoringFields(ignoreableFieldOfIdsAndParenEntityRefs)
                    .isEqualTo(expectedEntity);
        }

        @Test
        void technischeWlsExceptionWhenNoDataFoundExternal() throws Exception {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            defineStubForGetReferendumvorlage(null, wahlID, wahlbezirkID, HttpStatus.OK);

            val request = MockMvcRequestBuilders.get(BUSINESS_ACTIONS_REFERENDUMVORLAGEN + wahlID + "/" + wahlbezirkID);

            val response = mockMvc.perform(request).andExpect(status().isNoContent()).andReturn();

            Assertions.assertThat(response.getResponse().getContentAsByteArray()).isEmpty();
        }

        @Test
        void technischeWlsExceptionWhenCommunicationFailed() throws Exception {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            val eaiReferendumvorschlage = new de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WlsExceptionDTO();
            defineStubForGetReferendumvorlage(eaiReferendumvorschlage, wahlID, wahlbezirkID, HttpStatus.INSUFFICIENT_STORAGE);

            val request = MockMvcRequestBuilders.get(BUSINESS_ACTIONS_REFERENDUMVORLAGEN + wahlID + "/" + wahlbezirkID);

            val response = mockMvc.perform(request).andExpect(status().isInternalServerError()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsByteArray(), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.T, ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI.code(), serviceOid,
                    ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI.message());
            Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedWlsExceptionDTO);
        }

        @Test
        void fachlicheWlsExceptionWhenPathVariableIsInvalid() throws Exception {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            val mockedWlsExceptionCode = "123";
            val mockedWlsExceptionMessage = "faked validation exception";
            val mockedWlsExceptionService = "mockedServiceID";
            val mockedValidationException = FachlicheWlsException.withCode(mockedWlsExceptionCode).inService(mockedWlsExceptionService)
                    .buildWithMessage(mockedWlsExceptionMessage);
            Mockito.doThrow(mockedValidationException).when(referendumvorlageValidator)
                    .validReferumvorlageReferenceModelOrThrow(new ReferendumvorlagenReferenceModel(wahlID, wahlbezirkID));

            val request = MockMvcRequestBuilders.get(BUSINESS_ACTIONS_REFERENDUMVORLAGEN + wahlID + "/" + wahlbezirkID);

            val response = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsByteArray(), WlsExceptionDTO.class);

            val expectedBodyDTO = new WlsExceptionDTO(WlsExceptionCategory.F, mockedWlsExceptionCode, mockedWlsExceptionService, mockedWlsExceptionMessage);
            Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedBodyDTO);
        }

        @Test
        void alleReferendumdataIsStoredTransactional() throws Exception {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            val eaiReferendumvorschlage = createClientReferendumvorlagenDTO();
            defineStubForGetReferendumvorlage(eaiReferendumvorschlage, wahlID, wahlbezirkID, HttpStatus.OK);

            val mockedSaveAllException = new RuntimeException("save all failed");
            Mockito.doThrow(mockedSaveAllException).when(referendumvorlageRepository).saveAll(any());

            val request = MockMvcRequestBuilders.get(BUSINESS_ACTIONS_REFERENDUMVORLAGEN + wahlID + "/" + wahlbezirkID);

            val response = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsByteArray(), ReferendumvorlagenDTO.class);

            val expectedBodyDTO = referendumvorlagenDTOMapper.toDTO(referendumvorlagenClientMapper.toModel(eaiReferendumvorschlage));
            Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedBodyDTO);

            Assertions.assertThat(referendumvorlagenRepository.count()).isEqualTo(0);
            Assertions.assertThat(referendumvorlageRepository.count()).isEqualTo(0);
        }

        private de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.ReferendumvorlagenDTO createClientReferendumvorlagenDTO() {
            val dto = new de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.ReferendumvorlagenDTO();

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
}
