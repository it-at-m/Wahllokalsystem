package de.muenchen.oss.wahllokalsystem.briefwahlservice.configuration;

import static de.muenchen.oss.wahllokalsystem.briefwahlservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.rest.beanstandetewahlbriefe.BeanstandeteWahlbriefeCreateDTO;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.rest.beanstandetewahlbriefe.BeanstandeteWahlbriefeDTO;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.rest.wahlbriefdaten.WahlbriefdatenWriteDTO;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.service.beanstandetewahlbriefe.BeanstandeteWahlbriefeModel;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.service.beanstandetewahlbriefe.BeanstandeteWahlbriefeService;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.service.wahlbriefdaten.WahlbriefdatenService;
import java.util.HashMap;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureObservability
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE })
class SecurityConfigurationTest {

    @Autowired
    MockMvc api;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BeanstandeteWahlbriefeService beanstandeteWahlbriefeService;

    @MockBean
    WahlbriefdatenService wahlbriefdatenService;

    @Test
    void accessSecuredResourceRootThenUnauthorized() throws Exception {
        api.perform(get("/"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessSecuredResourceActuatorThenUnauthorized() throws Exception {
        api.perform(get("/actuator"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessUnsecuredResourceActuatorHealthThenOk() throws Exception {
        api.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    void accessUnsecuredResourceActuatorInfoThenOk() throws Exception {
        api.perform(get("/actuator/info"))
                .andExpect(status().isOk());
    }

    @Test
    void accessUnsecuredResourceActuatorMetricsThenOk() throws Exception {
        api.perform(get("/actuator/metrics"))
                .andExpect(status().isOk());
    }

    @Test
    void accessUnsecuredResourceV3ApiDocsThenOk() throws Exception {
        api.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    void accessUnsecuredResourceSwaggerUiThenOk() throws Exception {
        api.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Nested
    class BeanstandeteWahlbriefe {

        @Test
        @WithAnonymousUser
        void accessGetBeanstandeteWahlbriefeUnauthorizedThenUnauthorized() throws Exception {
            api.perform(get("/businessActions/beanstandeteWahlbriefe/wahlbezirkID/2")).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        void accessGetBeanstandeteWahlbriefeAuthorizedThenOk() throws Exception {
            val wahlbezirkID = "wahlbezirkID";
            val waehlerverzeichnisNummer = 13L;
            val serviceResponse = new BeanstandeteWahlbriefeModel(wahlbezirkID, waehlerverzeichnisNummer, new HashMap<>());

            Mockito.when(beanstandeteWahlbriefeService.getBeanstandeteWahlbriefe(Mockito.any())).thenReturn(serviceResponse);

            val expectedBeanstandeteWahlbriefeDTO = new BeanstandeteWahlbriefeDTO(wahlbezirkID, waehlerverzeichnisNummer, new HashMap<>());

            val result = api.perform(get("/businessActions/beanstandeteWahlbriefe/wahlbezirkID/2")).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(result.getResponse().getContentAsString(), BeanstandeteWahlbriefeDTO.class);

            Assertions.assertThat(responseBodyAsDTO).usingRecursiveComparison().isEqualTo(expectedBeanstandeteWahlbriefeDTO);

        }

        @Test
        @WithAnonymousUser
        void accessPostBeanstandeteWahlbriefeUnauthorizedThenUnauthorized() throws Exception {
            val requestBodyAsString = objectMapper.writeValueAsString(new BeanstandeteWahlbriefeCreateDTO(new HashMap<>()));
            val request = post(
                    "/businessActions/beanstandeteWahlbriefe/wahlbezirkID/2").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(requestBodyAsString);

            api.perform(request).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        void accessPostBeanstandeteWahlbriefeAuthorizedThenUnauthorized() throws Exception {
            val requestBodyAsString = objectMapper.writeValueAsString(new BeanstandeteWahlbriefeCreateDTO(new HashMap<>()));
            val request = post("/businessActions/beanstandeteWahlbriefe/wahlbezirkID/2").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                    .content(requestBodyAsString);

            Mockito.doNothing().when(beanstandeteWahlbriefeService).setBeanstandeteWahlbriefe(Mockito.any());

            api.perform(request).andExpect(status().isOk());
        }
    }

    @Nested
    class Wahlbriefdaten {

        @Test
        @WithAnonymousUser
        void accessGetWahlbriefdatenUnauthorizedThenUnauthorized() throws Exception {
            val request = get("/businessActions/wahlbriefdaten/wahlbezirkID");

            api.perform(request).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        void accessGetWahlbriefdatenAuthorizedThenNoContent() throws Exception {
            val request = get("/businessActions/wahlbriefdaten/wahlbezirkID");

            api.perform(request).andExpect(status().isNoContent());
        }

        @Test
        @WithAnonymousUser
        void accessSetWahlbriefdatenUnauthorizedThenUnauthorized() throws Exception {
            val requestBody = new WahlbriefdatenWriteDTO(null, null, null, null, null);
            val request = post("/businessActions/wahlbriefdaten/wahlbezirkID").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(requestBody));

            api.perform(request).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        void accessSetWahlbriefdatenAuthorizedThenOk() throws Exception {
            val requestBody = new WahlbriefdatenWriteDTO(null, null, null, null, null);
            val request = post("/businessActions/wahlbriefdaten/wahlbezirkID").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(requestBody));

            api.perform(request).andExpect(status().isOk());
        }
    }

}
