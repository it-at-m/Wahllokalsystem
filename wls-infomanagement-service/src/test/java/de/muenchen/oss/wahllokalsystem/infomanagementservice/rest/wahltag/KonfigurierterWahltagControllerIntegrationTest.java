package de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.wahltag;

import static de.muenchen.oss.wahllokalsystem.infomanagementservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.wahltag.KonfigurierterWahltag;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.wahltag.KonfigurierterWahltagRepository;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.utils.SecurityUtils;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.wahltag.KonfigurierterWahltagValidator;
import java.time.LocalDate;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE })
public class KonfigurierterWahltagControllerIntegrationTest {

    @Autowired
    MockMvc api;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KonfigurierterWahltagRepository konfigurierterWahltagRepository;

    @SpyBean
    KonfigurierterWahltagValidator konfigurierterWahltagValidator;

    @AfterEach
    void tearDown() {
        SecurityUtils.runAs("", "", Authorities.REPOSITORY_DELETE_KONFIGURIERTERWAHLTAG);
        konfigurierterWahltagRepository.deleteAll();
    }

    @Nested
    class GetKonfigurierterWahltag {

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_GET_KONFIGURIERTERWAHLTAG, Authorities.REPOSITORY_READ_KONFIGURIERTERWAHLTAG })
        void emptyResponse() throws Exception {
            val request = MockMvcRequestBuilders.get("/businessActions/konfigurierterWahltag");

            val response = api.perform(request).andExpect(status().isNoContent()).andReturn();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_GET_KONFIGURIERTERWAHLTAG, Authorities.REPOSITORY_READ_KONFIGURIERTERWAHLTAG,
                        Authorities.REPOSITORY_WRITE_KONFIGURIERTERWAHLTAG }
        )
        void dataFound() throws Exception {

            val konfigurierterWahltag1 = new KonfigurierterWahltag(LocalDate.now(), "1-2-3", WahltagStatus.INAKTIV, "4711");
            val konfigurierterWahltag2 = new KonfigurierterWahltag(LocalDate.now(), "3-4-5", WahltagStatus.INAKTIV, "0190");
            val konfigurierterWahltagExpected = new KonfigurierterWahltag(LocalDate.now(), "6-7-8", WahltagStatus.AKTIV, "0103");

            konfigurierterWahltagRepository.save(konfigurierterWahltag1);
            konfigurierterWahltagRepository.save(konfigurierterWahltag2);
            konfigurierterWahltagRepository.save(konfigurierterWahltagExpected);

            val request = MockMvcRequestBuilders.get("/businessActions/konfigurierterWahltag");

            val response = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBody = objectMapper.readValue(response.getResponse().getContentAsString(), KonfigurierterWahltagDTO.class);

            val expectedResponseBody = new KonfigurierterWahltagDTO(konfigurierterWahltagExpected.getWahltag(), konfigurierterWahltagExpected.getWahltagID(),
                    konfigurierterWahltagExpected.getWahltagStatus(), konfigurierterWahltagExpected.getNummer());

            Assertions.assertThat(responseBody).isEqualTo(expectedResponseBody);
        }
    }

    @Nested
    class PostKonfigurierterWahltag {

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_POST_KONFIGURIERTERWAHLTAG, Authorities.REPOSITORY_WRITE_KONFIGURIERTERWAHLTAG,
                        Authorities.SERVICE_GET_KONFIGURIERTERWAHLTAG, Authorities.REPOSITORY_READ_KONFIGURIERTERWAHLTAG }
        )
        void newAktivWahltagSavedAndReadOKWithBody() throws Exception {
            // DB leer, Wahltag neu setzen mit WahltagStatus.AKTIV -> OK zurück mit empty body
            val konfigurierterWahltagDTO = new KonfigurierterWahltagDTO(LocalDate.now(), "1-2-3", WahltagStatus.AKTIV, "4711");
            val requestPost = createPostWithBody(konfigurierterWahltagDTO);

            val responsePost = api.perform(requestPost).andExpect(status().isOk()).andReturn();

            Assertions.assertThat(responsePost.getResponse().getContentAsString()).isEmpty();

            // vorher gesetzten Wahltag aus DB lesen -> OK zurück mit vorher gesetztem body
            val requestGet = MockMvcRequestBuilders.get("/businessActions/konfigurierterWahltag");
            val responseGet = api.perform(requestGet).andExpect(status().isOk()).andReturn();
            val responseGetBody = objectMapper.readValue(responseGet.getResponse().getContentAsString(), KonfigurierterWahltagDTO.class);

            val expectedResponseGetBody = new KonfigurierterWahltagDTO(konfigurierterWahltagDTO.wahltag(), konfigurierterWahltagDTO.wahltagID(),
                    konfigurierterWahltagDTO.wahltagStatus(), konfigurierterWahltagDTO.nummer());

            Assertions.assertThat(responseGetBody).isEqualTo(expectedResponseGetBody);
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_POST_KONFIGURIERTERWAHLTAG, Authorities.REPOSITORY_WRITE_KONFIGURIERTERWAHLTAG,
                        Authorities.SERVICE_GET_KONFIGURIERTERWAHLTAG, Authorities.REPOSITORY_READ_KONFIGURIERTERWAHLTAG }
        )
        void newInaktivWahltagSavedAndReadOKNoContent() throws Exception {
            // Dooffall: DB leer neu setzen mit WahltagStatus.INAKTIV -> OK zurück mit empty body
            val konfigurierterWahltagDTO = new KonfigurierterWahltagDTO(LocalDate.now(), "1-2-3", WahltagStatus.INAKTIV, "4711");
            val requestPost = createPostWithBody(konfigurierterWahltagDTO);

            val responsePost = api.perform(requestPost).andExpect(status().isOk()).andReturn();

            Assertions.assertThat(responsePost.getResponse().getContentAsString()).isEmpty();

            // vorher gesetzten Wahltag lesen -> OK zurück mit empty body weil kein aktiver Wahltag gefunden
            val requestGet = MockMvcRequestBuilders.get("/businessActions/konfigurierterWahltag");
            val responseGet = api.perform(requestGet).andExpect(status().isNoContent()).andReturn();

            Assertions.assertThat(responseGet.getResponse().getContentAsString()).isEmpty();
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_POST_KONFIGURIERTERWAHLTAG, Authorities.REPOSITORY_WRITE_KONFIGURIERTERWAHLTAG,
                        Authorities.SERVICE_GET_KONFIGURIERTERWAHLTAG, Authorities.REPOSITORY_READ_KONFIGURIERTERWAHLTAG }
        )
        void overrideAktiverWahltagAndReadOKWithContent() throws Exception {
            // DB hat aktiven Wahltag, überschreiben mit neuem Wahltag -> -> OK zurück mit empty body
            val konfigurierterWahltag1 = new KonfigurierterWahltag(LocalDate.now(), "1-2-3", WahltagStatus.INAKTIV, "4711");
            val konfigurierterWahltag2 = new KonfigurierterWahltag(LocalDate.now(), "3-4-5", WahltagStatus.INAKTIV, "0190");
            val konfigurierterWahltag3 = new KonfigurierterWahltag(LocalDate.now(), "6-7-8-", WahltagStatus.AKTIV, "8888");
            val konfigurierterWahltagExpected = new KonfigurierterWahltag(LocalDate.now(), "9-10-11", WahltagStatus.AKTIV, "0103");

            konfigurierterWahltagRepository.save(konfigurierterWahltag1);
            konfigurierterWahltagRepository.save(konfigurierterWahltag2);
            konfigurierterWahltagRepository.save(konfigurierterWahltag3);

            val konfigurierterWahltagPostDTO = new KonfigurierterWahltagDTO(konfigurierterWahltagExpected.getWahltag(),
                    konfigurierterWahltagExpected.getWahltagID(),
                    konfigurierterWahltagExpected.getWahltagStatus(), konfigurierterWahltagExpected.getNummer());
            val requestPost = createPostWithBody(konfigurierterWahltagPostDTO);

            val responsePost = api.perform(requestPost).andExpect(status().isOk()).andReturn();

            Assertions.assertThat(responsePost.getResponse().getContentAsString()).isEmpty();

            // überschriebenen Wahltag aus DB lesen -> OK zurück mit vorher gesetztem body
            val requestGet = MockMvcRequestBuilders.get("/businessActions/konfigurierterWahltag");
            val responseGet = api.perform(requestGet).andExpect(status().isOk()).andReturn();
            val responseGetBody = objectMapper.readValue(responseGet.getResponse().getContentAsString(), KonfigurierterWahltagDTO.class);

            val expectedResponseGetBody = new KonfigurierterWahltagDTO(konfigurierterWahltagPostDTO.wahltag(), konfigurierterWahltagPostDTO.wahltagID(),
                    konfigurierterWahltagPostDTO.wahltagStatus(), konfigurierterWahltagPostDTO.nummer());

            Assertions.assertThat(responseGetBody).isEqualTo(expectedResponseGetBody);
        }

    }

    // Dooffall: DB hat nur inaktive Wahltage, neu setzen mit WahltagStatus.INAKTIV -> kein Wahltag
    private MockHttpServletRequestBuilder createPostWithBody(final KonfigurierterWahltagDTO requestDTO) throws Exception {
        return MockMvcRequestBuilders.post("/businessActions/konfigurierterWahltag").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO));
    }

    @Nested
    class GetKonfigurierteWahltage {

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_GET_KONFIGURIERTEWAHLTAGE, Authorities.REPOSITORY_READ_KONFIGURIERTEWAHLTAGE })
        void emptyResponse() throws Exception {
            val request = MockMvcRequestBuilders.get("/businessActions/konfigurierteWahltage");

            val response = api.perform(request).andExpect(status().isOk()).andReturn();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEqualTo("[]");
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_GET_KONFIGURIERTEWAHLTAGE, Authorities.REPOSITORY_READ_KONFIGURIERTEWAHLTAGE,
                        Authorities.REPOSITORY_WRITE_KONFIGURIERTERWAHLTAG }
        )
        void dataFound() throws Exception {

            val konfigurierterWahltag1 = new KonfigurierterWahltag(LocalDate.now(), "1-2-3", WahltagStatus.INAKTIV, "4711");
            val konfigurierterWahltag2 = new KonfigurierterWahltag(LocalDate.now(), "3-4-5", WahltagStatus.INAKTIV, "0190");
            val konfigurierterWahltag3 = new KonfigurierterWahltag(LocalDate.now(), "6-7-8", WahltagStatus.AKTIV, "0103");

            konfigurierterWahltagRepository.save(konfigurierterWahltag1);
            konfigurierterWahltagRepository.save(konfigurierterWahltag2);
            konfigurierterWahltagRepository.save(konfigurierterWahltag3);

            val request = MockMvcRequestBuilders.get("/businessActions/konfigurierteWahltage");

            val response = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyDTO = objectMapper.readValue(response.getResponse().getContentAsString(), KonfigurierterWahltagDTO[].class);

            val expectedResponseBodyDTO = new KonfigurierterWahltagDTO[] {
                    new KonfigurierterWahltagDTO(konfigurierterWahltag1.getWahltag(), konfigurierterWahltag1.getWahltagID(),
                            konfigurierterWahltag1.getWahltagStatus(), konfigurierterWahltag1.getNummer()),
                    new KonfigurierterWahltagDTO(konfigurierterWahltag2.getWahltag(), konfigurierterWahltag2.getWahltagID(),
                            konfigurierterWahltag2.getWahltagStatus(), konfigurierterWahltag2.getNummer()),
                    new KonfigurierterWahltagDTO(konfigurierterWahltag3.getWahltag(), konfigurierterWahltag3.getWahltagID(),
                            konfigurierterWahltag3.getWahltagStatus(), konfigurierterWahltag3.getNummer())
            };

            Assertions.assertThat(responseBodyDTO).isEqualTo(expectedResponseBodyDTO);
        }
    }

    @Nested
    class isWahltagActive {

        @Test
        @WithMockUser(authorities = {})
        void isFalseWhenWahltagNotFound() throws Exception {
            val request = MockMvcRequestBuilders.get("/businessActions/loginCheck/5555");

            val response = api.perform(request).andExpect(status().isOk()).andReturn();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEqualTo("false");
        }

        @Test
        @WithMockUser(authorities = { Authorities.REPOSITORY_WRITE_KONFIGURIERTERWAHLTAG })
        void isInactiveFound() throws Exception {

            val konfigurierterWahltag1 = new KonfigurierterWahltag(LocalDate.now(), "1-2-3", WahltagStatus.INAKTIV, "4711");
            val konfigurierterWahltag2 = new KonfigurierterWahltag(LocalDate.now(), "3-4-5", WahltagStatus.INAKTIV, "0190");
            val konfigurierterWahltag3 = new KonfigurierterWahltag(LocalDate.now(), "6-7-8", WahltagStatus.AKTIV, "0103");

            konfigurierterWahltagRepository.save(konfigurierterWahltag1);
            konfigurierterWahltagRepository.save(konfigurierterWahltag2);
            konfigurierterWahltagRepository.save(konfigurierterWahltag3);

            // Wahltag inaktiv
            val request = MockMvcRequestBuilders.get("/businessActions/loginCheck/1-2-3");
            val response = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBody = objectMapper.readValue(response.getResponse().getContentAsString(), String.class);
            val expectedResponseBody = "false";

            Assertions.assertThat(responseBody).isEqualTo(expectedResponseBody);

        }

        @Test
        @WithMockUser(authorities = { Authorities.REPOSITORY_WRITE_KONFIGURIERTERWAHLTAG })
        void isActiveFound() throws Exception {

            val konfigurierterWahltag1 = new KonfigurierterWahltag(LocalDate.now(), "1-2-3", WahltagStatus.INAKTIV, "4711");
            val konfigurierterWahltag2 = new KonfigurierterWahltag(LocalDate.now(), "3-4-5", WahltagStatus.INAKTIV, "0190");
            val konfigurierterWahltag3 = new KonfigurierterWahltag(LocalDate.now(), "6-7-8", WahltagStatus.AKTIV, "0103");

            konfigurierterWahltagRepository.save(konfigurierterWahltag1);
            konfigurierterWahltagRepository.save(konfigurierterWahltag2);
            konfigurierterWahltagRepository.save(konfigurierterWahltag3);

            val request = MockMvcRequestBuilders.get("/businessActions/loginCheck/6-7-8");
            val response = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBody = objectMapper.readValue(response.getResponse().getContentAsString(), String.class);
            val expectedResponseBody = "true";

            Assertions.assertThat(responseBody).isEqualTo(expectedResponseBody);
        }
    }
}
