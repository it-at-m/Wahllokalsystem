package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.eaiservice.Authorities;
import de.muenchen.oss.wahllokalsystem.eaiservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.eaiservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Stimmzettelgebiet;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.StimmzettelgebietRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Stimmzettelgebietsart;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahl;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.WahlRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahlart;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahlbezirk;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.WahlbezirkRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahltag;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.WahltageRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlberechtigteDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlbezirkArtDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.wahldaten.WahldatenMapper;
import java.time.LocalDate;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = MicroServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles({ TestConstants.SPRING_TEST_PROFILE })
public class WahldatenControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    WahltageRepository wahltageRepository;

    @Autowired
    WahlRepository wahlRepository;

    @Autowired
    StimmzettelgebietRepository stimmzettelgebietRepository;

    @Autowired
    WahlbezirkRepository wahlbezirkRepository;

    @Autowired
    WahldatenMapper wahldatenMapper;

    @Autowired
    ObjectMapper objectMapper;

    @AfterEach
    public void tearDown() throws Exception {
        wahlbezirkRepository.deleteAll();
        stimmzettelgebietRepository.deleteAll();
        wahlRepository.deleteAll();
        wahltageRepository.deleteAll();
    }

    @Nested
    class LoadWahlberechtigte {

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_WAHLBERECHTIGTE)
        void dataFound() throws Exception {
            val wahltag = wahltageRepository.save(new Wahltag(LocalDate.now(), "beschreibung wahltag", "nummer"));
            val wahl = wahlRepository.save(new Wahl("wahl", Wahlart.BTW, wahltag, "1"));
            val stimmzettelgebiet = stimmzettelgebietRepository.save(new Stimmzettelgebiet("sgz1", "sgz1", Stimmzettelgebietsart.SK, wahl));
            val wahlbezirkToFind = wahlbezirkRepository.save(new Wahlbezirk(WahlbezirkArtDTO.UWB, "nummer", stimmzettelgebiet, 10, 11, 12));

            wahlbezirkRepository.save(new Wahlbezirk(WahlbezirkArtDTO.UWB, "nummer", stimmzettelgebiet, 10, 11, 12));

            val request = get(WahldatenController.WAHLDATEN_REQUEST_MAPPING + "/wahlbezirke/" + wahlbezirkToFind.getId() + "/wahlberechtigte");

            val response = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WahlberechtigteDTO[].class);

            val expectedResponseBody = new WahlberechtigteDTO[] { wahldatenMapper.toWahlberechtigteDTO(wahlbezirkToFind) };

            Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedResponseBody);
        }

        @Test
        void noDataFound() {

        }

        @Test
        void wlsExceptionOnValidationFailed() {

        }

    }
}
