package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.configuration;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ergebnisse.BezirkUndWahlIDStapelartDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ergebnisse.ErgebnisDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ergebnisse.ErgebnisseDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ergebnisse.StapelartDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.status.MeldungDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.status.StatusDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.status.ValidierungsstatusDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke.StimmabgabevermerkeDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke.WahldatenDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.StatusService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.StimmabgabevermerkeService;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import lombok.val;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureObservability
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE })
class SecurityConfigurationTest {

    @MockBean
    StatusService statusService;

    @MockBean
    StimmabgabevermerkeService stimmabgabevermerkeService;

    @MockBean
    ErgebnisseService ergebnisseService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc api;

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
    class Status {

        @Nested
        class GetStatus {

            @WithAnonymousUser
            @Test
            void should_returnUnauthorized_when_callingAnonymous() throws Exception {
                val request = MockMvcRequestBuilders.get("/businessActions/status/wahlID/wahlbezirkID");

                api.perform(request).andExpect(status().isUnauthorized());
            }

            @WithMockUser
            @Test
            void should_returnNoContent_when_callingAuthenticated() throws Exception {
                val request = MockMvcRequestBuilders.get("/businessActions/status/wahlID/wahlbezirkID");

                api.perform(request).andExpect(status().isNoContent());

                Mockito.verify(statusService).getStatus(notNull());
            }
        }

        @Nested
        class PostStatus {

            @WithAnonymousUser
            @Test
            void should_returnUnauthorized_when_callingAnonymous() throws Exception {
                val requestBody = new StatusDTO(new BezirkUndWahlID("wahlID", "wahlbezirkID"),
                        new MeldungDTO(ValidierungsstatusDTO.VALIDE, true, true, LocalDateTime.now()),
                        new MeldungDTO(ValidierungsstatusDTO.VALIDE, true, true, LocalDateTime.now()));
                val request = MockMvcRequestBuilders.post("/businessActions/status/wahlID/wahlbezirkID").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody));

                api.perform(request).andExpect(status().isUnauthorized());
            }

