package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.unterbrechungsuhrzeit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.UnterbrechungsUhrzeit;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.UnterbrechungsUhrzeitRepository;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.unterbrechungsuhrzeit.UnterbrechungsUhrzeitModelMapper;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils.SecurityUtils;
import de.muenchen.oss.wahllokalsystem.wls.common.security.Profiles;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles({ TestConstants.SPRING_TEST_PROFILE, Profiles.NO_BEZIRKS_ID_CHECK })
public class UnterbrechungsUhrzeitControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UnterbrechungsUhrzeitModelMapper unterbrechungsUhrzeitModelMapper;

    @Autowired
    UnterbrechungsUhrzeitDTOMapper unterbrechungsUhrzeitDTOMapper;

    @Autowired
    UnterbrechungsUhrzeitRepository unterbrechungsUhrzeitRepository;

    @AfterEach
    void tearDown() {
        SecurityUtils.runAs(Authorities.REPOSITORY_DELETE_UNTERBRECHUNGSUHRZEIT);
        unterbrechungsUhrzeitRepository.deleteAll();
    }

    @Nested
    class GetUnterbrechungsUhrzeit {
        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_UNTERBRECHUNGSUHRZEIT, Authorities.REPOSITORY_WRITE_UNTERBRECHUNGSUHRZEIT })
        void dataFound() throws Exception {

            val wahlbezirkIDToFind = "123";
            val unterbrechungsUhrzeitToFind = new UnterbrechungsUhrzeit();
            unterbrechungsUhrzeitToFind.setWahlbezirkID(wahlbezirkIDToFind);
            unterbrechungsUhrzeitToFind.setUnterbrechungsUhrzeit(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
            unterbrechungsUhrzeitRepository.save(unterbrechungsUhrzeitToFind);
            val expectedResponseBody = unterbrechungsUhrzeitDTOMapper.toDTO(unterbrechungsUhrzeitModelMapper.toModel(unterbrechungsUhrzeitToFind));

            val request = get("/businessActions/unterbrechungsUhrzeit/" + wahlbezirkIDToFind);

            val response = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsString(), UnterbrechungsUhrzeitDTO.class);

            Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedResponseBody);
        }

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_UNTERBRECHUNGSUHRZEIT, Authorities.REPOSITORY_WRITE_UNTERBRECHUNGSUHRZEIT })
        void noDataFound() throws Exception {

            val wahlbezirkIDEmpty = "123";

            val wahlbezirkIDNotEmpty = "456";
            val unterbrechungsUhrzeitToFind = new UnterbrechungsUhrzeit();
            unterbrechungsUhrzeitToFind.setWahlbezirkID(wahlbezirkIDNotEmpty);
            unterbrechungsUhrzeitToFind.setUnterbrechungsUhrzeit(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
            unterbrechungsUhrzeitRepository.save(unterbrechungsUhrzeitToFind);

            val request = get("/businessActions/unterbrechungsUhrzeit/" + wahlbezirkIDEmpty);

            val response = mockMvc.perform(request).andExpect(status().isNoContent()).andReturn();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();
        }
    }

}
