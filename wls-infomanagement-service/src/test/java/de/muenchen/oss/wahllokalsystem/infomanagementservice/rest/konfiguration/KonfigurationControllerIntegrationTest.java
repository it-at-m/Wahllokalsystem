package de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.konfiguration;

import static de.muenchen.oss.wahllokalsystem.infomanagementservice.TestConstants.SPRING_TEST_PROFILE;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.konfiguration.Konfiguration;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.konfiguration.KonfigurationRepository;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.KonfigurationModelValidator;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.utils.SecurityUtils;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.utils.WithMockUserAsJwt;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE })
public class KonfigurationControllerIntegrationTest {

    @Autowired
    MockMvc api;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KonfigurationRepository konfigurationRepository;

    @SpyBean
    KonfigurationModelValidator konfigurationModelValidator;

    @AfterEach
    void tearDown() {
        SecurityUtils.runAs("", "", Authorities.REPOSITORY_DELETE_KONFIGURATION);
        konfigurationRepository.deleteAll();
    }

    @Nested
    class GetKonfiguration {

        @Test
        @WithMockUserAsJwt(authorities = { Authorities.SERVICE_GET_KONFIGURATION, Authorities.REPOSITORY_READ_KONFIGURATION })
        void emptyResponse() throws Exception {
            val request = MockMvcRequestBuilders.get("/businessActions/konfiguration/WILLKOMMENSTEXT");

            val response = api.perform(request).andExpect(status().isNoContent()).andReturn();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();
        }

        @Test
        @WithMockUserAsJwt(
                authorities = { Authorities.SERVICE_GET_KONFIGURATION, Authorities.REPOSITORY_READ_KONFIGURATION, Authorities.REPOSITORY_WRITE_KONFIGURATION }
        )
        void dataFound() throws Exception {
            val konfiguration1 = new Konfiguration("schluessel1", "wert1", "beschreibung1", "standardwert1");
            val konfiguration2 = new Konfiguration("schluessel2", "wert2", "beschreibung2", "standardwert2");
            val konfigurationToFind = new Konfiguration("WILLKOMMENSTEXT", "hello world", "A long time ago in a galaxy far, far away", "a new hope");

            konfigurationRepository.save(konfiguration1);
            konfigurationRepository.save(konfiguration2);
            konfigurationRepository.save(konfigurationToFind);

            val request = MockMvcRequestBuilders.get("/businessActions/konfiguration/WILLKOMMENSTEXT");

            val response = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBody = objectMapper.readValue(response.getResponse().getContentAsString(), KonfigurationDTO.class);

            val expectedResponseBody = new KonfigurationDTO(konfigurationToFind.getSchluessel(), konfigurationToFind.getWert(),
                    konfigurationToFind.getBeschreibung(), konfigurationToFind.getStandardwert());

            Assertions.assertThat(responseBody).isEqualTo(expectedResponseBody);
        }
    }

    @Nested
    class PostKonfiguration {

        @Test
        @WithMockUserAsJwt(
                authorities = { Authorities.SERVICE_POST_KONFIGURATION, Authorities.REPOSITORY_WRITE_KONFIGURATION, Authorities.REPOSITORY_READ_KONFIGURATION }
        )
        void newDataSuccessfullySaved() throws Exception {
            val konfigurationKey = "WILLKOMMENSTEXT";
            val requestBody = new KonfigurationSetDTO("wert", "beschreibung", "standard");
            val request = createPostWithBody(konfigurationKey, requestBody);

            val response = api.perform(request).andExpect(status().isOk()).andReturn();
            SecurityUtils.runAs("", "", Authorities.REPOSITORY_READ_KONFIGURATION);
            val savedKonfiguration = konfigurationRepository.findById(konfigurationKey).get();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();

            val expectedSavedKonfiguration = new Konfiguration(konfigurationKey, requestBody.wert(), requestBody.beschreibung(), requestBody.standardwert());
            Assertions.assertThat(savedKonfiguration).isEqualTo(expectedSavedKonfiguration);
        }

        @Test
        @WithMockUserAsJwt(
                authorities = { Authorities.SERVICE_POST_KONFIGURATION, Authorities.REPOSITORY_WRITE_KONFIGURATION, Authorities.REPOSITORY_READ_KONFIGURATION }
        )
        void oldDataOverriden() throws Exception {
            val konfigurationKey = "WILLKOMMENSTEXT";
            val requestBody = new KonfigurationSetDTO("wert", "beschreibung", "standard");
            val request = createPostWithBody(konfigurationKey, requestBody);

            val konfigurationToOverride = new Konfiguration(konfigurationKey, "old value", "old description", "old default");
            konfigurationRepository.save(konfigurationToOverride);

            api.perform(request).andExpect(status().isOk());
            SecurityUtils.runAs("", "", Authorities.REPOSITORY_READ_KONFIGURATION);
            val savedKonfiguration = konfigurationRepository.findById(konfigurationKey).get();

            val expectedSavedKonfiguration = new Konfiguration(konfigurationKey, requestBody.wert(), requestBody.beschreibung(), requestBody.standardwert());
            Assertions.assertThat(savedKonfiguration).isEqualTo(expectedSavedKonfiguration);
        }

