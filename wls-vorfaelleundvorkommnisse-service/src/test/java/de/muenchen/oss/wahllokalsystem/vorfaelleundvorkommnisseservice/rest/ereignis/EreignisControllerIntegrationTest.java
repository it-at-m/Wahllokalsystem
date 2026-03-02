package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis;

import static de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.EreignisseRepository;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.ereignis.EreignisseModelMapper;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.TestdataFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.util.List;
import java.util.Set;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
    classes = MicroServiceApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = {SPRING_TEST_PROFILE})
public class EreignisControllerIntegrationTest {

  @Autowired MockMvc api;

  @Autowired ObjectMapper objectMapper;

  @Autowired EreignisseRepository ereignisRepository;

  @Autowired EreignisDTOMapper ereignisDTOMapper;

  @Autowired EreignisseModelMapper ereignisModelMapper;

  @AfterEach
  void teardown() {
    SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_EREIGNISSE);
    ereignisRepository.deleteAll();
  }

  @Nested
  class GetEreignisse {

    @Test
    @Transactional
    void should_returnEmptyResponse_when_noDataFound() throws Exception {
      val request = MockMvcRequestBuilders.get("/businessActions/ereignisse/wahlbezirkID").with(
              jwt()
                      .authorities(
                              new SimpleGrantedAuthority(Authorities.SERVICE_GET_EREIGNISSE),
                              new SimpleGrantedAuthority(Authorities.REPOSITORY_READ_EREIGNISSE))
                      .jwt(jwt -> jwt.claim("wahlbezirkID", "wahlbezirkID")));
      val response = api.perform(request).andExpect(status().isNoContent()).andReturn();

      Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();
    }
      @Test
      @Transactional
      void should_returnForbidden_when_userHasWrongBezirkId() throws Exception {
          val request = MockMvcRequestBuilders.get("/businessActions/ereignisse/wahlbezirkID_Wrong").with(
                  jwt()
                          .authorities()
                          .jwt(jwt -> jwt.claim("wahlbezirkID", "wahlbezirkID_Wrong")));
          api.perform(request).andExpect(status().isForbidden());

      }
    @Test
    @Transactional
    void should_returnPersistedWahlbezirkEreignisseDTO_when_dataFound() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
        SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_EREIGNISSE);
      val ereignisList =
          Set.of(
              TestdataFactory.CreateEreignisEntity.withData("beschreibung1"),
              TestdataFactory.CreateEreignisEntity.withData("beschreibung2"));
      val ereignisse = TestdataFactory.CreateEreignisseEntity.withData(wahlbezirkID, ereignisList);
      ereignisRepository.save(ereignisse);

      val mockedEreignisseModel = ereignisModelMapper.toModel(ereignisse);
      val expectedResponseDTO = ereignisDTOMapper.toDTO(mockedEreignisseModel);

      val request = MockMvcRequestBuilders.get("/businessActions/ereignisse/wahlbezirkID").with(
              jwt()
                      .authorities(
                              new SimpleGrantedAuthority(Authorities.SERVICE_GET_EREIGNISSE),
                              new SimpleGrantedAuthority(Authorities.REPOSITORY_READ_EREIGNISSE))
                      .jwt(jwt -> jwt.claim("wahlbezirkID", wahlbezirkID)));
      val response = api.perform(request).andExpect(status().isOk()).andReturn();
      val responseBodyAsDTO =
          objectMapper.readValue(
              response.getResponse().getContentAsString(), WahlbezirkEreignisseDTO.class);

      Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedResponseDTO);
    }
  }

  @Nested
  class PostEreignisse {

    @Test
    void should_saveEreignisse_when_newDataIsPosted() throws Exception {
      val wahlbezirkID = "wahlbezirkID";

      val mockedEreignisDtoList =
          List.of(
              TestdataFactory.CreateEreignisDto.withData(),
              TestdataFactory.CreateEreignisDto.withData(),
              TestdataFactory.CreateEreignisDto.withData());
      val mockedEreignisseWriteDto =
          TestdataFactory.CreateEreignisseWriteDto.withData(mockedEreignisDtoList);
      val expectedSavedEreignisse =
          ereignisModelMapper.toEntity(
              ereignisDTOMapper.toModel(wahlbezirkID, mockedEreignisseWriteDto));

      val request = createPostWithBody(wahlbezirkID, mockedEreignisseWriteDto);
      val response = api.perform(request).andExpect(status().isOk()).andReturn();

      SecurityUtils.runWith(Authorities.REPOSITORY_READ_EREIGNISSE);
      val savedEreignisse = ereignisRepository.findByWahlbezirkID(wahlbezirkID);

      Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();
      Assertions.assertThat(savedEreignisse.get()).isEqualTo(expectedSavedEreignisse);
    }

    @Test
    void should_overrideExistingEreignisse_when_newDataIsPosted() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
        SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_EREIGNISSE, Authorities.REPOSITORY_READ_EREIGNISSE);
      val ereignisList = Set.of(TestdataFactory.CreateEreignisEntity.withData("beschreibung1"));
      val ereignisseToOverride =
          TestdataFactory.CreateEreignisseEntity.withData(wahlbezirkID, ereignisList);
      ereignisRepository.save(ereignisseToOverride);
      /*SecurityUtils.runWith(Authorities.REPOSITORY_READ_EREIGNISSE);*/

      val mockedEreignisDtoList =
          List.of(
              TestdataFactory.CreateEreignisDto.withData(),
              TestdataFactory.CreateEreignisDto.withData(),
              TestdataFactory.CreateEreignisDto.withData());
      val mockedEreignisseWriteDto =
          TestdataFactory.CreateEreignisseWriteDto.withData(mockedEreignisDtoList);
      val expectedSavedEreignisse =
          ereignisModelMapper.toEntity(
              ereignisDTOMapper.toModel(wahlbezirkID, mockedEreignisseWriteDto));

      SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_SET_EREIGNISSE);
      val request = createPostWithBody(wahlbezirkID, mockedEreignisseWriteDto);
      api.perform(request).andExpect(status().isOk()).andReturn();

      SecurityUtils.runWith(Authorities.REPOSITORY_READ_EREIGNISSE);
      val savedEreignisse = ereignisRepository.findByWahlbezirkID(wahlbezirkID);
      Assertions.assertThat(savedEreignisse.get()).isEqualTo(expectedSavedEreignisse);
    }
      @Test
      void should_returnForbidden_when_userHasWrongBezirkId() throws Exception {
        String wahlbezirkID = null;

          val mockedEreignisDtoList =
                  List.of(
                          TestdataFactory.CreateEreignisDto.withData(),
                          TestdataFactory.CreateEreignisDto.withData(),
                          TestdataFactory.CreateEreignisDto.withData());
          val mockedEreignisseWriteDto =
                  TestdataFactory.CreateEreignisseWriteDto.withData(mockedEreignisDtoList);
          val request = createPostWithBody(wahlbezirkID, mockedEreignisseWriteDto);
          api.perform(request).andExpect(status().isForbidden());

      }

    private MockHttpServletRequestBuilder createPostWithBody(
        final String wahlbezirkID, final EreignisseWriteDTO ereignisseWriteDTO) throws Exception {
      return MockMvcRequestBuilders.post("/businessActions/ereignisse/" + wahlbezirkID)
          .with(csrf())
              .with(
                      jwt()
                              .authorities(
                                     new SimpleGrantedAuthority(Authorities.SERVICE_POST_EREIGNISSE),
                                      new SimpleGrantedAuthority(Authorities.REPOSITORY_DELETE_EREIGNISSE),
                                      new SimpleGrantedAuthority(Authorities.REPOSITORY_WRITE_EREIGNISSE))
                              .jwt(jwt -> jwt.claim("wahlbezirkID", wahlbezirkID)))
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(ereignisseWriteDTO));
    }
  }
}