            @WithMockUser
            @Test
            void should_returnNoContent_when_callingAuthenticated() throws Exception {
                val requestBody = new StatusDTO(new BezirkUndWahlID("wahlID", "wahlbezirkID"),
                        new MeldungDTO(ValidierungsstatusDTO.VALIDE, true, true, LocalDateTime.now()),
                        new MeldungDTO(ValidierungsstatusDTO.VALIDE, true, true, LocalDateTime.now()));
                val request = MockMvcRequestBuilders.post("/businessActions/status/wahlID/wahlbezirkID").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody));

                api.perform(request).andExpect(status().isOk());

                Mockito.verify(statusService).setStatus(notNull(), notNull());
            }
        }
    }

    @Nested
    class AsyncProgress {

        @WithAnonymousUser
        @Test
        void should_returnUnauthorized_when_callingGetAnonymous() throws Exception {
            val request = MockMvcRequestBuilders.get("/businessActions/asyncProgress");

            api.perform(request).andExpect(status().isUnauthorized());
        }

        @WithMockUser
        @Test
        void should_returnOk_when_callingAuthenticated() throws Exception {
            val request = MockMvcRequestBuilders.get("/businessActions/asyncProgress");

            api.perform(request).andExpect(status().isOk());
        }
    }

    @Nested
    class Stimmabgabevermerke {

        @WithAnonymousUser
        @Test
        void should_returnUnauthorized_when_callingGetAnonymous() throws Exception {
            val request = MockMvcRequestBuilders.get("/businessActions/stimmabgabevermerke/wbzID/1");

            api.perform(request).andExpect(status().isUnauthorized());
        }

        @WithMockUser
        @Test
        void should_returnNoContent_when_callingGetAuthenticated() throws Exception {
            val request = MockMvcRequestBuilders.get("/businessActions/stimmabgabevermerke/wbzID/1");

            api.perform(request).andExpect(status().isNoContent());

            Mockito.verify(stimmabgabevermerkeService).getStimmabgabevermerke(any());
        }

        @WithAnonymousUser
        @Test
        void should_returnUnauthorized_when_callingSetAnonymous() throws Exception {
            val request = MockMvcRequestBuilders.post("/businessActions/stimmabgabevermerke/wbzID/1").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(new StimmabgabevermerkeDTO("wahlbezirkID", 0L, 1,
                            Set.of(new WahldatenDTO("wahlbezirkID", "wahlID", 0L, Collections.emptySet(), Collections.emptySet())))));

            api.perform(request).andExpect(status().isUnauthorized());
        }

        @WithMockUser
        @Test
        void should_returnOk_when_callingSetAuthenticated() throws Exception {
            val request = MockMvcRequestBuilders.post("/businessActions/stimmabgabevermerke/wbzID/1").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(new StimmabgabevermerkeDTO("wahlbezirkID", 0L, 1,
                            Set.of(new WahldatenDTO("wahlbezirkID", "wahlID", 0L, Collections.emptySet(), Collections.emptySet())))));

            api.perform(request).andExpect(status().isOk());

            Mockito.verify(stimmabgabevermerkeService).postStimmabgabevermerke(any(), any());
        }
    }

    @Nested
    class Ergebnisse {

        @Nested
        class GetErgebnisse {

            @WithMockUser
            @Test
            void should_returnNoContent_when_userIsAuthenticated() throws Exception {
                val request = MockMvcRequestBuilders.get("/businessActions/ergebnisse/wahlID/wahlbezirkID/LTW_BZW_A");

                api.perform(request).andExpect(status().isNoContent()).andReturn();

                Mockito.verify(ergebnisseService).getErgebnisse(notNull());
            }

            @WithAnonymousUser
            @Test
            void should_returnUnauthorized_when_userIsAnonymous() throws Exception {
                val request = MockMvcRequestBuilders.get("/businessActions/ergebnisse/wahlID/wahlbezirkID/LTW_BZW_A");

                api.perform(request).andExpect(status().isUnauthorized()).andReturn();
            }
        }

        @Nested
        class PostErgebnisse {

            @WithMockUser
            @Test
            void should_returnOk_when_userIsAuthenticated() throws Exception {
                val ergebnis1 = new ErgebnisDTO(null, null, null, 1, null);
                val newErgebnisDTOList = new ArrayList<ErgebnisDTO>();
                newErgebnisDTOList.add(ergebnis1);

                val ergebnisse = new ErgebnisseDTO(new BezirkUndWahlIDStapelartDTO("wahlbezirkID", "wahlID", StapelartDTO.BTW_A), newErgebnisDTOList);
                val request = MockMvcRequestBuilders.post("/businessActions/ergebnisse/wahlID/wahlbezirkID/LTW_BZW_A").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ergebnisse));
                api.perform(request).andExpect(status().isOk()).andReturn();

                Mockito.verify(ergebnisseService).postErgebnisse(any(), any());
            }

            @WithAnonymousUser
            @Test
            void should_returnUnauthorized_when_userIsAnonymous() throws Exception {
                val ergebnis1 = new ErgebnisDTO(null, null, null, 1, null);
                val newErgebnisDTOList = new ArrayList<ErgebnisDTO>();
                newErgebnisDTOList.add(ergebnis1);

                val ergebnisse = new ErgebnisseDTO(new BezirkUndWahlIDStapelartDTO("wahlbezirkID", "wahlID", StapelartDTO.BTW_A), newErgebnisDTOList);
                val request = MockMvcRequestBuilders.post("/businessActions/ergebnisse/wahlID/wahlbezirkID/LTW_BZW_A").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ergebnisse));

                api.perform(request).andExpect(status().isUnauthorized()).andReturn();
            }
        }

        @Nested
        class GetAllErgebnisse {

            @WithMockUser
            @Test
            void should_returnNoContent_when_userIsAuthenticated() throws Exception {
                val request = MockMvcRequestBuilders.get("/businessActions/ergebnisse/wahlID/wahlbezirkID");

                api.perform(request).andExpect(status().isNoContent()).andReturn();

                Mockito.verify(ergebnisseService).getAllErgebnisse(notNull(), notNull());
            }

            @WithAnonymousUser
            @Test
            void should_returnUnauthorized_when_userIsAnonymous() throws Exception {
                val request = MockMvcRequestBuilders.get("/businessActions/ergebnisse/wahlID/wahlbezirkID");

                api.perform(request).andExpect(status().isUnauthorized()).andReturn();
            }
        }
    }
}