        @Test
        @WithMockUserAsJwt(
                authorities = { Authorities.SERVICE_POST_KONFIGURATION, Authorities.REPOSITORY_WRITE_KONFIGURATION }
        )
        void validationExceptionOccurredAndIsMapped() throws Exception {
            val requestBody = new KonfigurationSetDTO(null, "beschreibung", "standard");
            val request = createPostWithBody("WILLKOMMENSTEXT", requestBody);

            val mockedExceptionMessage = "mocked null pointer exception";
            val mockedValidationException = new NullPointerException(mockedExceptionMessage);
            Mockito.doThrow(mockedValidationException).when(konfigurationModelValidator).valideOrThrowSetKonfiguration(any());

            val response = api.perform(request).andExpect(status().isInternalServerError()).andReturn();
            val responseBodyDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.T, "999", "WLS-INFOMANAGEMENT", "");

            Assertions.assertThat(responseBodyDTO).usingRecursiveComparison().ignoringFields("message").isEqualTo(expectedWlsExceptionDTO);
            Assertions.assertThat(responseBodyDTO.message()).contains(mockedExceptionMessage);
        }

        private MockHttpServletRequestBuilder createPostWithBody(final String konfigurationKey, final KonfigurationSetDTO requestDTO) throws Exception {
            return MockMvcRequestBuilders.post("/businessActions/konfiguration/" + konfigurationKey).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                    .content(
                            objectMapper.writeValueAsString(requestDTO));
        }
    }

    @Nested
    class GetKonfigurationen {
        @Test
        @WithMockUserAsJwt(
                authorities = { Authorities.SERVICE_GET_KONFIUGRATIONEN, Authorities.REPOSITORY_READ_KONFIGURATION, Authorities.REPOSITORY_WRITE_KONFIGURATION }
        )
        void konfigurationDataFound() throws Exception {
            konfigurationRepository.save(new Konfiguration("schluessel1", "wert1", "beschreibung1", "standard1"));
            konfigurationRepository.save(new Konfiguration("schluessel2", "wert2", "beschreibung2", "standard2"));

            val request = MockMvcRequestBuilders.get("/businessActions/konfiguration");

            val response = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyDTO = objectMapper.readValue(response.getResponse().getContentAsString(), KonfigurationDTO[].class);

            val expectedResponseItems = new KonfigurationDTO[] {
                    new KonfigurationDTO("schluessel1", "wert1", "beschreibung1", "standard1"),
                    new KonfigurationDTO("schluessel2", "wert2", "beschreibung2", "standard2"),
            };

            Assertions.assertThat(responseBodyDTO).containsExactlyInAnyOrder(expectedResponseItems);
        }
    }

    @Nested
    class GetKennbuchstabenListen {

        @Test
        @WithMockUserAsJwt(
                authorities = { Authorities.SERVICE_GET_KENNBUCHSTABEN_LISTEN, Authorities.REPOSITORY_READ_KONFIGURATION,
                        Authorities.REPOSITORY_WRITE_KONFIGURATION }
        )
        void kennbuchstabenFoundDefaultSettings() throws Exception {
            konfigurationRepository.save(new Konfiguration("KENNBUCHSTABEN", "a,b, c;A,B,C$1,2;11,12", "", ""));

            val request = MockMvcRequestBuilders.get("/businessActions/kennbuchstaben");

            val response = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyDTO = objectMapper.readValue(response.getResponse().getContentAsString(), KennbuchstabenListenDTO.class);

            val expectedResponseDTO = new KennbuchstabenListenDTO(List.of(new KennbuchstabenListeDTO(
                    List.of(new KennbuchstabenDTO(List.of("a", "b", " c")), new KennbuchstabenDTO(List.of("A", "B", "C")))
            ), new KennbuchstabenListeDTO(List.of(new KennbuchstabenDTO(List.of("1", "2")), new KennbuchstabenDTO(List.of("11", "12"))))));

            Assertions.assertThat(responseBodyDTO).isEqualTo(expectedResponseDTO);
        }
    }
}
