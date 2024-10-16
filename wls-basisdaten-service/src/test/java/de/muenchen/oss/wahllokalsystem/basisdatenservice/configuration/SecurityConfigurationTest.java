package de.muenchen.oss.wahllokalsystem.basisdatenservice.configuration;

import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.handbuch.HandbuchService;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlagen.ReferendumvorlagenService;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.ungueltigewahlscheine.UngueltigeWahlscheineService;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag.WahltageService;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltermindaten.WahltermindatenService;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlag.WahlvorschlaegeService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

    @MockBean
    WahlvorschlaegeService wahlvorschlaegeService;

    @MockBean
    WahltageService wahltageService;

    @MockBean
    HandbuchService handbuchService;

    @MockBean
    UngueltigeWahlscheineService ungueltigeWahlscheineService;

    @MockBean
    ReferendumvorlagenService referendumvorlagenService;

    @MockBean
    WahltermindatenService wahltermindatenService;

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
    class Wahlvorschlaege {

        @Test
        @WithAnonymousUser
        void accessGetWahlvorstaendeUnauthorizedThenUnauthorized() throws Exception {
            api.perform(get("/businessActions/wahlvorschlaege/wahlID/wahlbezirkID")).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        void accessGetWahlvorschlaegeUnauthorizedThenOk() throws Exception {
            api.perform(get("/businessActions/wahlvorschlaege/wahlID/wahlbezirkID")).andExpect(status().isOk());
        }
    }

    @Nested
    class Wahltage {

        @Test
        @WithAnonymousUser
        void accessGetWahltageUnauthorizedThenUnauthorized() throws Exception {
            api.perform(get("/businessActions/wahltage")).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        void accessGetWahltageUnauthorizedThenOk() throws Exception {
            api.perform(get("/businessActions/wahltage")).andExpect(status().isOk());
        }
    }

    @Nested
    class Handbuch {

        @Test
        @WithAnonymousUser
        void accessGetHandbuchUnauthorizedThenUnauthorized() throws Exception {
            api.perform(get("/businessActions/handbuch/wahlID/UWB")).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        void accessGetHandbuchAuthorizedThenOk() throws Exception {
            api.perform(get("/businessActions/handbuch/wahlID/UWB")).andExpect(status().isOk());
        }

        @Test
        @WithAnonymousUser
        void accessPostHandbuchUnauthorizedThenUnauthorized() throws Exception {
            api.perform(post("/businessActions/handbuch/wahlID/UWB").with(csrf())).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        void accessPostHandbuchAuthorizedThenOk() throws Exception {
            api.perform(multipart("/businessActions/handbuch/wahlID/UWB").file("manual", "content".getBytes()).with(csrf())).andExpect(status().isOk());
        }
    }

    @Nested
    class UngueltigeWahlscheine {

        @Test
        @WithAnonymousUser
        void accessGetUngueltigeWahlscheineUnauthorizedThenUnauthorized() throws Exception {
            api.perform(get("/businessActions/ungueltigews/wahlID/UWB")).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        void accessGetUngueltigeWahlscheineAuthorizedThenOk() throws Exception {
            api.perform(get("/businessActions/ungueltigews/wahlID/UWB")).andExpect(status().isOk());
        }

        @Test
        @WithAnonymousUser
        void accessPostUngueltigeWahlscheineUnauthorizedThenUnauthorized() throws Exception {
            api.perform(post("/businessActions/ungueltigews/wahlID/UWB").with(csrf())).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        void accessPostUngueltigeWahlscheineAuthorizedThenOk() throws Exception {
            api.perform(multipart("/businessActions/ungueltigews/wahlID/UWB").file("manual", "content".getBytes()).with(csrf())).andExpect(status().isOk());
        }
    }

    @Nested
    class Referendumvorlagen {

        @Test
        @WithAnonymousUser
        void accessGetReferendumvorlagenUnauthorizedThenUnauthorized() throws Exception {
            api.perform(get("/businessActions/referendumvorlagen/wahlID/wahlbezirkID")).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        void accessGetReferendumvorlagenAuthorizedThenOk() throws Exception {
            api.perform(get("/businessActions/referendumvorlagen/wahlID/wahlbezirkID")).andExpect(status().isOk());
        }
    }

    @Nested
    class Wahltermindaten {

        @Test
        @WithAnonymousUser
        void should_denyAccess_when_accessingUnauthorizedViaGet() throws Exception {
            api.perform(put("/businessActions/wahltermindaten/wahlID").with(csrf())).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        void should_permitAccess_when_accessingAuthorizedViaGet() throws Exception {
            api.perform(put("/businessActions/wahltermindaten/wahlID").with(csrf())).andExpect(status().isOk());
        }

        @Test
        @WithAnonymousUser
        void should_denyAccess_when_accessingUnauthorizedViaDelete() throws Exception {
            api.perform(delete("/businessActions/wahltermindaten/wahlID").with(csrf())).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        void should_permitAccess_when_accessingAuthorizedDelete() throws Exception {
            api.perform(delete("/businessActions/wahltermindaten/wahlID").with(csrf())).andExpect(status().isOk());
        }
    }

    @Nested
    class AsyncProgress {
        @Test
        @WithAnonymousUser
        void should_denyAccess_when_requestWithAnonymousUser() throws Exception {
            api.perform(get("/businessActions/asyncProgress")).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        void should_permitAccess_when_requestWithAuthorizedUser() throws Exception {
            api.perform(get("/businessActions/asyncProgress")).andExpect(status().isOk());
        }
    }

}
