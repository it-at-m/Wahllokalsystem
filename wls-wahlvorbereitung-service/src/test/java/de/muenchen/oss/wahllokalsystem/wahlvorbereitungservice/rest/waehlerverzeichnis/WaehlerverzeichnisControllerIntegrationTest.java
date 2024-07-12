package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.waehlerverzeichnis;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.Waehlerverzeichnis;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.WaehlerverzeichnisRepository;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.waehlerverzeichnis.WaehlerverzeichnisModelMapper;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.waehlerverzeichnis.WaehlerverzeichnisValidator;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils.SecurityUtils;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.Profiles;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = MicroServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles({ TestConstants.SPRING_TEST_PROFILE, Profiles.NO_BEZIRKS_ID_CHECK })
public class WaehlerverzeichnisControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WaehlerverzeichnisRepository waehlerverzeichnisRepository;

    @Autowired
    WaehlerverzeichnisDTOMapper waehlerverzeichnisDTOMapper;

    @Autowired
    WaehlerverzeichnisModelMapper waehlerverzeichnisModelMapper;

    @Autowired
    ExceptionFactory exceptionFactory;

    @SpyBean
    WaehlerverzeichnisValidator waehlerverzeichnisValidator;

    @AfterEach
    void tearDown() {
        SecurityUtils.runAs(Authorities.REPOSITORY_DELETE_WAEHLERVERZEICHNIS);
        waehlerverzeichnisRepository.deleteAll();
    }

    @Nested
    class PostWaehlerverzeichnis {

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_POST_WAEHLERVERZEICHNIS, Authorities.REPOSITORY_WRITE_WAEHLERVERZEICHNIS })
        void newValidDataIsSaved() throws Exception {
            val wahlbezirkID = "wahlbezirkID";
            val waehlerverzeichnisNummer = 89L;
            val requestBody = new WaehlerverzeichnisWriteDTO(true, false, true, false);

            val request = post(buildURL(wahlbezirkID, waehlerverzeichnisNummer)).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(requestBody));

            mockMvc.perform(request).andExpect(status().isCreated());

            SecurityUtils.runAs(Authorities.REPOSITORY_READ_WAEHLERVERZEICHNIS);
            val savedWaehlerverzeichnis = waehlerverzeichnisRepository.findById(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer))
                    .get();
            val expectedWaehlerverzeichnis = new Waehlerverzeichnis(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer),
                    requestBody.verzeichnisLagVor(), requestBody.berichtigungVorBeginnDerAbstimmung(), requestBody.nachtraeglicheBerichtigung(),
                    requestBody.mitteilungUeberUngueltigeWahlscheineErhalten());
            Assertions.assertThat(savedWaehlerverzeichnis).isEqualTo(expectedWaehlerverzeichnis);
        }

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_POST_WAEHLERVERZEICHNIS, Authorities.REPOSITORY_WRITE_WAEHLERVERZEICHNIS })
        void existingDataIsReplaced() throws Exception {
            val wahlbezirkID = "wahlbezirkID";
            val waehlerverzeichnisNummer = 89L;
            val requestBody = new WaehlerverzeichnisWriteDTO(true, false, true, false);

            waehlerverzeichnisRepository.save(
                    new Waehlerverzeichnis(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer), false, false, false,
                            false));

            val request = post(buildURL(wahlbezirkID, waehlerverzeichnisNummer)).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(requestBody));

            mockMvc.perform(request).andExpect(status().isCreated());

            SecurityUtils.runAs(Authorities.REPOSITORY_READ_WAEHLERVERZEICHNIS);
            val savedWaehlerverzeichnis = waehlerverzeichnisRepository.findById(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer))
                    .get();
            val expectedWaehlerverzeichnis = new Waehlerverzeichnis(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer),
                    requestBody.verzeichnisLagVor(), requestBody.berichtigungVorBeginnDerAbstimmung(), requestBody.nachtraeglicheBerichtigung(),
                    requestBody.mitteilungUeberUngueltigeWahlscheineErhalten());
            Assertions.assertThat(savedWaehlerverzeichnis).isEqualTo(expectedWaehlerverzeichnis);
            Assertions.assertThat(waehlerverzeichnisRepository.count()).isEqualTo(1);
        }

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_POST_WAEHLERVERZEICHNIS, Authorities.REPOSITORY_WRITE_WAEHLERVERZEICHNIS })
        void exceptionOnValidation() throws Exception {
            val wahlbezirkID = "wahlbezirkID";
            val waehlerverzeichnisNummer = 89L;
            val requestBody = new WaehlerverzeichnisWriteDTO(true, false, true, false);

            val request = post(buildURL(wahlbezirkID, waehlerverzeichnisNummer)).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(requestBody));

            val mockedValidationException = exceptionFactory.createFachlicheWlsException(ExceptionConstants.PARAMS_UNVOLLSTAENDIG);
            Mockito.doThrow(mockedValidationException).when(waehlerverzeichnisValidator).validModelToSetOrThrow(any());

            val result = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn();
            val wlsExceptionFromBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), WlsExceptionDTO.class);

            Assertions.assertThat(wlsExceptionFromBody)
                    .isEqualTo(new WlsExceptionDTO(WlsExceptionCategory.F, mockedValidationException.getCode(),
                            mockedValidationException.getServiceName(), mockedValidationException.getMessage()));
        }
    }

    @Nested
    class GetWaehlerverzeichnis {

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_GET_WAEHLERVERZEICHNIS, Authorities.REPOSITORY_READ_WAEHLERVERZEICHNIS })
        void dataFound() throws Exception {
            val wahlbezirkIDToFind = "wahlbezirkIDToFind";
            val waehlerverzeichnisNummerToFind = 23L;

            val waehlerverzeichnis1 = new Waehlerverzeichnis(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkIDToFind, waehlerverzeichnisNummerToFind + 1),
                    true, true, true, true);
            val waehlerverzeichnis2 = new Waehlerverzeichnis(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkIDToFind + "!", waehlerverzeichnisNummerToFind),
                    true, true, true, false);
            val waehlerverzeichnis3 = new Waehlerverzeichnis(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkIDToFind, waehlerverzeichnisNummerToFind), true,
                    false, true, false);
            val waehlerverzeichnis4 = new Waehlerverzeichnis(
                    new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkIDToFind + "!", waehlerverzeichnisNummerToFind + 1), false, false, true, true);
            waehlerverzeichnisRepository.saveAll(List.of(waehlerverzeichnis1, waehlerverzeichnis2, waehlerverzeichnis3, waehlerverzeichnis4));

            val request = get(buildURL(wahlbezirkIDToFind, waehlerverzeichnisNummerToFind));

            val result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
            val resultBodyAsDTO = objectMapper.readValue(result.getResponse().getContentAsString(), WaehlerverzeichnisDTO.class);

            val expectedResultBody = waehlerverzeichnisDTOMapper.toDto(waehlerverzeichnisModelMapper.toModel(waehlerverzeichnis3));
            Assertions.assertThat(resultBodyAsDTO).isEqualTo(expectedResultBody);
        }

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_GET_WAEHLERVERZEICHNIS, Authorities.REPOSITORY_READ_WAEHLERVERZEICHNIS })
        void noDataFound() throws Exception {
            val wahlbezirkIDToFind = "wahlbezirkIDToFind";
            val waehlerverzeichnisNummerToFind = 23L;

            val waehlerverzeichnis1 = new Waehlerverzeichnis(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkIDToFind, waehlerverzeichnisNummerToFind + 1),
                    true, true, true, true);
            val waehlerverzeichnis2 = new Waehlerverzeichnis(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkIDToFind + "!", waehlerverzeichnisNummerToFind),
                    true, true, true, false);
            val waehlerverzeichnis3 = new Waehlerverzeichnis(
                    new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkIDToFind + "!", waehlerverzeichnisNummerToFind + 1), false, false, true, true);
            waehlerverzeichnisRepository.saveAll(List.of(waehlerverzeichnis1, waehlerverzeichnis2, waehlerverzeichnis3));

            val request = get(buildURL(wahlbezirkIDToFind, waehlerverzeichnisNummerToFind));

            val result = mockMvc.perform(request).andExpect(status().isNoContent()).andReturn();

            Assertions.assertThat(result.getResponse().getContentAsString()).isEmpty();
        }
    }

    private String buildURL(final String wahlbezirkID, final long waehlerverzeichnisnummer) {
        return "/businessActions/waehlerverzeichnis/" + wahlbezirkID + "/" + waehlerverzeichnisnummer;
    }
}
