package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.handbuch;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Handbuch;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.HandbuchRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahlbezirkArt;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahltagIdUndWahlbezirksart;
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
public class HandbuchControllerIntegretionTest {

    @Value("${service.info.oid}")
    String serviceOid;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    HandbuchRepository handbuchRepository;

    @AfterEach
    void tearDown() {
        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_HANDBUCH);
        handbuchRepository.deleteAll();
    }

    @Nested
    class GetHandbuch {

        @Test
        void dataFound() throws Exception {
            val handbuchContent = "dies ist ein Handbuch".getBytes();

            SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_HANDBUCH);
            handbuchRepository.save(new Handbuch(new WahltagIdUndWahlbezirksart("wahltagID", WahlbezirkArt.UWB), handbuchContent));

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_HANDBUCH);
            val request = MockMvcRequestBuilders.get("/businessActions/handbuch/wahltagID/UWB");
            val response = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsByteArray = response.getResponse().getContentAsByteArray();

            Assertions.assertThat(response.getResponse().getHeader("Content-Type")).isEqualTo("application/pdf");
            Assertions.assertThat(response.getResponse().getHeader("Content-Disposition")).isEqualTo("attachment; filename=UWBHandbuch.pdf");
            Assertions.assertThat(responseBodyAsByteArray).isEqualTo(handbuchContent);
        }

        @Test
        void noDataFound() throws Exception {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_HANDBUCH);
            val request = MockMvcRequestBuilders.get("/businessActions/handbuch/wahltagID/UWB");
            val response = mockMvc.perform(request).andExpect(status().isInternalServerError()).andReturn();

            val responseBodyAsWlsExceptionDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.T, ExceptionConstants.GETHANDBUCH_KEINE_DATEN.code(), serviceOid,
                    ExceptionConstants.GETHANDBUCH_KEINE_DATEN.message());
            Assertions.assertThat(responseBodyAsWlsExceptionDTO).isEqualTo(expectedWlsExceptionDTO);
        }

    }

    @Nested
    class SetHandbuch {

        @Test
        void dataIsSaved() throws Exception {
            val handbuchContent = "dies ist ein Handbuch".getBytes();

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_POST_HANDBUCH);
            val request = MockMvcRequestBuilders.multipart("/businessActions/handbuch/wahltagID/UWB").file("manual", handbuchContent).with(csrf());
            mockMvc.perform(request).andExpect(status().isOk());

            SecurityUtils.runWith(Authorities.REPOSITORY_READ_HANDBUCH);
            val savedHandbuch = handbuchRepository.findById(new WahltagIdUndWahlbezirksart("wahltagID", WahlbezirkArt.UWB)).get();

            Assertions.assertThat(savedHandbuch.getHandbuch()).isEqualTo(handbuchContent);
        }

        @Test
        void existingDataIsReplaced() throws Exception {
            SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_HANDBUCH);
            handbuchRepository.save(new Handbuch(new WahltagIdUndWahlbezirksart("wahltagID", WahlbezirkArt.UWB), "alter handbuch content".getBytes()));

            val handbuchContent = "dies ist ein Handbuch".getBytes();

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_POST_HANDBUCH);
            val request = MockMvcRequestBuilders.multipart("/businessActions/handbuch/wahltagID/UWB").file("manual", handbuchContent).with(csrf());
            mockMvc.perform(request).andExpect(status().isOk());

            SecurityUtils.runWith(Authorities.REPOSITORY_READ_HANDBUCH);
            val savedHandbuch = handbuchRepository.findById(new WahltagIdUndWahlbezirksart("wahltagID", WahlbezirkArt.UWB)).get();

            Assertions.assertThat(savedHandbuch.getHandbuch()).isEqualTo(handbuchContent);
        }

        @Test
        void wlsExceptionOccuredCauseOfMissingAttachment() throws Exception {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_POST_HANDBUCH);
            val request = MockMvcRequestBuilders.multipart("/businessActions/handbuch/wahltagID/UWB").with(csrf());
            val response = mockMvc.perform(request).andExpect(status().isInternalServerError()).andReturn();
            val responseBodyAsWlsExceptionDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.T, ExceptionKonstanten.CODE_ALLGEMEIN_UNBEKANNT,
                    serviceOid, "");

            Assertions.assertThat(responseBodyAsWlsExceptionDTO).usingRecursiveComparison().ignoringFields("message").isEqualTo(expectedWlsExceptionDTO);
        }
    }
}
