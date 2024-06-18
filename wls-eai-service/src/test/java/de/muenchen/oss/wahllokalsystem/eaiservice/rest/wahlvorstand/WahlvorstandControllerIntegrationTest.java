package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand;

import static de.muenchen.oss.wahllokalsystem.eaiservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.eaiservice.Authorities;
import de.muenchen.oss.wahllokalsystem.eaiservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorstand.Wahlvorstand;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorstand.WahlvorstandFunktion;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorstand.WahlvorstandRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorstand.Wahlvorstandsmitglied;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.common.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto.WahlvorstandDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlvorstand.WahlvorstandMapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, "no-security" })
public class WahlvorstandControllerIntegrationTest {

    @Value("${service.info.oid}")
    String serviceInfoOid;

    @Autowired
    MockMvc api;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WahlvorstandRepository wahlvorstandRepository;

    @Autowired
    WahlvorstandMapper wahlvorstandMapper;

    @Autowired
    ExceptionFactory exceptionFactory;

    @AfterEach
    void tearDown() {
        wahlvorstandRepository.deleteAll();
    }

    @Nested
    class LoadWahlvorstand {

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_WAHLVORSTAND)
        void noDataFound() throws Exception {
            val request = MockMvcRequestBuilders.get("/wahlvorstaende?wahlbezirkID=" + UUID.randomUUID());

            val response = api.perform(request).andExpect(status().isOk()).andReturn();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();
        }

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_WAHLVORSTAND)
        @Transactional
        void dataFound() throws Exception {
            val wahlbezirkID1 = UUID.randomUUID();
            val wahlvorstand1 = new Wahlvorstand(wahlbezirkID1,
                    Set.of(new Wahlvorstandsmitglied("vorname11", "nachname11", WahlvorstandFunktion.B, true, LocalDateTime.now()),
                            new Wahlvorstandsmitglied("vorname12", "nachname12", WahlvorstandFunktion.SWB, false, LocalDateTime.now())));
            val wahlvorstandToLoad = wahlvorstandRepository.save(wahlvorstand1);

            val wahlbezirkID2 = UUID.randomUUID();
            val wahlvorstand2 = new Wahlvorstand(wahlbezirkID2,
                    Set.of(new Wahlvorstandsmitglied("vorname21", "nachname21", WahlvorstandFunktion.B, true, LocalDateTime.now()),
                            new Wahlvorstandsmitglied("vorname22", "nachname22", WahlvorstandFunktion.SWB, false, LocalDateTime.now())));
            wahlvorstandRepository.save(wahlvorstand2);

            val request = MockMvcRequestBuilders.get("/wahlvorstaende?wahlbezirkID=" + wahlvorstandToLoad.getWahlbezirkID());

            val response = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WahlvorstandDTO.class);

            val expectedResponseDTO = wahlvorstandMapper.toDTO(wahlvorstandToLoad);

            Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedResponseDTO);
        }

        @Test
        void wlsExceptionOnInvalidWahlbezirkIDFormat() throws Exception {
            val request = MockMvcRequestBuilders.get("/wahlvorstaende?wahlbezirkID=wrongFormat");

            val response = api.perform(request).andExpect(status().isBadRequest()).andReturn();
            val wlsExceptionDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WlsExceptionDTO.class);

            val expectedWlsException = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionConstants.ID_NICHT_KONVERTIERBAR.code(), serviceInfoOid,
                    ExceptionConstants.ID_NICHT_KONVERTIERBAR.message());

            Assertions.assertThat(wlsExceptionDTO).isEqualTo(expectedWlsException);
        }
    }

}
