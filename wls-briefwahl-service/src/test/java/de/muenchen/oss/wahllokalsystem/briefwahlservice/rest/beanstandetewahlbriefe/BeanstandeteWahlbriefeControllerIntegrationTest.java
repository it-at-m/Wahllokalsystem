package de.muenchen.oss.wahllokalsystem.briefwahlservice.rest.beanstandetewahlbriefe;

import static de.muenchen.oss.wahllokalsystem.briefwahlservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.common.beanstandetewahlbriefe.Zurueckweisungsgrund;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.domain.BeanstandeteWahlbriefe;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.domain.BeanstandeteWahlbriefeRepository;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.test.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.test.utils.SecurityUtils;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import java.util.Map;
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

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, "dummy.nobezirkid.check" })
public class BeanstandeteWahlbriefeControllerIntegrationTest {

    @Autowired
    MockMvc api;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    BeanstandeteWahlbriefeRepository beanstandeteWahlbriefeRepository;

    @Nested
    class GetBeanstandeteWahlbriefe {

        @AfterEach
        void setup() {
            SecurityUtils.runAs("", "", Authorities.REPOSITORY_DELETE_BEANSTANDETE_WAHLBRIEFE);
            beanstandeteWahlbriefeRepository.deleteAll();
        }

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_GET_BEANSTANDETE_WAHLBRIEFE, Authorities.REPOSITORY_READ_BEANSTANDETE_WAHLBRIEFE })
        void emptyResponse() throws Exception {
            val request = get("/businessActions/beanstandeteWahlbriefe/wahlbezirkID/21");

            val response = api.perform(request).andExpect(status().isNoContent()).andReturn();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_GET_BEANSTANDETE_WAHLBRIEFE, Authorities.REPOSITORY_READ_BEANSTANDETE_WAHLBRIEFE,
                        Authorities.REPOSITORY_WRITE_BEANSTANDETE_WAHLBRIEFE }
        )
        void dataFound() throws Exception {
            val wahlbezirkID1 = "wahlbezirkID1";
            val wahlbezirkID2 = "wahlbezirkID2";

            val waehlerverzeichnissNummer1 = 1L;
            val waehlerverzeichnissNummer2 = 2L;

            val beanstandeteWahlbriefe1 = new BeanstandeteWahlbriefe();
            beanstandeteWahlbriefe1.setBezirkIDUndWaehlerverzeichnisNummer(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID1, waehlerverzeichnissNummer1));
            beanstandeteWahlbriefe1.setBeanstandeteWahlbriefe(
                    Map.of("wahl1", new Zurueckweisungsgrund[] { Zurueckweisungsgrund.ZUGELASSEN, Zurueckweisungsgrund.UNTERSCHRIFT_FEHLT }, "wahl2",
                            new Zurueckweisungsgrund[] { Zurueckweisungsgrund.NICHT_WAHLBERECHTIGT }));
            beanstandeteWahlbriefeRepository.save(beanstandeteWahlbriefe1);

            val beanstandeteWahlbriefe2 = new BeanstandeteWahlbriefe();
            beanstandeteWahlbriefe2.setBezirkIDUndWaehlerverzeichnisNummer(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID1, waehlerverzeichnissNummer2));
            beanstandeteWahlbriefeRepository.save(beanstandeteWahlbriefe2);

            val beanstandeteWahlbriefe3 = new BeanstandeteWahlbriefe();
            beanstandeteWahlbriefe3.setBezirkIDUndWaehlerverzeichnisNummer(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID2, waehlerverzeichnissNummer1));
            beanstandeteWahlbriefeRepository.save(beanstandeteWahlbriefe3);

            val expectedZurueckweisungen = Map.of("wahl1",
                    new Zurueckweisungsgrund[] { Zurueckweisungsgrund.ZUGELASSEN, Zurueckweisungsgrund.UNTERSCHRIFT_FEHLT }, "wahl2",
                    new Zurueckweisungsgrund[] { Zurueckweisungsgrund.NICHT_WAHLBERECHTIGT });
            val expectedResponse = new BeanstandeteWahlbriefeDTO(wahlbezirkID1, waehlerverzeichnissNummer1, expectedZurueckweisungen);

            val request = get("/businessActions/beanstandeteWahlbriefe/" + wahlbezirkID1 + "/" + waehlerverzeichnissNummer1);
            val response = api.perform(request).andExpect(status().isOk()).andReturn();

            val responseBody = objectMapper.readValue(response.getResponse().getContentAsString(), BeanstandeteWahlbriefeDTO.class);
            Assertions.assertThat(responseBody).usingRecursiveComparison().isEqualTo(expectedResponse);
        }

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_GET_BEANSTANDETE_WAHLBRIEFE, Authorities.REPOSITORY_READ_BEANSTANDETE_WAHLBRIEFE })
        void illegalRequestIsMappedToWlsException() throws Exception {
            val request = get("/businessActions/beanstandeteWahlbriefe/wahlbezirkID/0");

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F, "100", "WLS-BRIEFWAHL", null);

            val response = api.perform(request).andExpect(status().isBadRequest()).andReturn();
            val wlsExceptionFromResponse = objectMapper.readValue(response.getResponse().getContentAsString(), WlsExceptionDTO.class);

            Assertions.assertThat(wlsExceptionFromResponse).usingRecursiveComparison().ignoringFields("message").isEqualTo(expectedWlsExceptionDTO);
            Assertions.assertThat(wlsExceptionFromResponse.message()).isNotNull();
        }
    }

    @Nested
    class AddBeanstandeteWahlbriefe {

        @AfterEach
        void setup() {
            SecurityUtils.runAs("", "", Authorities.REPOSITORY_DELETE_BEANSTANDETE_WAHLBRIEFE);
            beanstandeteWahlbriefeRepository.deleteAll();
        }

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_ADD_BEANSTANDETE_WAHLBRIEFE, Authorities.REPOSITORY_WRITE_BEANSTANDETE_WAHLBRIEFE })
        void wlsExceptionOnInvalidRequest() throws Exception {
            val requestBody = BeanstandeteWahlbriefeCreateDTO.builder().build();
            val request = post("/businessActions/beanstandeteWahlbriefe/wahlbezirkID/0").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody));

            val expecetedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F, "101", "WLS-BRIEFWAHL", null);

            val result = api.perform(request).andExpect(status().isBadRequest()).andReturn();
            val resultBodyAsWlsExceptionDTO = objectMapper.readValue(result.getResponse().getContentAsString(), WlsExceptionDTO.class);

            Assertions.assertThat(resultBodyAsWlsExceptionDTO).usingRecursiveComparison().ignoringFields("message").isEqualTo(expecetedWlsExceptionDTO);
            Assertions.assertThat(resultBodyAsWlsExceptionDTO.message()).isNotNull();
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_ADD_BEANSTANDETE_WAHLBRIEFE, Authorities.REPOSITORY_READ_BEANSTANDETE_WAHLBRIEFE,
                        Authorities.REPOSITORY_WRITE_BEANSTANDETE_WAHLBRIEFE }
        )
        void dataAreSaved() throws Exception {
            val wahlbezirkID = "wahlbezirkID";
            val waehlerverzeichnisNummer = 89L;

            val zurueckweisungen = Map.of("wahl1",
                    new Zurueckweisungsgrund[] { Zurueckweisungsgrund.ZUGELASSEN, Zurueckweisungsgrund.UNTERSCHRIFT_FEHLT }, "wahl2",
                    new Zurueckweisungsgrund[] { Zurueckweisungsgrund.NICHT_WAHLBERECHTIGT });
            val requestBody = new BeanstandeteWahlbriefeCreateDTO(zurueckweisungen);
            val request = post("/businessActions/beanstandeteWahlbriefe/" + wahlbezirkID + "/" + waehlerverzeichnisNummer).with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody));

            val expectedRepoResponse = new BeanstandeteWahlbriefe();
            expectedRepoResponse.setBezirkIDUndWaehlerverzeichnisNummer(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer));
            expectedRepoResponse.setBeanstandeteWahlbriefe(zurueckweisungen);

            api.perform(request).andExpect(status().isOk());

            SecurityUtils.runAs("", "", Authorities.REPOSITORY_READ_BEANSTANDETE_WAHLBRIEFE);
            val repoResponse = beanstandeteWahlbriefeRepository.findById(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer))
                    .orElseThrow();

            Assertions.assertThat(repoResponse).usingRecursiveComparison().isEqualTo(expectedRepoResponse);
        }

    }
}
