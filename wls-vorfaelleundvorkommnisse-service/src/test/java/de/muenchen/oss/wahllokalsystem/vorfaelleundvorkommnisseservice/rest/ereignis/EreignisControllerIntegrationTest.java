package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignis;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.EreignisRepository;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignisart;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisModel;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisModelMapper;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.TestdataFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.Profiles;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, Profiles.NO_BEZIRKS_ID_CHECK })
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
            val response = api.perform(request).andExpect(status().isNoContent()).andReturn();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();
        }

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_GET_EREIGNISSE, Authorities.REPOSITORY_READ_EREIGNISSE, Authorities.REPOSITORY_WRITE_EREIGNISSE })
        @Transactional
        void dataFound() throws Exception {
            val wahlbezirkID = "wahlbezirkID";
            List<EreignisModel> ereignisModelList = new ArrayList<>();
            ereignisModelList.add(TestdataFactory.createEreignisModelWithData("beschreibung", LocalDateTime.now().withNano(0), Ereignisart.VORFALL));
            val ereignisseWriteModel = TestdataFactory.createEreignisseWriteModelWithData(wahlbezirkID, ereignisModelList);
            List<Ereignis> ereignisList = TestdataFactory.createEreignisEntityListFromModel(ereignisseWriteModel);
            ereignisRepository.saveAll(ereignisList);
            val ereignisseToLoad = ereignisModelMapper.toEreignisseModel(wahlbezirkID, false, true, ereignisModelList);

            val request = MockMvcRequestBuilders.get("/businessActions/ereignisse/wahlbezirkID");
            val response = api.perform(request).andExpect(status().isOk()).andReturn();

            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WahlbezirkEreignisseDTO.class);
            val expectedResponseDTO = ereignisDTOMapper.toDTO(ereignisseToLoad);
            Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedResponseDTO);
        }
    }

    @Nested
    class PostEreignisse {

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_POST_EREIGNISSE, Authorities.REPOSITORY_DELETE_EREIGNISSE, Authorities.REPOSITORY_WRITE_EREIGNISSE })
        void newDataSuccessfullySaved() throws Exception {
            val wahlbezirkID = "wahlbezirkID";
            List<EreignisDTO> ereignisDtoList = new ArrayList<>();
            ereignisDtoList.add(TestdataFactory.createEreignisDtoWithData("beschreibung", LocalDateTime.now().withNano(0), Ereignisart.VORFALL));
            ereignisDtoList.add(TestdataFactory.createEreignisDtoWithData("beschreibung2", LocalDateTime.now().withNano(0), Ereignisart.VORFALL));
            ereignisDtoList.add(TestdataFactory.createEreignisDtoWithData("beschreibung3", LocalDateTime.now().withNano(0), Ereignisart.VORKOMMNIS));
            val ereignisseWriteDto = TestdataFactory.createEreignisseWriteDTOWithData(ereignisDtoList);

            val request = createPostWithBody(wahlbezirkID, ereignisseWriteDto);
            val response = api.perform(request).andExpect(status().isOk()).andReturn();

            SecurityUtils.runWith(Authorities.REPOSITORY_READ_EREIGNISSE);
            val savedEreignisse = ereignisRepository.findByWahlbezirkID(wahlbezirkID);
            Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();

            List<Ereignis> expectedSavedEreignisse = new ArrayList<>();
            for (EreignisDTO ereignisDto : ereignisseWriteDto.ereigniseintraege()) {
                Ereignis ereignis = new Ereignis(wahlbezirkID, ereignisDto.beschreibung(), ereignisDto.uhrzeit(), ereignisDto.ereignisart());
                expectedSavedEreignisse.add(ereignis);
            }
            Assertions.assertThat(savedEreignisse.size()).isEqualTo(expectedSavedEreignisse.size());
            Assertions.assertThat(savedEreignisse).filteredOn(ereignis -> ereignis.getClass().equals(Ereignis.class));
            Assertions.assertThat(savedEreignisse).filteredOn(ereignis -> ereignis.getWahlbezirkID().equals(wahlbezirkID));
        }

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_POST_EREIGNISSE, Authorities.REPOSITORY_DELETE_EREIGNISSE, Authorities.REPOSITORY_WRITE_EREIGNISSE })
        void oldDataOverriden() throws Exception {
            // create ereignisse
            val wahlbezirkID = "wahlbezirkID";
            List<EreignisModel> ereignisModelList = new ArrayList<>();
            ereignisModelList.add(TestdataFactory.createEreignisModelWithData("beschreibung", LocalDateTime.now().withNano(0), Ereignisart.VORFALL));
            val ereignisseWriteModel = TestdataFactory.createEreignisseWriteModelWithData(wahlbezirkID, ereignisModelList);
            List<Ereignis> ereignisListToOverride = TestdataFactory.createEreignisEntityListFromModel(ereignisseWriteModel);
            // save ereignisse
            ereignisRepository.saveAll(ereignisListToOverride);
            // read ereignisse
            SecurityUtils.runWith(Authorities.REPOSITORY_READ_EREIGNISSE);
            val savedEreignisseBeforeOverridden = ereignisRepository.findByWahlbezirkID(wahlbezirkID);
            Assertions.assertThat(savedEreignisseBeforeOverridden.size()).isEqualTo(ereignisListToOverride.size());

            // create new ereignisse
            List<EreignisDTO> ereignisDtoList = new ArrayList<>();
            ereignisDtoList.add(TestdataFactory.createEreignisDtoWithData("beschreibung", LocalDateTime.now().withNano(0), Ereignisart.VORFALL));
            ereignisDtoList.add(TestdataFactory.createEreignisDtoWithData("beschreibung2", LocalDateTime.now().withNano(0), Ereignisart.VORFALL));
            ereignisDtoList.add(TestdataFactory.createEreignisDtoWithData("beschreibung3", LocalDateTime.now().withNano(0), Ereignisart.VORKOMMNIS));
            val ereignisseWriteDto = TestdataFactory.createEreignisseWriteDTOWithData(ereignisDtoList);
            // save new ereignisse and override old ereignisse
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_SET_EREIGNISSE);
            val request = createPostWithBody(wahlbezirkID, ereignisseWriteDto);
            api.perform(request).andExpect(status().isOk()).andReturn();
            // read new ereignisse
            SecurityUtils.runWith(Authorities.REPOSITORY_READ_EREIGNISSE);
            val savedEreignisse = ereignisRepository.findByWahlbezirkID(wahlbezirkID);
            List<Ereignis> expectedSavedEreignisse = new ArrayList<>();
            for (EreignisDTO ereignisDto : ereignisseWriteDto.ereigniseintraege()) {
                Ereignis ereignis = new Ereignis(wahlbezirkID, ereignisDto.beschreibung(), ereignisDto.uhrzeit(), ereignisDto.ereignisart());
                expectedSavedEreignisse.add(ereignis);
            }
            Assertions.assertThat(savedEreignisse.size()).isEqualTo(expectedSavedEreignisse.size());

        }

        private MockHttpServletRequestBuilder createPostWithBody(final String wahlbezirkID, final EreignisseWriteDTO ereignisseWriteDTO) throws Exception {
            return MockMvcRequestBuilders.post("/businessActions/ereignisse/" + wahlbezirkID)
                    .with(csrf()).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(ereignisseWriteDTO));
        }
    }
}
