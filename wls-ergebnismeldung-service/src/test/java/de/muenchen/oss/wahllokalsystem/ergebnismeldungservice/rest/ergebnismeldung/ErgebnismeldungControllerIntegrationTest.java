package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ergebnismeldung;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getAllServeEvents;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.admin.model.ServeEventQuery;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerte;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerteRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.BezirkUndWahlIDStapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.Ergebnis;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.Ergebnisse;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.ErgebnisseRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.status.Meldung;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.status.Status;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.status.StatusRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.status.Validierungsstatus;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.BezirkUndWahlIDUndWaehlerverzeichnisnummer;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Stimmabgabevermerke;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.StimmabgabevermerkeRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Wahldaten;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.ErgebnismeldungDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.basisdaten.model.WahlDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.infomanagement.model.KonfigurierterWahltagDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.monitoring.model.SendungsdatenDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.wahlvorbereitung.model.UrnenwahlSchliessungsUhrzeitDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Testdaten;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.WithMockUserAsJwt;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureWireMock
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, "dummy.nobezirkid.check" })
class ErgebnismeldungControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    StatusRepository statusRepository;

    @Autowired
    ErgebnisseRepository ergebnisseRepository;

    @Autowired
    StimmabgabevermerkeRepository stimmabgabevermerkeRepository;

    @Autowired
    AWerteRepository aWerteRepository;

    @Autowired
    ObjectMapper objectMapper;

    private static final MockedStatic<LocalDateTime> mockedStaticLocalDateTime = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);

    @BeforeAll
    static void setupTestSuit() {
        var clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
        var mockedLocalDateTime = LocalDateTime.now(clock);
        mockedStaticLocalDateTime.when(LocalDateTime::now).thenReturn(mockedLocalDateTime);
    }

    @BeforeEach
    void setup() {

    }

    @AfterEach
    void teardown() {
        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_STATUS);
        statusRepository.deleteAll();

        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_AWERTE);
        aWerteRepository.deleteAll();

        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_ERGEBNISSE);
        ergebnisseRepository.deleteAll();

        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_STIMMABGABEVERMERKE);
        stimmabgabevermerkeRepository.deleteAll();

        reset();
    }

    @AfterAll
    static void teardownTestSuit() {
        mockedStaticLocalDateTime.close();
    }

    @Nested
    class SendErgebnisse {

        @Nested
        class UpdateSendungszeiten {

            @Nested
            class ForUWB {

                @WithMockUserAsJwt(
                        claimProperties = "wahlbezirksArt=UWB", authorities = {
                                Authorities.REPOSITORY_READ_STATUS, Authorities.REPOSITORY_WRITE_STATUS, Authorities.SERVICE_GET_STATUS,
                                Authorities.SERVICE_UPDATE_SENDUNGSZEITEN
                        }
                )
                @Test
                void should_sendSchnellmeldungToMonitoringService_when_statusForBezirkUndWahlRequiredSchnellmeldungToSend() throws Exception {
                    val wahlbezirkID = "wahlbezirkID";
                    val wahlID = "wahlID";
                    val waehlerverzeichnisNummer = 1L;

                    val statusToSend = new Status(new BezirkUndWahlID(wahlID, wahlbezirkID), createMeldung(Validierungsstatus.VALIDE),
                            createMeldung(Validierungsstatus.NICHT_GESENDET));
                    statusRepository.save(statusToSend);

                    val mockedNow = LocalDateTime.now();

                    val schnellmeldungSendungsuhrzeitStubbing = stubFor(
                            post("/businessActions/schnellmeldungSendungsuhrzeit").willReturn(aResponse().withStatus(HttpStatus.OK.value())));

                    val mockedUrnenwahlschliessungsUhrzeit = new UrnenwahlSchliessungsUhrzeitDTO().urnenwahlSchliessungsUhrzeit(mockedNow);
                    stubFor(get(createURIUrnenwahlSchliessungsUhrzeit(wahlbezirkID))
                            .willReturn(createWireMockResponse(mockedUrnenwahlschliessungsUhrzeit, HttpStatus.OK)));

                    val request = MockMvcRequestBuilders.post(
                            createURISendErgebnismeldungForNiederschrift(wahlbezirkID, wahlID, waehlerverzeichnisNummer))
                            .header("forceergebnismeldung", "true")
                            .with(csrf());
                    mockMvc.perform(request).andExpect(status().isOk());

                    val statusRequest = getAllServeEvents(ServeEventQuery.forStubMapping(schnellmeldungSendungsuhrzeitStubbing)).get(0);
                    val sendStatusRequestBodyAsDTO = objectMapper.readValue(statusRequest.getRequest().getBody(), SendungsdatenDTO.class);

                    val expectedSendStatusRequestBody = new SendungsdatenDTO().bezirkUndWahlID(
                            new de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.monitoring.model.BezirkUndWahlID().wahlID(wahlID)
                                    .wahlbezirkID(wahlbezirkID))
                            .sendungsuhrzeit(mockedNow);

                    Assertions.assertThat(sendStatusRequestBodyAsDTO).isEqualTo(expectedSendStatusRequestBody);
                }

                @WithMockUserAsJwt(
                        claimProperties = "wahlbezirksArt=UWB", authorities = {
                                Authorities.REPOSITORY_READ_STATUS, Authorities.REPOSITORY_WRITE_STATUS, Authorities.SERVICE_GET_STATUS,
                                Authorities.SERVICE_UPDATE_SENDUNGSZEITEN
                        }
                )
                @Test
                @Disabled("nicht durchführbar weil eine fehlende Schließungsuhrzeit zu einem Fehler führt welcher als geschlossen interpretiert wird #793")
                void should_returnBadRequestWithFachlicheWlsException_when_wahlIsNotGeschlossen() throws Exception {
                    val wahlbezirkID = "wahlbezirkID";
                    val wahlID = "wahlID";
                    val waehlerverzeichnisNummer = 1L;

                    val statusToSend = new Status(new BezirkUndWahlID(wahlID, wahlbezirkID), createMeldung(Validierungsstatus.VALIDE),
                            createMeldung(Validierungsstatus.NICHT_GESENDET));
                    statusRepository.save(statusToSend);

                    val mockedUrnenwahlschliessungsUhrzeit = new UrnenwahlSchliessungsUhrzeitDTO().urnenwahlSchliessungsUhrzeit(null);
                    stubFor(get(createURIUrnenwahlSchliessungsUhrzeit(wahlbezirkID))
                            .willReturn(createWireMockResponse(mockedUrnenwahlschliessungsUhrzeit, HttpStatus.OK)));

                    val request = MockMvcRequestBuilders.post(
                            createURISendErgebnismeldungForNiederschrift(wahlbezirkID, wahlID, waehlerverzeichnisNummer))
                            .header("forceergebnismeldung", "true")
                            .with(csrf());
                    val mockMvcResult = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn();
                    val resultBodyAsWlsExceptionDTO = objectMapper.readValue(mockMvcResult.getResponse().getContentAsString(), WlsExceptionDTO.class);

                    Assertions.assertThat(resultBodyAsWlsExceptionDTO.category()).isEqualTo(WlsExceptionCategory.F);
                }
            }
        }

        @Nested
        class SendErgebnisseToEAI {

            @WithMockUserAsJwt(
                    claimProperties = "wahlbezirksArt=UWB", authorities = {
                            Authorities.REPOSITORY_READ_STATUS, Authorities.REPOSITORY_WRITE_STATUS, Authorities.SERVICE_GET_STATUS,
                            Authorities.SERVICE_SEND_ERGEBNISSE,
                            Authorities.REPOSITORY_READ_STIMMABGABEVERMERKE, Authorities.REPOSITORY_WRITE_STIMMABGABEVERMERKE,
                            Authorities.SERVICE_GET_STIMMABGABEVERMERKE,
                            Authorities.REPOSITORY_READ_STIMMZETTELUMSCHLAEGE,
                            Authorities.REPOSITORY_READ_AWERTE, Authorities.REPOSITORY_WRITE_AWERTE, Authorities.SERVICE_GET_AWERTE,
                            Authorities.REPOSITORY_READ_ERGEBNISSE, Authorities.REPOSITORY_WRITE_ERGEBNISSE, Authorities.SERVICE_GET_ERGEBNISSE,
                    }
            )
            @Test
            void should_sendErgebnisseToEAI_when_ergebnisseAreValid() throws Exception {
                val wahlbezirkID = "wahlbezirkID";
                val wahlID = "wahlID";
                val waehlerverzeichnisNummer = 1L;

                insertDataForErgebnismeldungIntoRepositories(wahlID, wahlbezirkID, waehlerverzeichnisNummer);

                //define wiremock stubbings
                val mockedWahltagID = "wahltagID";
                val mockedKonfigurierterWahltag = new KonfigurierterWahltagDTO().wahltagID(mockedWahltagID);
                stubFor(
                        get("/businessActions/konfigurierterWahltag").willReturn(createWireMockResponse(mockedKonfigurierterWahltag, HttpStatus.OK)));

                val mockedWahlenOfWahltag = List.of(new WahlDTO().wahlart(WahlDTO.WahlartEnum.EUW).wahlID(wahlID));
                stubFor(
                        get("/businessActions/wahlen/" + mockedWahltagID).willReturn(createWireMockResponse(mockedWahlenOfWahltag, HttpStatus.OK)));

                val mockedUrnenwahlschliessungsUhrzeit = new UrnenwahlSchliessungsUhrzeitDTO().urnenwahlSchliessungsUhrzeit(LocalDateTime.now());
                stubFor(get(createURIUrnenwahlSchliessungsUhrzeit(wahlbezirkID))
                        .willReturn(createWireMockResponse(mockedUrnenwahlschliessungsUhrzeit, HttpStatus.OK)));
                val eaiServiceStubbing = stubFor(post("/ergebnismeldung").willReturn(createWireMockResponse(HttpStatus.OK)));

                val request = MockMvcRequestBuilders.post(
                        createURISendErgebnismeldungForNiederschrift(wahlbezirkID, wahlID, waehlerverzeichnisNummer))
                        .with(csrf());
                mockMvc.perform(request).andExpect(status().isOk());

                val eaiEvents = getAllServeEvents(ServeEventQuery.forStubMapping(eaiServiceStubbing));
                Assertions.assertThat(eaiEvents.size()).isEqualTo(1);

                val sendErgebnismeldungToEAI = objectMapper.readValue(eaiEvents.get(0).getRequest().getBody(), ErgebnismeldungDTO.class);
                Assertions.assertThat(sendErgebnismeldungToEAI.getWahlbezirkID()).isEqualTo(wahlbezirkID);
                Assertions.assertThat(sendErgebnismeldungToEAI.getWahlID()).isEqualTo(wahlID);
            }

            private void insertDataForErgebnismeldungIntoRepositories(String wahlID, String wahlbezirkID, long waehlerverzeichnisNummer) {
                val statusToSend = new Status(new BezirkUndWahlID(wahlID, wahlbezirkID), createMeldung(Validierungsstatus.VALIDE),
                        createMeldung(Validierungsstatus.NICHT_GESENDET));
                statusRepository.save(statusToSend);

                //Insert ergebnisse für Stapel
                val stapelA = new Ergebnisse(new BezirkUndWahlIDStapelart(wahlbezirkID, wahlID, Stapelart.EUW_A),
                        List.of(new Ergebnis("wahlvorschlag1", null, 0L, 1L, 0L)));
                val stapelBUngekennzeichnet = new Ergebnisse(new BezirkUndWahlIDStapelart(wahlbezirkID, wahlID, Stapelart.EUW_B_UNGEKENNZEICHNET),
                        List.of(new Ergebnis("wahlvorschlag1", null, 0L, 1L, 0L)));
                val stapelCGueltig = new Ergebnisse(new BezirkUndWahlIDStapelart(wahlbezirkID, wahlID, Stapelart.EUW_C_GUELTIG),
                        List.of(new Ergebnis("wahlvorschlag1", null, 0L, 1L, 0L)));
                val stapelCUngueltig = new Ergebnisse(new BezirkUndWahlIDStapelart(wahlbezirkID, wahlID, Stapelart.EUW_C_UNGUELTIG),
                        List.of(new Ergebnis("wahlvorschlag1", null, 0L, 1L, 0L)));
                ergebnisseRepository.save(stapelA);
                ergebnisseRepository.save(stapelBUngekennzeichnet);
                ergebnisseRepository.save(stapelCGueltig);
                ergebnisseRepository.save(stapelCUngueltig);

                //Insert Stimmabgabevermerke
                val vermerk = Testdaten.Vermerk.createEntity(1);
                val eigennommeneWahlscheine = Testdaten.EigenommenerWahlschein.createEntity(1);
                val wahldaten = new Wahldaten(UUID.randomUUID(), new BezirkUndWahlIDUndWaehlerverzeichnisnummer(wahlbezirkID, wahlID, waehlerverzeichnisNummer),
                        Set.of(vermerk), Set.of(eigennommeneWahlscheine));
                vermerk.setWahldaten(wahldaten);
                stimmabgabevermerkeRepository.save(new Stimmabgabevermerke(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer), 1,
                        Set.of(wahldaten)));

                //Insert AWerte
                aWerteRepository.save(new AWerte(new BezirkUndWahlID(wahlID, wahlbezirkID), 1L, null));
            }
        }
    }

    private String createURISendErgebnismeldungForNiederschrift(final String wahlbezirkID, final String wahlID, final long waehlerverzeichnisNummer) {
        return "/businessActions/sendErgebnismeldung/" + wahlID + "/" + wahlbezirkID + "/" + waehlerverzeichnisNummer + "/V1/hauptwahlbezirkID";
    }

    private String createURIUrnenwahlSchliessungsUhrzeit(final String wahlbezirkID) {
        return "/businessActions/urnenwahlSchliessungsUhrzeit/" + wahlbezirkID;
    }

    private ResponseDefinitionBuilder createWireMockResponse(final Object responseBody, final HttpStatus responseStatus) throws Exception {
        return aResponse()
                .withBody(objectMapper.writeValueAsString(responseBody))
                .withHeader("Content-Type", "application/json")
                .withStatus(responseStatus.value());
    }

    private ResponseDefinitionBuilder createWireMockResponse(final HttpStatus responseStatus) {
        return aResponse()
                .withStatus(responseStatus.value());
    }

    private Meldung createMeldung(final Validierungsstatus validierungsstatus) {
        val meldung = new Meldung();

        meldung.setGedruckt(true);
        meldung.setSendeuhrzeit(LocalDateTime.now());
        meldung.setUebermittelt(false);
        meldung.setValidierungsstatus(validierungsstatus);

        return meldung;
    }

}
