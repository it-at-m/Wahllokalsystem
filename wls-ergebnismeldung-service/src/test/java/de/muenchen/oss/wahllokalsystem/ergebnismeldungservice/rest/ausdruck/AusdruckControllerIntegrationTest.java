package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ausdruck;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;
import static de.muenchen.oss.wahllokalsystem.wls.common.security.Profiles.NO_BEZIRKS_ID_CHECK;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.Ausdruck;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.AusdruckRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.Meldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.WahlUndBezirkIDUndMeldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.AusdruckModelMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.TimePrecisionComparators;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles(
        profiles = { SPRING_TEST_PROFILE, NO_BEZIRKS_ID_CHECK, SPRING_NO_SECURITY_PROFILE }
)
public class AusdruckControllerIntegrationTest {

    @Autowired
    AusdruckRepository ausdruckRepository;

    @Autowired
    AusdruckModelMapper ausdruckModelMapper;

    @Autowired
    AusdruckReadDTOMapper ausdruckReadDTOMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @AfterEach
    void tearDown() {
        ausdruckRepository.deleteAll();
    }

    @Nested
    class GetAusdruck {

        @Test
        void should_returnData_when_dataIsPresentInRepository() throws Exception {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val meldungsart = Meldungsart.V1;
            val content = "Testausdruck";
            val erstelltAm = Instant.now();

            val request = MockMvcRequestBuilders.get(buildGetPostAusdruckURI(wahlID, wahlbezirkID, meldungsart));

            val entityToFind = new Ausdruck(new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, wahlID, meldungsart), content, erstelltAm);
            ausdruckRepository.save(entityToFind);

            val response = mockMvc.perform(request).andExpect(status().isOk()).andReturn().getResponse();
            val responseBodyAsByteArray = response.getContentAsString();

            Assertions.assertThat(response.getHeader("Content-Type")).isEqualTo("text/html; charset=utf-8");
            Assertions.assertThat(responseBodyAsByteArray).isEqualTo(content);
        }

