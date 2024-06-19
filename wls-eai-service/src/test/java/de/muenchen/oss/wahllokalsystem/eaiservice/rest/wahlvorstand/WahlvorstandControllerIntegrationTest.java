package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand;

import static de.muenchen.oss.wahllokalsystem.eaiservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.eaiservice.Authorities;
import de.muenchen.oss.wahllokalsystem.eaiservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorstand.Wahlvorstand;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorstand.WahlvorstandFunktion;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorstand.WahlvorstandRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorstand.Wahlvorstandsmitglied;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.common.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto.WahlvorstandDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto.WahlvorstandsaktualisierungDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto.WahlvorstandsmitgliedAktualisierungDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlvorstand.WahlvorstandMapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = MicroServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE })
public class WahlvorstandControllerIntegrationTest {

    @Value("${service.info.oid}")
    String serviceInfoOid;

    @Autowired
    MockMvc api;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WahlvorstandRepository wahlvorstandRepository;

    @Autowired
    WahlvorstandMapper wahlvorstandMapper;

    @Autowired
    ExceptionFactory exceptionFactory;

    @Autowired
    EntityManager entityManager;

    @AfterEach
    void tearDown() {
        wahlvorstandRepository.deleteAll();
    }

    @Nested
    class LoadWahlvorstand {

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_WAHLVORSTAND)
        void noDataFound() throws Exception {
            val request = MockMvcRequestBuilders.get("/wahlvorstaende?wahlbezirkID=" + UUID.randomUUID());

            val response = api.perform(request).andExpect(status().isNotFound()).andReturn();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();
        }

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_WAHLVORSTAND)
        @Transactional
        void dataFound() throws Exception {
            val wahlbezirkID1 = UUID.randomUUID();
            val wahlvorstand1 = new Wahlvorstand(wahlbezirkID1,
                    Set.of(new Wahlvorstandsmitglied("vorname11", "nachname11", WahlvorstandFunktion.B, true, LocalDateTime.now()),
                            new Wahlvorstandsmitglied("vorname12", "nachname12", WahlvorstandFunktion.SWB, false, LocalDateTime.now())));
            val wahlvorstandToLoad = wahlvorstandRepository.save(wahlvorstand1);

            val wahlbezirkID2 = UUID.randomUUID();
            val wahlvorstand2 = new Wahlvorstand(wahlbezirkID2,
                    Set.of(new Wahlvorstandsmitglied("vorname21", "nachname21", WahlvorstandFunktion.B, true, LocalDateTime.now()),
                            new Wahlvorstandsmitglied("vorname22", "nachname22", WahlvorstandFunktion.SWB, false, LocalDateTime.now())));
            wahlvorstandRepository.save(wahlvorstand2);

            val request = MockMvcRequestBuilders.get("/wahlvorstaende?wahlbezirkID=" + wahlvorstandToLoad.getWahlbezirkID());

            val response = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WahlvorstandDTO.class);

            val expectedResponseDTO = wahlvorstandMapper.toDTO(wahlvorstandToLoad);

            Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedResponseDTO);
        }

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_WAHLVORSTAND)
        void wlsExceptionOnInvalidWahlbezirkIDFormat() throws Exception {
            val request = MockMvcRequestBuilders.get("/wahlvorstaende?wahlbezirkID=wrongFormat");

            val response = api.perform(request).andExpect(status().isBadRequest()).andReturn();
            val wlsExceptionDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WlsExceptionDTO.class);

            val expectedWlsException = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionConstants.ID_NICHT_KONVERTIERBAR.code(), serviceInfoOid,
                    ExceptionConstants.ID_NICHT_KONVERTIERBAR.message());

            Assertions.assertThat(wlsExceptionDTO).isEqualTo(expectedWlsException);
        }
    }

    @Nested
    class SaveAnwesenheit {

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_SAVE_ANWESENHEIT)
        @Transactional
        void personsAreUpdated() throws Exception {
            val wahlbezirkID = UUID.randomUUID();
            val oldUpdatedDate = LocalDateTime.now().minusDays(1);
            val mitglied1 = new Wahlvorstandsmitglied("vorname11", "nachname11", WahlvorstandFunktion.B, true, oldUpdatedDate);
            val mitglied2 = new Wahlvorstandsmitglied("vorname12", "nachname12", WahlvorstandFunktion.SWB, false, oldUpdatedDate);
            val mitglied3 = new Wahlvorstandsmitglied("vorname13", "nachname13", WahlvorstandFunktion.SWB, false, oldUpdatedDate);
            val wahlvorstand1 = new Wahlvorstand(wahlbezirkID,
                    Set.of(mitglied1, mitglied2, mitglied3));
            val wahlvorstandToUpdate = wahlvorstandRepository.save(wahlvorstand1);

            val updateDateTime = LocalDateTime.now();
            val mitglieder = Set.of(new WahlvorstandsmitgliedAktualisierungDTO(mitglied1.getId().toString(), false),
                    new WahlvorstandsmitgliedAktualisierungDTO(mitglied2.getId().toString(), true));
            val aktualisierung = new WahlvorstandsaktualisierungDTO(wahlbezirkID.toString(), mitglieder, updateDateTime);
            val request = MockMvcRequestBuilders.put("/wahlvorstaende/anwesenheit").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(aktualisierung));

            api.perform(request).andExpect(status().isOk());

            val updatedEntity = wahlvorstandRepository.findById(wahlvorstandToUpdate.getId());
            val expectedEntityMitglied1 = new Wahlvorstandsmitglied("vorname11", "nachname11", WahlvorstandFunktion.B, false, updateDateTime);
            expectedEntityMitglied1.setId(mitglied1.getId());
            val expectedEntityMitglied2 = new Wahlvorstandsmitglied("vorname12", "nachname12", WahlvorstandFunktion.SWB, true, updateDateTime);
            expectedEntityMitglied2.setId(mitglied2.getId());
            val expectedEntityMitglied3 = new Wahlvorstandsmitglied("vorname13", "nachname13", WahlvorstandFunktion.SWB, false, oldUpdatedDate);
            expectedEntityMitglied3.setId(mitglied3.getId());
            val expectedUpdatedEntity = new Wahlvorstand(wahlbezirkID, Set.of(expectedEntityMitglied1, expectedEntityMitglied2, expectedEntityMitglied3));
            expectedUpdatedEntity.setId(wahlvorstandToUpdate.getId());

            Assertions.assertThat(updatedEntity.get()).usingRecursiveComparison().isEqualTo(expectedUpdatedEntity);
        }

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_SAVE_ANWESENHEIT)
        void HttpStatusNotFoundWhenWahlvorstandDoesNotExists() throws Exception {
            val updateDateTime = LocalDateTime.now();
            val mitglieder = Set.of(new WahlvorstandsmitgliedAktualisierungDTO(UUID.randomUUID().toString(), false),
                    new WahlvorstandsmitgliedAktualisierungDTO(UUID.randomUUID().toString(), true));
            val aktualisierung = new WahlvorstandsaktualisierungDTO(UUID.randomUUID().toString(), mitglieder, updateDateTime);
            val request = MockMvcRequestBuilders.put("/wahlvorstaende/anwesenheit").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(aktualisierung));

            api.perform(request).andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_SAVE_ANWESENHEIT)
        void HttpStatusBadRequestWhenRequestIsInvalid() throws Exception {
            val mitglieder = Set.of(new WahlvorstandsmitgliedAktualisierungDTO(UUID.randomUUID().toString(), false),
                    new WahlvorstandsmitgliedAktualisierungDTO(UUID.randomUUID().toString(), true));
            val aktualisierung = new WahlvorstandsaktualisierungDTO(UUID.randomUUID().toString(), mitglieder, null);
            val request = MockMvcRequestBuilders.put("/wahlvorstaende/anwesenheit").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(aktualisierung));

            api.perform(request).andExpect(status().isBadRequest());
        }
    }

}
