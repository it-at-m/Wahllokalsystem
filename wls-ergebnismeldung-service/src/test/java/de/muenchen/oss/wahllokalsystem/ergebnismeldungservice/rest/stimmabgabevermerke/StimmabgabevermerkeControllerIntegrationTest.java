package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.BezirkUndWahlIDUndWaehlerverzeichnisnummer;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.EingenommenerWahlscheine;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Stimmabgabevermerke;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.StimmabgabevermerkeRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Stimmzettel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Stimmzettelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Vermerk;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Wahldaten;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke.dto.StimmabgabevermerkeDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke.dto.StimmabgabevermerkeDTOMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.StimmabgabevermerkeModelMapper;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class)
@AutoConfigureMockMvc
@AutoConfigureWireMock
@ActiveProfiles(
        profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE,
                de.muenchen.oss.wahllokalsystem.wls.common.security.Profiles.NO_BEZIRKS_ID_CHECK }
)
public class StimmabgabevermerkeControllerIntegrationTest {

    @Autowired
    StimmabgabevermerkeRepository stimmabgabevermerkeRepository;

    @Autowired
    StimmabgabevermerkeModelMapper stimmabgabevermerkeModelMapper;

    @Autowired
    StimmabgabevermerkeDTOMapper stimmabgabevermerkeDTOMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @AfterEach
    void setup() {
        stimmabgabevermerkeRepository.deleteAll();
    }

    @Nested
    class GetStimmabgabevermerke {

        @Test
        void should_returnData_when_dataIsPresentInRepository() throws Exception {
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";
            val waehlerverzeichnisNummer = 1L;
            val anzahlBlaetter = 4711L;
            val request = MockMvcRequestBuilders.get(buildStimmabgabevermerkeURI(wahlbezirkID, waehlerverzeichnisNummer));

            val entityToFind = new Stimmabgabevermerke();
            val wahldaten = createWahldaten(wahlbezirkID, wahlID, waehlerverzeichnisNummer);
            entityToFind.setBezirkIDUndWaehlerverzeichnisNummer(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer));
            entityToFind.setAnzahlBlaetter(anzahlBlaetter);
            entityToFind.getWahldaten().add(wahldaten);

            stimmabgabevermerkeRepository.save(entityToFind);

            val response = mockMvc.perform(request).andExpect(status().isOk()).andReturn().getResponse();
            val responseBodyAsDTO = objectMapper.readValue(response.getContentAsString(), StimmabgabevermerkeDTO.class);

            val expectedResult = stimmabgabevermerkeDTOMapper.toStimmabgabevermerkeDTO(stimmabgabevermerkeModelMapper.toModel(entityToFind));

            Assertions.assertThat(responseBodyAsDTO)
                    .usingRecursiveComparison()
                    .ignoringCollectionOrder()
                    .isEqualTo(expectedResult);
        }

    }

    @Nested
    class PostStimmabgabevermerke {

    }

    private String buildStimmabgabevermerkeURI(final String wahlbezirkID, final Long waehlerverzeichnisNummer) {
        return "/businessActions/stimmabgabevermerke/" + wahlbezirkID + "/" + waehlerverzeichnisNummer;
    }

    private Wahldaten createWahldaten(final String wahlbezirkID, final String wahlID, final Long waehlerverzeichnisNummer) {
        val emptyWahldaten = new Wahldaten();
        val stimmzettel1 = new Stimmzettel();
        stimmzettel1.setAnzahl(20);
        stimmzettel1.setStimmzettelart(Stimmzettelart.KLEIN);

        val stimmzettel2 = new Stimmzettel();
        stimmzettel2.setAnzahl(21);
        stimmzettel2.setStimmzettelart(Stimmzettelart.GROSS);

        val stimmzettel3 = new Stimmzettel();
        stimmzettel3.setAnzahl(22);
        stimmzettel3.setStimmzettelart(Stimmzettelart.BEIDE);

        val vermerk1 = new Vermerk();
        vermerk1.setBlattnummer(1L);
        vermerk1.getStimmzetteln().addAll(List.of(stimmzettel1, stimmzettel2, stimmzettel3));

        val vermerk2 = new Vermerk();
        vermerk2.setBlattnummer(2L);
        vermerk2.getStimmzetteln().addAll(List.of(stimmzettel1, stimmzettel2, stimmzettel3));

        val wahlschein1 = new EingenommenerWahlscheine();
        wahlschein1.setAnzahl(1);
        wahlschein1.setStimmzettelart(Stimmzettelart.KLEIN);
        val wahlschein2 = new EingenommenerWahlscheine();
        wahlschein2.setAnzahl(2);
        wahlschein2.setStimmzettelart(Stimmzettelart.GROSS);
        val wahlschein3 = new EingenommenerWahlscheine();
        wahlschein3.setAnzahl(3);
        wahlschein3.setStimmzettelart(Stimmzettelart.BEIDE);

        emptyWahldaten
                .setBezirkUndWahlIDUndWaehlerverzeichnisnummer(new BezirkUndWahlIDUndWaehlerverzeichnisnummer(wahlbezirkID, wahlID, waehlerverzeichnisNummer));
        emptyWahldaten.getVermerke().addAll(List.of(vermerk1, vermerk2));
        emptyWahldaten.getEingenommenewahlscheine().addAll(List.of(wahlschein1, wahlschein2, wahlschein3));

        return emptyWahldaten;
    }
}
