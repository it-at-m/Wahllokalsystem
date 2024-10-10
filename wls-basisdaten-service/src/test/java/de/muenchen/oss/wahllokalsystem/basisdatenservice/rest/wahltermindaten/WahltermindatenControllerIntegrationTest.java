package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahltermindaten;

import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_TEST_PROFILE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.WahldatenClientMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.common.WahlbezirkArt;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.Kopfdaten;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.KopfdatenRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.Stimmzettelgebietsart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.Referendumoption;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.Referendumvorlage;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.ReferendumvorlageRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.Referendumvorlagen;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.ReferendumvorlagenRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlbezirke.Wahlbezirk;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlbezirke.WahlbezirkRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlen.Wahl;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlen.WahlRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlen.Wahlart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahltag.Wahltag;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahltag.WahltagRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag.Kandidat;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag.KandidatRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag.Wahlvorschlaege;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag.WahlvorschlaegeRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag.Wahlvorschlag;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag.WahlvorschlagRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.BasisdatenDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.BasisstrukturdatenDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.StimmzettelgebietDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlbezirkDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahltagDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltermindaten.AsyncWahltermindatenService;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class)
@AutoConfigureMockMvc
@AutoConfigureWireMock
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
public class WahltermindatenControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @SpyBean
    WahlvorschlaegeRepository wahlvorschlaegeRepository;
    @Autowired
    WahlvorschlagRepository wahlvorschlagRepository;
    @Autowired
    KandidatRepository kandidatRepository;

    @SpyBean
    ReferendumvorlagenRepository referendumvorlagenRepository;
    @Autowired
    ReferendumvorlageRepository referendumvorlageRepository;

    @SpyBean
    WahlRepository wahlRepository;
    @SpyBean
    WahlbezirkRepository wahlbezirkRepository;
    @SpyBean
    KopfdatenRepository kopfdatenRepository;

    @SpyBean
    AsyncWahltermindatenService asyncWahltermindatenService;

    @Autowired
    private WahltagRepository wahltagRepository;

    @Autowired
    WahldatenClientMapper wahldatenClientMapper;

    @AfterEach
    void tearDown() {
        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_REFERENDUMVORLAGEN, Authorities.REPOSITORY_DELETE_WAHLVORSCHLAEGE,
                Authorities.REPOSITORY_DELETE_WAHL, Authorities.REPOSITORY_DELETE_WAHLBEZIRK);
        wahlvorschlaegeRepository.deleteAll();
        referendumvorlagenRepository.deleteAll();
        wahlbezirkRepository.deleteAll();
        wahlRepository.deleteAll();
        kopfdatenRepository.deleteAll();
    }

    @Nested
    class DeleteWahltermindaten {

        private String dateStringForDelete;
        private LocalDate localDateForDelete;
        private Wahltag wahltagForDelete;
        private Wahl wahlToDelete1;
        private Wahl wahlToDelete2;
        private Collection<Wahlbezirk> wahlbezirkeToDelete;
        private Collection<Kopfdaten> kopfdatenToDelete;
        private Collection<Wahlvorschlaege> wahlvorschlaegeToDelete;
        private Collection<Referendumvorlagen> referendumvorlagenToDelete;

        @BeforeEach
        void setup() throws Exception {
            //Data that should be deleted after call
            dateStringForDelete = "2013-01-01";
            localDateForDelete = LocalDate.parse(dateStringForDelete);
            wahltagForDelete = wahltagRepository.save(new Wahltag("wahltagForDelete", LocalDate.parse(dateStringForDelete), null, "12"));

            wahlToDelete1 = createWahlToDelete("wahlToDelete1", localDateForDelete);
            wahlToDelete2 = createWahlToDelete("wahlToDelete2", localDateForDelete);
            wahlbezirkeToDelete = createWahlbezirkeToDelete(localDateForDelete, wahlToDelete1.getWahlID(), wahlToDelete2.getWahlID());
            kopfdatenToDelete = createKopfdatenToDelete(wahlbezirkeToDelete);
            wahlvorschlaegeToDelete = createWahlvorschlaegeToDelete(wahlbezirkeToDelete);
            referendumvorlagenToDelete = createReferendumvorlagenToDelete(wahlbezirkeToDelete);

            setupWireMockForWahltagClient(wahltagForDelete);

            setupWireMockForWahlClient(wahlToDelete1.getWahlID(), wahlToDelete2.getWahlID());
        }

        @Test
        void should_deleteWahlenAndWahlbezirkeOnWahlenOfWahltag_when_wahltagIDIsGiven() throws Exception {
            //Data that should be kept after the call
            val localDateForDataToKeep = LocalDate.parse("2024-10-07");
            val wahlToKeep = wahlRepository.save(new Wahl(UUID.randomUUID().toString(), "wahlToKeep", 1, 1, localDateForDataToKeep, Wahlart.BTW, null, "1"));
            val wahlbezirkToKeep = wahlbezirkRepository.save(
                    new Wahlbezirk("wahlbezirkToKeep", localDateForDataToKeep, "1", WahlbezirkArt.UWB, "1", wahlToKeep.getWahlID()));
            val wahlvorschlaegeToKeep = createWahlvorschlaege(wahlToKeep.getWahlID(), wahlbezirkToKeep.getWahlbezirkID(), "3");
            val referendumvorlagenToKeep = createReferendunvorlagen(wahlToKeep.getWahlID(), wahlbezirkToKeep.getWahlbezirkID(), "3");
            val kopfdatenToKeep = kopfdatenRepository.save(
                    new Kopfdaten(new BezirkUndWahlID(wahlToKeep.getWahlID(), wahlbezirkToKeep.getWahlbezirkID()), "LHM", Stimmzettelgebietsart.WK, "3",
                            "SHZ to keep", wahlToKeep.getName(), wahlbezirkToKeep.getNummer()));

            val request = MockMvcRequestBuilders.delete("/businessActions/wahltermindaten/" + wahltagForDelete.getWahltagID());
            mockMvc.perform(request).andExpect(status().isOk());

            //verify data to keep are not deleted
            Assertions.assertThat(wahlRepository.findById(wahlToKeep.getWahlID())).isPresent();
            Assertions.assertThat(wahlbezirkRepository.findById(wahlbezirkToKeep.getWahlbezirkID())).isPresent();
            Assertions.assertThat(wahlvorschlaegeRepository.findById(wahlvorschlaegeToKeep.getId())).isPresent();
            Assertions.assertThat(referendumvorlagenRepository.findById(referendumvorlagenToKeep.getId())).isPresent();
            Assertions.assertThat(kopfdatenRepository.findById(kopfdatenToKeep.getBezirkUndWahlID())).isPresent();

            //verify no additional data exists
            val expectedCountWahlvorschlaeg = wahlvorschlaegeToKeep.getWahlvorschlaege().size();
            val expectedCountKandidaten = (Long) wahlvorschlaegeToKeep.getWahlvorschlaege().stream()
                    .map(vorschlag -> vorschlag.getKandidaten().size())
                    .mapToLong(Integer::longValue).sum();
            val expectedCountReferendumvorlage = referendumvorlagenToKeep.getReferendumvorlagen().size();
            Assertions.assertThat(wahlRepository.count()).isEqualTo(1);
            Assertions.assertThat(wahlbezirkRepository.count()).isEqualTo(1);
            Assertions.assertThat(wahlvorschlaegeRepository.count()).isEqualTo(1);
            Assertions.assertThat(wahlvorschlagRepository.count()).isEqualTo(expectedCountWahlvorschlaeg);
            Assertions.assertThat(kandidatRepository.count()).isEqualTo(expectedCountKandidaten);
            Assertions.assertThat(referendumvorlagenRepository.count()).isEqualTo(1);
            Assertions.assertThat(referendumvorlageRepository.count()).isEqualTo(expectedCountReferendumvorlage);
            Assertions.assertThat(kopfdatenRepository.count()).isEqualTo(1);
        }

        @Test
        void should_keepAllExistingData_when_deleteOfWahlbezirkeFailed() throws Exception {
            Mockito.doThrow(new RuntimeException("test transactional")).when(wahlbezirkRepository).deleteByWahltag(any());

            val request = MockMvcRequestBuilders.delete("/businessActions/wahltermindaten/" + wahltagForDelete.getWahltagID());
            mockMvc.perform(request).andExpect(status().isInternalServerError());

            verifyNoDataForDeletionIsDeleted(wahlvorschlaegeToDelete, referendumvorlagenToDelete, wahlbezirkeToDelete, kopfdatenToDelete);
        }

        @Test
        void should_keepAllExistingData_when_deleteOfKopfdatenFailed() throws Exception {
            Mockito.doThrow(new RuntimeException("test transactional")).when(kopfdatenRepository).deleteAllByBezirkUndWahlID_WahlID(any());

            val request = MockMvcRequestBuilders.delete("/businessActions/wahltermindaten/" + wahltagForDelete.getWahltagID());
            mockMvc.perform(request).andExpect(status().isInternalServerError());

            verifyNoDataForDeletionIsDeleted(wahlvorschlaegeToDelete, referendumvorlagenToDelete, wahlbezirkeToDelete, kopfdatenToDelete);
        }

        @Test
        void should_keepAllExistingData_when_deleteOfWahlenFailed() throws Exception {
            Mockito.doThrow(new RuntimeException("test transactional")).when(wahlRepository).deleteById(any());

            val request = MockMvcRequestBuilders.delete("/businessActions/wahltermindaten/" + wahltagForDelete.getWahltagID());
            mockMvc.perform(request).andExpect(status().isInternalServerError());

            verifyNoDataForDeletionIsDeleted(wahlvorschlaegeToDelete, referendumvorlagenToDelete, wahlbezirkeToDelete, kopfdatenToDelete);
        }

        @Test
        void should_keepAllExistingData_when_deleteOfWahlvorschlaegeFailed() throws Exception {
            Mockito.doThrow(new RuntimeException("test transactional")).when(wahlvorschlaegeRepository).deleteAllByBezirkUndWahlID_WahlID(any());

            val request = MockMvcRequestBuilders.delete("/businessActions/wahltermindaten/" + wahltagForDelete.getWahltagID());
            mockMvc.perform(request).andExpect(status().isInternalServerError());

            verifyNoDataForDeletionIsDeleted(wahlvorschlaegeToDelete, referendumvorlagenToDelete, wahlbezirkeToDelete, kopfdatenToDelete);
        }

        @Test
        void should_keepAllExistingData_when_deleteOfReferendumvorlagenFailed() throws Exception {
            Mockito.doThrow(new RuntimeException("test transactional")).when(referendumvorlagenRepository).deleteAllByBezirkUndWahlID_WahlID(any());

            val request = MockMvcRequestBuilders.delete("/businessActions/wahltermindaten/" + wahltagForDelete.getWahltagID());
            mockMvc.perform(request).andExpect(status().isInternalServerError());

            verifyNoDataForDeletionIsDeleted(wahlvorschlaegeToDelete, referendumvorlagenToDelete, wahlbezirkeToDelete, kopfdatenToDelete);
        }

        private void verifyNoDataForDeletionIsDeleted(Collection<Wahlvorschlaege> wahlvorschlaegeToDelete,
                Collection<Referendumvorlagen> referendumvorlagenToDelete,
                Collection<Wahlbezirk> wahlbezirkeToDelete, Collection<Kopfdaten> kopfdatenToDelete) {
            val expectedCountWahlvorschlaeg = wahlvorschlaegeToDelete.stream().map(wahlvorschlaege -> wahlvorschlaege.getWahlvorschlaege().size())
                    .mapToLong(Integer::longValue)
                    .sum();
            val expectedCountKandidaten = wahlvorschlaegeToDelete.stream()
                    .flatMap(wahlvorschlaege -> wahlvorschlaege.getWahlvorschlaege().stream())
                    .mapToLong(wahlvorschlag -> wahlvorschlag.getKandidaten().size())
                    .sum();
            val expectedCountReferendumvorlage = referendumvorlagenToDelete.stream()
                    .mapToLong(referendumvorlagen -> referendumvorlagen.getReferendumvorlagen().size())
                    .sum();
            Assertions.assertThat(wahlRepository.count()).isEqualTo(2);
            Assertions.assertThat(wahlbezirkRepository.count()).isEqualTo(wahlbezirkeToDelete.size());
            Assertions.assertThat(wahlvorschlaegeRepository.count()).isEqualTo(wahlvorschlaegeToDelete.size());
            Assertions.assertThat(wahlvorschlagRepository.count()).isEqualTo(expectedCountWahlvorschlaeg);
            Assertions.assertThat(kandidatRepository.count()).isEqualTo(expectedCountKandidaten);
            Assertions.assertThat(referendumvorlagenRepository.count()).isEqualTo(referendumvorlagenToDelete.size());
            Assertions.assertThat(referendumvorlageRepository.count()).isEqualTo(expectedCountReferendumvorlage);
            Assertions.assertThat(kopfdatenRepository.count()).isEqualTo(kopfdatenToDelete.size());
        }

        private void setupWireMockForWahlClient(String wahlToDeleteID1, String wahlToDeleteID2)
                throws JsonProcessingException {
            val wahlclientResponse = new BasisdatenDTO().basisstrukturdaten(
                    Set.of(new BasisstrukturdatenDTO().wahlID(wahlToDeleteID1), new BasisstrukturdatenDTO().wahlID(wahlToDeleteID2)));
            WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/wahldaten/basisdaten"))
                    .willReturn(WireMock.aResponse()
                            .withHeader("Content-Type", "application/json")
                            .withBody(objectMapper.writeValueAsString(wahlclientResponse))
                            .withStatus(HttpStatus.OK.value())));
        }

        private Wahl createWahlToDelete(final String wahlID, final LocalDate wahltag) {
            return wahlRepository.save(new Wahl(wahlID, wahlID, 1, 1, wahltag, Wahlart.BTW, null, "1"));
        }

        private Collection<Wahlbezirk> createWahlbezirkeToDelete(final LocalDate wahltagDate, final String wahlToDeleteID1, final String wahlToDeleteID2) {
            val wahlbezirk1ToDeleteWahl1 = wahlbezirkRepository.save(
                    new Wahlbezirk("wbz1_1", wahltagDate, "1", WahlbezirkArt.UWB, "1", wahlToDeleteID1));
            val wahlbezirk2ToDeleteWahl1 = wahlbezirkRepository.save(
                    new Wahlbezirk("wbz1_2", wahltagDate, "2", WahlbezirkArt.UWB, "1", wahlToDeleteID1));
            val wahlbezirk1ToDeleteWahl2 = wahlbezirkRepository.save(
                    new Wahlbezirk("wbz2_1", wahltagDate, "1", WahlbezirkArt.UWB, "2", wahlToDeleteID2));
            val wahlbezirk2ToDeleteWahl2 = wahlbezirkRepository.save(
                    new Wahlbezirk("wbz2_2", wahltagDate, "2", WahlbezirkArt.UWB, "2", wahlToDeleteID2));

            return List.of(wahlbezirk1ToDeleteWahl1, wahlbezirk2ToDeleteWahl1, wahlbezirk1ToDeleteWahl2, wahlbezirk2ToDeleteWahl2);
        }

        private Collection<Kopfdaten> createKopfdatenToDelete(final Collection<Wahlbezirk> wahlbezirke) {
            return wahlbezirke.stream()
                    .map(wahlbezirk -> kopfdatenRepository.save(
                            new Kopfdaten(new BezirkUndWahlID(wahlbezirk.getWahlID(), wahlbezirk.getWahlbezirkID()), "LHM", Stimmzettelgebietsart.WK, "1",
                                    "SGZ 1_1", wahlbezirk.getWahlID(), wahlbezirk.getWahlbezirkID())))
                    .toList();
        }

        private Collection<Wahlvorschlaege> createWahlvorschlaegeToDelete(final Collection<Wahlbezirk> wahlbezirke) {
            return wahlbezirke.stream()
                    .map(wahlbezirk -> createWahlvorschlaege(wahlbezirk.getWahlID(), wahlbezirk.getWahlbezirkID(), wahlbezirk.getNummer()))
                    .toList();
        }

        private Collection<Referendumvorlagen> createReferendumvorlagenToDelete(final Collection<Wahlbezirk> wahlbezirke) {
            return wahlbezirke.stream()
                    .map(wahlbezirk -> createReferendunvorlagen(wahlbezirk.getWahlID(), wahlbezirk.getWahlbezirkID(), wahlbezirk.getNummer()))
                    .toList();
        }

        private Wahlvorschlaege createWahlvorschlaege(final String wahlID, final String wahlbezirkID, final String stimmzettelgebietID) {
            val wahlvorschlaege = new Wahlvorschlaege(null, new BezirkUndWahlID(wahlID, wahlbezirkID), stimmzettelgebietID, new HashSet<>());

            val wahlvorschlag1 = new Wahlvorschlag(null, UUID.randomUUID().toString(), wahlvorschlaege, 1L, wahlID + wahlbezirkID, true,
                    new HashSet<>());
            wahlvorschlaege.addWahlvorschlag(wahlvorschlag1);
            val kandidat1Vorschlag1 = new Kandidat(null, UUID.randomUUID().toString(), wahlvorschlag1, "kandidat1", 1, false, 1L, false);
            wahlvorschlag1.addKandidat(kandidat1Vorschlag1);
            val kandidat2Vorschlag1 = new Kandidat(null, UUID.randomUUID().toString(), wahlvorschlag1, "kandidat1", 1, false, 1L, false);
            wahlvorschlag1.addKandidat(kandidat2Vorschlag1);

            val wahlvorschlag2 = new Wahlvorschlag(null, UUID.randomUUID().toString(), wahlvorschlaege, 1L, wahlID + wahlbezirkID, true,
                    new HashSet<>());
            wahlvorschlaege.addWahlvorschlag(wahlvorschlag2);
            val kandidat1Vorschlag2 = new Kandidat(null, UUID.randomUUID().toString(), wahlvorschlag2, "kandidat1", 1, false, 1L, false);
            wahlvorschlag2.addKandidat(kandidat1Vorschlag2);
            val kandidat2Vorschlag2 = new Kandidat(null, UUID.randomUUID().toString(), wahlvorschlag2, "kandidat1", 1, false, 1L, false);
            wahlvorschlag2.addKandidat(kandidat2Vorschlag2);

            return wahlvorschlaegeRepository.save(wahlvorschlaege);
        }

        private Referendumvorlagen createReferendunvorlagen(final String wahlID, final String wahlbezirkID, final String stimmzettelgebietID) {
            val referendumvorlagen = new Referendumvorlagen(null, new BezirkUndWahlID(wahlID, wahlbezirkID), stimmzettelgebietID, new HashSet<>());

            val referendumvorlage1 = new Referendumvorlage(null, referendumvorlagen, "1", 1L, wahlID + wahlbezirkID, "Frage 1", new HashSet<>());
            referendumvorlagen.addReferendumvorlage(referendumvorlage1);
            val referendumOption1Vorlage1 = new Referendumoption(UUID.randomUUID().toString(), "Option 1", 1L);
            referendumvorlage1.getReferendumoptionen().add(referendumOption1Vorlage1);
            val referendumOption2Vorlage1 = new Referendumoption(UUID.randomUUID().toString(), "Option 2", 1L);
            referendumvorlage1.getReferendumoptionen().add(referendumOption2Vorlage1);

            val referendumvorlage2 = new Referendumvorlage(null, referendumvorlagen, "1", 1L, wahlID + wahlbezirkID, "Frage 1", new HashSet<>());
            referendumvorlagen.addReferendumvorlage(referendumvorlage2);
            val referendumOption1Vorlage2 = new Referendumoption(UUID.randomUUID().toString(), "Option 1", 1L);
            referendumvorlage1.getReferendumoptionen().add(referendumOption1Vorlage2);
            val referendumOption2Vorlage2 = new Referendumoption(UUID.randomUUID().toString(), "Option 2", 1L);
            referendumvorlage1.getReferendumoptionen().add(referendumOption2Vorlage2);

            return referendumvorlagenRepository.save(referendumvorlagen);
        }
    }

    @Nested
    class PutWahltermindaten {

        @Test
        void should_persistImportedData_when_wahltagIDIsValidWahltag() throws Exception {
            val localDateOfWahltag = LocalDate.parse("2024-08-26");
            val wahltagToGetWahltermindaten = new Wahltag("wahltagID", localDateOfWahltag, "", "1");

            setupWireMockForWahltagClient(wahltagToGetWahltermindaten);

            val basisstrukturdatenToImport = new BasisdatenDTO()
                    .basisstrukturdaten(
                            Set.of(
                                    new BasisstrukturdatenDTO().wahlID("wahlID1").wahltag(localDateOfWahltag).wahlbezirkID("wahlbezirkID1_1")
                                            .stimmzettelgebietID("sgzID1"),
                                    new BasisstrukturdatenDTO().wahlID("wahlID1").wahltag(localDateOfWahltag).wahlbezirkID("wahlbezirkID1_2")
                                            .stimmzettelgebietID("sgzID1"),
                                    new BasisstrukturdatenDTO().wahlID("wahlID1").wahltag(localDateOfWahltag).wahlbezirkID("wahlbezirkID1_3")
                                            .stimmzettelgebietID("sgzID1"),
                                    new BasisstrukturdatenDTO().wahlID("wahlID2").wahltag(localDateOfWahltag).wahlbezirkID("wahlbezirkID2_1")
                                            .stimmzettelgebietID("sgzID2"),
                                    new BasisstrukturdatenDTO().wahlID("wahlID2").wahltag(localDateOfWahltag).wahlbezirkID("wahlbezirkID2_2")
                                            .stimmzettelgebietID("sgzID2")))
                    .stimmzettelgebiete(
                            Set.of(
                                    new StimmzettelgebietDTO().wahltag(localDateOfWahltag).name("sgz1").identifikator("sgzID1").nummer("1")
                                            .stimmzettelgebietsart(
                                                    StimmzettelgebietDTO.StimmzettelgebietsartEnum.WK),
                                    new StimmzettelgebietDTO().wahltag(localDateOfWahltag).name("sgz2").identifikator("sgzID2").nummer("2")
                                            .stimmzettelgebietsart(
                                                    StimmzettelgebietDTO.StimmzettelgebietsartEnum.WK)

                            ))
                    .wahlen(
                            Set.of(
                                    new WahlDTO().name("wahl 1").wahltag(localDateOfWahltag).identifikator("wahlID1").wahlart(WahlDTO.WahlartEnum.BTW)
                                            .nummer("1"),
                                    new WahlDTO().name("wahl 2").wahltag(localDateOfWahltag).identifikator("wahlID2").wahlart(WahlDTO.WahlartEnum.BTW)
                                            .nummer("2")))
                    .wahlbezirke(
                            Set.of(
                                    new WahlbezirkDTO().wahltag(localDateOfWahltag).wahlID("wahlID1").identifikator("wahlbezirkID1_1").nummer("1_1")
                                            .wahlnummer("1").wahlbezirkArt(
                                                    WahlbezirkDTO.WahlbezirkArtEnum.UWB),
                                    new WahlbezirkDTO().wahltag(localDateOfWahltag).wahlID("wahlID1").identifikator("wahlbezirkID1_2").nummer("1_2")
                                            .wahlnummer("1").wahlbezirkArt(
                                                    WahlbezirkDTO.WahlbezirkArtEnum.UWB),
                                    new WahlbezirkDTO().wahltag(localDateOfWahltag).wahlID("wahlID1").identifikator("wahlbezirkID1_3").nummer("1_3")
                                            .wahlnummer("1").wahlbezirkArt(
                                                    WahlbezirkDTO.WahlbezirkArtEnum.UWB),
                                    new WahlbezirkDTO().wahltag(localDateOfWahltag).wahlID("wahlID2").identifikator("wahlbezirkID2_1").nummer("2_1")
                                            .wahlnummer("2").wahlbezirkArt(
                                                    WahlbezirkDTO.WahlbezirkArtEnum.UWB),
                                    new WahlbezirkDTO().wahltag(localDateOfWahltag).wahlID("wahlID2").identifikator("wahlbezirkID2_2").nummer("2_2")
                                            .wahlnummer("2").wahlbezirkArt(
                                                    WahlbezirkDTO.WahlbezirkArtEnum.UWB)));

            WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/wahldaten/basisdaten"))
                    .willReturn(WireMock.aResponse()
                            .withHeader("Content-Type", "application/json")
                            .withBody(objectMapper.writeValueAsString(basisstrukturdatenToImport))
                            .withStatus(HttpStatus.OK.value())));

            val request = MockMvcRequestBuilders.put("/businessActions/wahltermindaten/" + wahltagToGetWahltermindaten.getWahltagID());
            mockMvc.perform(request).andExpect(status().isOk());

            Assertions.assertThat(kopfdatenRepository.count()).isEqualTo(5);
            Assertions.assertThat(wahlbezirkRepository.count()).isEqualTo(5);
            Assertions.assertThat(wahlRepository.count()).isEqualTo(2);

            val expectedBasisdatenModel = wahldatenClientMapper.fromRemoteClientDTOToModel(basisstrukturdatenToImport);
            Mockito.verify(asyncWahltermindatenService)
                    .initVorlagenAndVorschlaege(eq(wahltagToGetWahltermindaten.getWahltag()), eq(wahltagToGetWahltermindaten.getNummer()),
                            eq(expectedBasisdatenModel));
        }
    }

    private void setupWireMockForWahltagClient(Wahltag wahltag) throws JsonProcessingException {
        val wahltagClientResponse = Set.of(new WahltagDTO().identifikator(wahltag.getWahltagID()).tag(wahltag.getWahltag())
                .nummer(wahltag.getNummer()));
        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/wahldaten/wahltage"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(wahltagClientResponse))
                        .withStatus(HttpStatus.OK.value())));
    }
}
