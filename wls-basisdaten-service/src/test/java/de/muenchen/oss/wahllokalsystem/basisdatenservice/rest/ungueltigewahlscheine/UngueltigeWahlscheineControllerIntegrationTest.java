package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.ungueltigewahlscheine;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.ungueltigewahlscheine.UngueltigeWahlscheine;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.ungueltigewahlscheine.UngueltigeWahlscheineRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.common.WahlbezirkArt;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.common.WahltagIdUndWahlbezirksart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionKonstanten;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class)
@AutoConfigureMockMvc
public class UngueltigeWahlscheineControllerIntegrationTest {

    @Value("${service.info.oid}")
    String serviceOid;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UngueltigeWahlscheineRepository ungueltigeWahlscheineRepository;

    @AfterEach
    void tearDown() {
        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_UNGUELTIGEWAHLSCHEINE);
        ungueltigeWahlscheineRepository.deleteAll();
    }

    @Nested
    class GetUngueltigeWahlscheine {

        @Test
        void dataFound() throws Exception {
            val ungueltigeWahlscheineData = "csv-data".getBytes();

            SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_UNGUELTIGEWAHLSCHEINE);
            ungueltigeWahlscheineRepository.save(
                    new UngueltigeWahlscheine(new WahltagIdUndWahlbezirksart("wahltagID", WahlbezirkArt.UWB), ungueltigeWahlscheineData));

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_UNGUELTIGEWAHLSCHEINE);
            val request = MockMvcRequestBuilders.get("/businessActions/ungueltigews/wahltagID/UWB");
            val response = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

            Assertions.assertThat(response.getResponse().getContentAsByteArray()).isEqualTo(ungueltigeWahlscheineData);
            Assertions.assertThat(response.getResponse().getHeader("Content-Type")).isEqualTo("text/csv");
            Assertions.assertThat(response.getResponse().getHeader("Content-Disposition")).isEqualTo("attachment; filename=UWBUngueltigews.csv");
        }

        @Test
        void technischeWlsExceptionWhenNoDataFound() throws Exception {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_UNGUELTIGEWAHLSCHEINE);
            val request = MockMvcRequestBuilders.get("/businessActions/ungueltigews/wahltagID/UWB");
            val response = mockMvc.perform(request).andExpect(status().isInternalServerError()).andReturn();
            val responseBodyAsWlsExceptionDTO = objectMapper.readValue(response.getResponse().getContentAsByteArray(), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.T, ExceptionConstants.GETUNGUELTIGEWAHLSCHEINE_KEINE_DATEN.code(),
                    serviceOid, ExceptionConstants.GETUNGUELTIGEWAHLSCHEINE_KEINE_DATEN.message());

            Assertions.assertThat(responseBodyAsWlsExceptionDTO).isEqualTo(expectedWlsExceptionDTO);
        }

        @Test
        void fachlicheWlsExceptionWhenPathVariableIsInvalid() throws Exception {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_UNGUELTIGEWAHLSCHEINE);
            val request = MockMvcRequestBuilders.get("/businessActions/ungueltigews/   /UWB");
            val response = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn();
            val responseBodyAsWlsExceptionDTO = objectMapper.readValue(response.getResponse().getContentAsByteArray(), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F,
                    ExceptionConstants.GETUNGUELTIGEWAHLSCHEINE_PARAMETER_UNVOLLSTAENDIG.code(),
                    serviceOid, ExceptionConstants.GETUNGUELTIGEWAHLSCHEINE_PARAMETER_UNVOLLSTAENDIG.message());

            Assertions.assertThat(responseBodyAsWlsExceptionDTO).isEqualTo(expectedWlsExceptionDTO);
        }
    }

    @Nested
    class SetUngueltigeWahlscheine {

        @Test
        void newDataIsSet() throws Exception {
            val newData = "csv-data".getBytes();

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_POST_UNGUELTIGEWAHLSCHEINE);
            val request = MockMvcRequestBuilders.multipart("/businessActions/ungueltigews/wahltagID/UWB").file("ungueltigeWahlscheine", newData).with(csrf());
            mockMvc.perform(request).andExpect(status().isOk());

            SecurityUtils.runWith(Authorities.REPOSITORY_READ_UNGUELTIGEWAHLSCHEINE);
            val savedUngueltigeWahlscheine = ungueltigeWahlscheineRepository.findById(new WahltagIdUndWahlbezirksart("wahltagID", WahlbezirkArt.UWB)).get();

            Assertions.assertThat(savedUngueltigeWahlscheine.getUngueltigeWahlscheine()).isEqualTo(newData);
        }

        @Test
        void oldDataIsReplaced() throws Exception {
            val newData = "csv-data".getBytes();

            val oldData = "old-csv-data".getBytes();
            SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_UNGUELTIGEWAHLSCHEINE);
            ungueltigeWahlscheineRepository.save(new UngueltigeWahlscheine(new WahltagIdUndWahlbezirksart("wahltagID", WahlbezirkArt.UWB), oldData));

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_POST_UNGUELTIGEWAHLSCHEINE);
            val request = MockMvcRequestBuilders.multipart("/businessActions/ungueltigews/wahltagID/UWB").file("ungueltigeWahlscheine", newData).with(csrf());
            mockMvc.perform(request).andExpect(status().isOk());

            SecurityUtils.runWith(Authorities.REPOSITORY_READ_UNGUELTIGEWAHLSCHEINE);
            val savedUngueltigeWahlscheine = ungueltigeWahlscheineRepository.findById(new WahltagIdUndWahlbezirksart("wahltagID", WahlbezirkArt.UWB)).get();

            Assertions.assertThat(savedUngueltigeWahlscheine.getUngueltigeWahlscheine()).isEqualTo(newData);
            Assertions.assertThat(ungueltigeWahlscheineRepository.count()).isEqualTo(1);
        }

        @Test
        void fachlicheWlsExceptionWhenRequestIsInvalid() throws Exception {
            val newData = "csv-data".getBytes();

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_POST_UNGUELTIGEWAHLSCHEINE);
            val request = MockMvcRequestBuilders.multipart("/businessActions/ungueltigews/    /UWB").file("ungueltigeWahlscheine", newData).with(csrf());
            val response = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn();
            val responseBodyAsWlsExceptionDTO = objectMapper.readValue(response.getResponse().getContentAsByteArray(), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionConstants.POSTUNGUELTIGEWS_PARAMETER_UNVOLLSTAENDIG.code(),
                    serviceOid, ExceptionConstants.POSTUNGUELTIGEWS_PARAMETER_UNVOLLSTAENDIG.message());

            Assertions.assertThat(responseBodyAsWlsExceptionDTO).isEqualTo(expectedWlsExceptionDTO);
        }

        @Test
        void technischeWlsExceptionWhenNotSaveableCauseOfMissingAttachment() throws Exception {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_POST_UNGUELTIGEWAHLSCHEINE);
            val request = MockMvcRequestBuilders.multipart("/businessActions/ungueltigews/wahltagID/UWB").with(csrf());
            val response = mockMvc.perform(request).andExpect(status().isInternalServerError()).andReturn();
            val responseBodyAsWlsExceptionDTO = objectMapper.readValue(response.getResponse().getContentAsByteArray(), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.T, ExceptionKonstanten.CODE_ALLGEMEIN_UNBEKANNT,
                    serviceOid, "");

            Assertions.assertThat(responseBodyAsWlsExceptionDTO).usingRecursiveComparison().ignoringFields("message").isEqualTo(expectedWlsExceptionDTO);
        }
    }

}
