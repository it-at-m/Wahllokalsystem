package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.EreignisRepository;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignisart;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisModelMapper;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.TestdataFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE })
public class EreignisControllerIntegrationTest {

    @Autowired
    MockMvc api;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    EreignisRepository ereignisRepository;

    @Autowired
    EreignisDTOMapper ereignisDTOMapper;

    @Autowired
    EreignisModelMapper ereignisModelMapper;


    @AfterEach
    void tearDown() {
        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_EREIGNISSE);
        ereignisRepository.deleteAll();
    }

    @Nested
    class GetEreignisse {

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_GET_EREIGNISSE, Authorities.REPOSITORY_READ_EREIGNISSE })
        @Transactional
        void noDataFound() throws Exception {
            val request = MockMvcRequestBuilders.get("/businessActions/ereignisse/wahlbezirkID");
            val response = api.perform(request).andExpect(status().isNotFound()).andReturn();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();
        }

        @Test
        @WithMockUser(authorities = { Authorities.REPOSITORY_READ_EREIGNISSE, Authorities.SERVICE_GET_EREIGNISSE })
        @Transactional
        void dataFound() throws Exception {

            val ereignis = TestdataFactory.createEreignisEntityWithData("wahlbezirkID", "beschreibung", LocalDateTime.now(), Ereignisart.VORFALL);
            val savedEreignis = ereignisRepository.save(ereignis);
            val ereignisToLoad = ereignisModelMapper.toModel(savedEreignis);

            val request = MockMvcRequestBuilders.get("/businessactions/ereignisse/wahlbezirkID");

            val response = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsString(), EreignisDTO.class);
            val expectedResponseDTO = ereignisDTOMapper.toDTO(ereignisToLoad);

            Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedResponseDTO);
        }
    }

}
