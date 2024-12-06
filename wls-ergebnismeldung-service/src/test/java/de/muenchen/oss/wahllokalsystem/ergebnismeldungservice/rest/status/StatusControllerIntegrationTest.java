package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.status;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.status.Meldung;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.status.Status;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.status.StatusRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.status.Validierungsstatus;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.StatusModelMapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class)
@AutoConfigureMockMvc
@AutoConfigureWireMock
@ActiveProfiles(
        profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE,
                de.muenchen.oss.wahllokalsystem.wls.common.security.Profiles.NO_BEZIRKS_ID_CHECK }
)
public class StatusControllerIntegrationTest {

    @Autowired
    StatusRepository statusRepository;

    @Autowired
    StatusModelMapper statusModelMapper;

    @Autowired
    StatusDTOMapper statusDTOMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        statusRepository.deleteAll();
    }

    @Nested
    class GetStatus {

        @Test
        void should_returnData_when_dataIsPresentInRepository() throws Exception {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val request = MockMvcRequestBuilders.get(buildStatusURI(wahlID, wahlbezirkID));

            val schnellMeldung = createMeldung();
            val niederschrift = createMeldung();
            val entityToFind = new Status(new BezirkUndWahlID(wahlID, wahlbezirkID), schnellMeldung, niederschrift);
            statusRepository.save(entityToFind);

            val response = mockMvc.perform(request).andExpect(status().isOk()).andReturn().getResponse();
            val responseBodyAsDTO = objectMapper.readValue(response.getContentAsString(), StatusDTO.class);

            val expectedResult = statusDTOMapper.toDTO(statusModelMapper.toModel(entityToFind));

            Assertions.assertThat(responseBodyAsDTO).usingRecursiveComparison().isEqualTo(expectedResult);
        }

        @Test
        void should_returnNoContent_when_dataIsNotPresentInRepository() throws Exception {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val request = MockMvcRequestBuilders.get(buildStatusURI(wahlID, wahlbezirkID));

            val response = mockMvc.perform(request).andExpect(status().isNoContent()).andReturn().getResponse();

            Assertions.assertThat(response.getContentAsString()).isEmpty();
        }

        @Test
        void should_returnBadRequestWlsException_when_validationFailed() throws Exception {
            val wahlID = "    ";
            val wahlbezirkID = "wahlbezirkID";
            val request = MockMvcRequestBuilders.get(buildStatusURI(wahlID, wahlbezirkID));

            val response = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn().getResponse();
            val receivedWlsException = objectMapper.readValue(response.getContentAsString(), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionConstants.GET_STATUS_PARAMETER_UNVOLLSTAENDIG.code(),
                    "WLS-ERGEBNISMELDUNG", ExceptionConstants.GET_STATUS_PARAMETER_UNVOLLSTAENDIG.message());
            Assertions.assertThat(receivedWlsException).isEqualTo(expectedWlsExceptionDTO);
        }
    }

    @Nested
    class PostStatus {

        @Test
        void should_persistData_when_noDataIsPresentInRepository() throws Exception {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val requestBody = new StatusDTO(new BezirkUndWahlID(wahlID, wahlbezirkID), createMeldungDTO(), createMeldungDTO());

            val request = MockMvcRequestBuilders.post(buildStatusURI(wahlID, wahlbezirkID)).with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody));

            WireMock.stubFor(WireMock.post(UrlPattern.ANY).willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())));

            mockMvc.perform(request).andExpect(status().isOk()).andReturn().getResponse();

            val entityFromRepo = statusRepository.findById(requestBody.bezirkUndWahlID()).get();
            val expectedEntity = statusModelMapper.toEntity(statusDTOMapper.toModel(requestBody));
            Assertions.assertThat(entityFromRepo).isEqualTo(expectedEntity);
        }

        @Test
        void should_replaceOldData_when_dataIsPresentInRepository() throws Exception {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val requestBody = new StatusDTO(new BezirkUndWahlID(wahlID, wahlbezirkID), createMeldungDTO(), createMeldungDTO());

            val request = MockMvcRequestBuilders.post(buildStatusURI(wahlID, wahlbezirkID)).with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody));

            val schnellmeldung = createMeldung();
            schnellmeldung.setGedruckt(!requestBody.schnellmeldung().gedruckt());
            val niederschrift = createMeldung();
            niederschrift.setGedruckt(!requestBody.niederschrift().gedruckt());
            val entityToReplace = new Status(requestBody.bezirkUndWahlID(), schnellmeldung, niederschrift);
            Assertions.assertThat(entityToReplace).usingRecursiveComparison().isNotEqualTo(requestBody);
            statusRepository.save(entityToReplace);

            WireMock.stubFor(WireMock.post(UrlPattern.ANY).willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())));

            mockMvc.perform(request).andExpect(status().isOk()).andReturn().getResponse();

            val entityFromRepo = statusRepository.findById(requestBody.bezirkUndWahlID()).get();
            val expectedEntity = statusModelMapper.toEntity(statusDTOMapper.toModel(requestBody));
            Assertions.assertThat(entityFromRepo).isEqualTo(expectedEntity);
        }

        @Test
        void should_returnBadRequestWlsException_when_validationFailed() throws Exception {
            val wahlID = "    ";
            val wahlbezirkID = "wahlbezirkID";
            val requestBody = new StatusDTO(new BezirkUndWahlID(wahlID, wahlbezirkID), createMeldungDTO(), createMeldungDTO());

            val request = MockMvcRequestBuilders.post(buildStatusURI(wahlID, wahlbezirkID)).with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody));

            val response = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn().getResponse();
            val receivedWlsException = objectMapper.readValue(response.getContentAsString(), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionConstants.POST_STATUS_PARAMETER_UNVOLLSTAENDIG.code(),
                    "WLS-ERGEBNISMELDUNG", ExceptionConstants.POST_STATUS_PARAMETER_UNVOLLSTAENDIG.message());
            Assertions.assertThat(receivedWlsException).isEqualTo(expectedWlsExceptionDTO);
        }

        @Test
        void should_notifyAllSenders_when_newDataIsSet() throws Exception {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val requestBody = new StatusDTO(new BezirkUndWahlID(wahlID, wahlbezirkID), createMeldungDTO(), createMeldungDTO());

            val request = MockMvcRequestBuilders.post(buildStatusURI(wahlID, wahlbezirkID)).with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody));

            WireMock.stubFor(WireMock.post("/businessActions/niederschriftDruckuhrzeit").willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())));
            WireMock.stubFor(WireMock.post("/businessActions/niederschriftSendungsuhrzeit").willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())));
            WireMock.stubFor(WireMock.post("/businessActions/schnellmeldungDruckuhrzeit").willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())));
            WireMock.stubFor(
                    WireMock.post("/businessActions/schnellmeldungSendungsuhrzeit").willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())));

            mockMvc.perform(request).andExpect(status().isOk());
        }

        private MeldungDTO createMeldungDTO() {
            return new MeldungDTO(ValidierungsstatusDTO.VALIDE, true, true, LocalDateTime.now().with(ChronoField.MILLI_OF_SECOND, 123));
        }
    }

    private Meldung createMeldung() {
        val meldung = new Meldung();

        meldung.setSendeuhrzeit(LocalDateTime.now().with(ChronoField.MILLI_OF_SECOND, 123)); //Controller only returns 3 digits
        meldung.setUebermittelt(true);
        meldung.setValidierungsstatus(Validierungsstatus.VALIDE);
        meldung.setGedruckt(true);

        return meldung;
    }

    private String buildStatusURI(final String wahlID, final String wahlbezirkID) {
        return "/businessActions/status/" + wahlID + "/" + wahlbezirkID;
    }
}
