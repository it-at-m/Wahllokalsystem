package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignis;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.EreignisRepository;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisModelMapper;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisartModel;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.TestdataFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.Profiles;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
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
        void should_returnEmptyResponse_when_noDataFound() throws Exception {
            val request = MockMvcRequestBuilders.get("/businessActions/ereignisse/wahlbezirkID");
            val response = api.perform(request).andExpect(status().isNoContent()).andReturn();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();
        }

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_GET_EREIGNISSE, Authorities.REPOSITORY_READ_EREIGNISSE, Authorities.REPOSITORY_WRITE_EREIGNISSE })
        @Transactional
        void should_returnPersistedWahlbezirkEreignisseDTO_when_dataFound() throws Exception {
            val wahlbezirkID = "wahlbezirkID";

            val mockedEreignisModelList = List.of(TestdataFactory.CreateEreignisModel.withEreignisart(EreignisartModel.VORFALL));
            val mockedEreignisseWriteModel = TestdataFactory.CreateEreignisseWriteModel.withData(wahlbezirkID, mockedEreignisModelList);
            val mockedEreignisList = TestdataFactory.CreateEreignisEntity.listFromModel(mockedEreignisseWriteModel);
            val mockedEreignisseToLoad = ereignisModelMapper.toEreignisseModel(wahlbezirkID, false, true, mockedEreignisModelList);
            ereignisRepository.saveAll(mockedEreignisList);

            val request = MockMvcRequestBuilders.get("/businessActions/ereignisse/wahlbezirkID");
            val response = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WahlbezirkEreignisseDTO.class);

            val expectedResponseDTO = ereignisDTOMapper.toDTO(mockedEreignisseToLoad);
            Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedResponseDTO);
        }
    }

    @Nested
    class PostEreignisse {

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_POST_EREIGNISSE, Authorities.REPOSITORY_DELETE_EREIGNISSE, Authorities.REPOSITORY_WRITE_EREIGNISSE })
        void should_saveListOfEreignisEntities_when_newDataSuccessfullySaved() throws Exception {
            val wahlbezirkID = "wahlbezirkID";

            val mockedEreignisDtoList = List.of(
                    TestdataFactory.CreateEreignisDto.withData(),
                    TestdataFactory.CreateEreignisDto.withData(),
                    TestdataFactory.CreateEreignisDto.withData());
            val mockedEreignisseWriteDto = TestdataFactory.CreateEreignisseWriteDto.withData(mockedEreignisDtoList);

            val request = createPostWithBody(wahlbezirkID, mockedEreignisseWriteDto);
            val response = api.perform(request).andExpect(status().isOk()).andReturn();

            SecurityUtils.runWith(Authorities.REPOSITORY_READ_EREIGNISSE);
            val savedEreignisse = ereignisRepository.findByWahlbezirkID(wahlbezirkID);

            val expectedSavedEreignisse = mockedEreignisseWriteDto.ereigniseintraege().stream()
                    .map(ereignisDto -> new Ereignis(wahlbezirkID, ereignisDto.beschreibung(), ereignisDto.uhrzeit(),
                            TestdataFactory.MapEreignisart.ereignisartDtoToEreignisart(ereignisDto.ereignisart())))
                    .toList();
            Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();
            Assertions.assertThat(savedEreignisse.size()).isEqualTo(expectedSavedEreignisse.size());
            Assertions.assertThat(savedEreignisse).allSatisfy(ereignis -> {
                Assertions.assertThat(ereignis.getWahlbezirkID()).isEqualTo(wahlbezirkID);
            });
        }

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_POST_EREIGNISSE, Authorities.REPOSITORY_DELETE_EREIGNISSE, Authorities.REPOSITORY_WRITE_EREIGNISSE })
        void should_overrideOldListOfEreignisEntities_when_newDataSuccessfullySaved() throws Exception {
            val wahlbezirkID = "wahlbezirkID";

            val mockedEreignisModelList = List.of(TestdataFactory.CreateEreignisModel.withData());
            val mockedEreignisseWriteModel = TestdataFactory.CreateEreignisseWriteModel.withData(wahlbezirkID, mockedEreignisModelList);
            val ereignisListToOverride = TestdataFactory.CreateEreignisEntity.listFromModel(mockedEreignisseWriteModel);
            ereignisRepository.saveAll(ereignisListToOverride);

            SecurityUtils.runWith(Authorities.REPOSITORY_READ_EREIGNISSE);
            val savedEreignisseBeforeOverridden = ereignisRepository.findByWahlbezirkID(wahlbezirkID);
            val mockedEreignisDtoList = List.of(
                    TestdataFactory.CreateEreignisDto.withData(),
                    TestdataFactory.CreateEreignisDto.withData(),
                    TestdataFactory.CreateEreignisDto.withData());
            val mockedEreignisseWriteDto = TestdataFactory.CreateEreignisseWriteDto.withData(mockedEreignisDtoList);

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_SET_EREIGNISSE);
            val request = createPostWithBody(wahlbezirkID, mockedEreignisseWriteDto);
            api.perform(request).andExpect(status().isOk()).andReturn();

            SecurityUtils.runWith(Authorities.REPOSITORY_READ_EREIGNISSE);
            val savedEreignisse = ereignisRepository.findByWahlbezirkID(wahlbezirkID);
            val expectedSavedEreignisse = mockedEreignisseWriteDto.ereigniseintraege().stream()
                    .map(ereignisDto -> new Ereignis(wahlbezirkID, ereignisDto.beschreibung(), ereignisDto.uhrzeit(),
                            TestdataFactory.MapEreignisart.ereignisartDtoToEreignisart(ereignisDto.ereignisart())))
                    .toList();
            Assertions.assertThat(savedEreignisseBeforeOverridden.size()).isEqualTo(ereignisListToOverride.size());
            Assertions.assertThat(savedEreignisse.size()).isEqualTo(expectedSavedEreignisse.size());

        }

        private MockHttpServletRequestBuilder createPostWithBody(final String wahlbezirkID, final EreignisseWriteDTO ereignisseWriteDTO) throws Exception {
            return MockMvcRequestBuilders.post("/businessActions/ereignisse/" + wahlbezirkID)
                    .with(csrf()).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(ereignisseWriteDTO));
        }
    }
}