        @Test
        void should_returnNotFoundRequest_when_requestIsInvalid() throws Exception {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val meldungsart = Meldungsart.V1;

            val request = MockMvcRequestBuilders.get(buildGetPostAusdruckURI(wahlID, wahlbezirkID, meldungsart));

            mockMvc.perform(request).andExpect(status().isNotFound());
        }
    }

    @Nested
    class PostAusdruck {

        @Test
        void should_persistData_when_dataIsSent() throws Exception {
            var clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
            var mockedInstant = Instant.now(clock);
            MockedStatic<Instant> mockedStatic = mockStatic(Instant.class, Mockito.CALLS_REAL_METHODS);
            mockedStatic.when(Instant::now).thenReturn(mockedInstant);

            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val meldungsart = Meldungsart.V1;
            val content = "Testausdruck";
            val erstelltAm = Instant.now();
            val idToFind = new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, wahlID, meldungsart);

            val entityToFind = new Ausdruck(idToFind, content, erstelltAm);

            val requestBody = new AusdruckWriteDTO(content);
            val request = MockMvcRequestBuilders.post(buildGetPostAusdruckURI(wahlID, wahlbezirkID, meldungsart)).contentType(MediaType.APPLICATION_JSON)
                    .content(
                            objectMapper.writeValueAsString(requestBody));

            mockMvc.perform(request).andExpect(status().isOk());

            val persistedEntity = ausdruckRepository.findOneByWahlUndBezirkIDUndMeldungsart(idToFind);

            Assertions.assertThat(persistedEntity)
                    .usingRecursiveComparison()
                    .withComparatorForType(TimePrecisionComparators.INSTANT_PRECISION_MILLISECONDS, Instant.class)
                    .isEqualTo(entityToFind);
        }

        @Test
        void should_returnBadRequest_when_requestBodyIsMissing() throws Exception {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val meldungsart = Meldungsart.V1;
            val request = MockMvcRequestBuilders.post(buildGetPostAusdruckURI(wahlID, wahlbezirkID, meldungsart));

            mockMvc.perform(request).andExpect(status().isBadRequest());

            Assertions.assertThat(ausdruckRepository.count()).isEqualTo(0);
        }

        @Test
        void should_returnInternalServerError_when_requestPathParameterWahlIDIsInvalid() throws Exception {
            val wahlbezirkID = "wahlbezirkID";
            val meldungsart = Meldungsart.V1;
            val content = "Testausdruck";

            val requestBody = new AusdruckWriteDTO(content);
            val request = MockMvcRequestBuilders.post(buildGetPostAusdruckURI(" ", wahlbezirkID, meldungsart)).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(requestBody));

            mockMvc.perform(request).andExpect(status().isInternalServerError());

            Assertions.assertThat(ausdruckRepository.count()).isEqualTo(0);
        }

        @Test
        void should_returnInternalServerError_when_requestPathParameterWahlbezirkIDIsInvalid() throws Exception {
            val wahlID = "wahlID";
            val meldungsart = Meldungsart.V1;
            val content = "Testausdruck";

            val requestBody = new AusdruckWriteDTO(content);
            val request = MockMvcRequestBuilders.post(buildGetPostAusdruckURI(wahlID, " ", meldungsart)).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(requestBody));

            mockMvc.perform(request).andExpect(status().isInternalServerError());

            Assertions.assertThat(ausdruckRepository.count()).isEqualTo(0);
        }

        @Test
        void should_returnInternalServerError_when_requestPathParameterWMeldungsartIDIsInvalid() throws Exception {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val content = "Testausdruck";

            val requestBody = new AusdruckWriteDTO(content);
            val request = MockMvcRequestBuilders.post(buildGetPostAusdruckURI(wahlID, wahlbezirkID, null)).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(requestBody));

            mockMvc.perform(request).andExpect(status().isInternalServerError());

            Assertions.assertThat(ausdruckRepository.count()).isEqualTo(0);
        }
    }

    @Nested
    class GetAllAusdrucke {
        @Test
        void should_returnData_when_dataIsPresentInRepository() throws Exception {

            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val content = "Testausdruck";
            val erstelltAm = Instant.now();

            val request = MockMvcRequestBuilders.get(buildGetAllAusdruckeURI(wahlID, wahlbezirkID));

            val entityToFind1 = new Ausdruck(new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, wahlID, Meldungsart.V1), content, erstelltAm);
            val entityToFind2 = new Ausdruck(new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, wahlID, Meldungsart.V3), content, erstelltAm);
            ausdruckRepository.save(entityToFind1);
            ausdruckRepository.save(entityToFind2);

            val response = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsString(),
                    AusdruckReadDTO[].class);

            val expectedResponseBody = new AusdruckReadDTO[] {
                    ausdruckReadDTOMapper.toDTO(ausdruckModelMapper.toModel(entityToFind1)),
                    ausdruckReadDTOMapper.toDTO(ausdruckModelMapper.toModel(entityToFind2))
            };

            Assertions.assertThat(responseBodyAsDTO)
                    .usingRecursiveComparison()
                    .withComparatorForType(TimePrecisionComparators.INSTANT_PRECISION_MILLISECONDS, Instant.class)
                    .isEqualTo(expectedResponseBody);
        }

        @Test
        void should_returnOKButNoData_when_noDataFound() throws Exception {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            val request = MockMvcRequestBuilders.get(buildGetAllAusdruckeURI(wahlID, wahlbezirkID));

            val response = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsString(),
                    AusdruckReadDTO[].class);

            Assertions.assertThat(responseBodyAsDTO.length).isEqualTo(0);
        }
    }

    private String buildGetPostAusdruckURI(final String wahlID, final String wahlbezirkID, final Meldungsart meldungsart) {
        return "/businessActions/ausdruck/" + wahlID + "/" + wahlbezirkID + "/" + meldungsart + "/html";
    }

    private String buildGetAllAusdruckeURI(final String wahlID, final String wahlbezirkID) {
        return "/businessActions/ausdruck/" + wahlID + "/" + wahlbezirkID;
    }
}
