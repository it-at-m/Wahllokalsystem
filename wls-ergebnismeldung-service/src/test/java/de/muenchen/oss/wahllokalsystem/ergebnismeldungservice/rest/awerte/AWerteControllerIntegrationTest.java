package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.awerte;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client.eai.AWerteClientMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerteRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.WahlberechtigteDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AWerteModelMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AsyncAWerteService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE, Profiles.DUMMY_CLIENTS })
public class AWerteControllerIntegrationTest {

    @Configuration
    static class TestConfiguration {
        @Primary
        @Bean
        public TaskExecutor syncTaskExecutor() {
            return new SyncTaskExecutor();
        }
    }

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AWerteDTOMapper aWerteDTOMapper;

    @Autowired
    AWerteModelMapper aWerteModelMapper;

    @Autowired
    AWerteClientMapper aWerteClientMapper;

    @Autowired
    MockMvc api;

    @Autowired
    AWerteRepository awerteRepository;

    @SpyBean
    AsyncAWerteService asyncAWerteService;

    @AfterEach
    void teardown() {
        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_AWERTE);
        awerteRepository.deleteAll();
    }

    @Nested
    class GetAWerte {

        @Test
        void should_returnAWerteListFromEAI_when_dataFound() throws Exception {
            val wahlbezirkID = "wahlbezirkID1";
            // same values as DummyClientImpl
            val eaiWahlberechtigte = createClientListOfAWahlberechtigteDTO(wahlbezirkID);

            val request = MockMvcRequestBuilders.get("/businessActions/awerte/" + wahlbezirkID);
            val response = api.perform(request).andExpect(status().isOk()).andReturn();

            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsString(),
                    de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.awerte.AWerteDTO[].class);
            val expectedResponseBody = aWerteDTOMapper
                    .fromListOfAWerteModelToListOfAWerteDTO(aWerteClientMapper.fromRemoteClientListOfWahlberechtigteDtoToListOfAWerteModel(eaiWahlberechtigte));

            Assertions.assertThat(responseBodyAsDTO).containsExactlyInAnyOrderElementsOf(expectedResponseBody);
        }

        @Test
        void should_saveAWerteListFromEAIToRepo_when_dataFound() throws Exception {
            val wahlbezirkID = "wahlbezirkID1";
            // same values as DummyClientImpl
            val eaiWahlberechtigte = createClientListOfAWahlberechtigteDTO(wahlbezirkID);

            val request = MockMvcRequestBuilders.get("/businessActions/awerte/" + wahlbezirkID);
            api.perform(request).andExpect(status().isOk()).andReturn();

            val aWerteFromRepo = awerteRepository.findByBezirkUndWahlID_WahlbezirkID(wahlbezirkID);
            val expectedEntities = aWerteModelMapper.fromListOfAWerteModelToListOfAWerteEntity(
                    aWerteClientMapper.fromRemoteClientListOfWahlberechtigteDtoToListOfAWerteModel(eaiWahlberechtigte));

            Assertions.assertThat(aWerteFromRepo).usingRecursiveComparison().isEqualTo(expectedEntities);
        }
    }

    @Nested
    class InitialiseAWerte {
        @Test
        void should_callAsynchronousMethodInitialiseAWerteAndReturnWithOK_when_wahlbezirkIDsAreGiven() throws Exception {
            val wahlbezirkIDs = List.of("wahlbezirkID1", "wahlbezirkID2", "wahlbezirkID3");

            val request = MockMvcRequestBuilders.post("/businessActions/awerte/init").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(wahlbezirkIDs));

            api.perform(request).andExpect(status().isOk()).andReturn();
            Mockito.verify(asyncAWerteService).initialiseAWerte(wahlbezirkIDs);
        }
    }

    private List<WahlberechtigteDTO> createClientListOfAWahlberechtigteDTO(String wahlbezirkID) {
        val wahlberechtigte1 = new WahlberechtigteDTO();
        wahlberechtigte1.setWahlID("wahlID01");
        wahlberechtigte1.setWahlbezirkID(wahlbezirkID);
        wahlberechtigte1.setA1(25L);
        wahlberechtigte1.setA2(26L);
        return List.of(wahlberechtigte1);
    }
}
