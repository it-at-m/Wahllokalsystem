package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen;

import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_TEST_PROFILE;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahltag;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahltagRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Farbe;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Wahl;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.WahlRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Wahlart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;

//@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//@AutoConfigureWireMock
//@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
@ExtendWith(MockitoExtension.class)
class WahlenServiceTest {

    @Mock
    WahlRepository wahlRepository;

    @Mock
    WahltagRepository wahltagRepository;

    @Mock
    WahlenValidator wahlenValidator;

    @Mock
    WahlModelMapper wahlModelMapper;

    @InjectMocks
    WahlenService unitUnderTest;

//    @BeforeEach
//    void setup() {
//        WireMock.resetAllRequests();
//    }

    @Nested
    class GetWahlen {

        @Test
        void ifRepoDataFoundThanReturnsRepoDataAndMakesNoCallToRemoteClient(){
//            SecurityUtils.runWith(
//                    Authorities.REPOSITORY_READ_WAHLTAG, Authorities.REPOSITORY_READ_WAHL, Authorities.SERVICE_GET_WAHLEN);
            var searchingForWahltag = new Wahltag("wahltagID",LocalDate.now(),"beschreibung1", "1" );
            Optional<Wahltag> mOp = Optional.of(searchingForWahltag);
            List<Wahl> mockedListOfEntities = createWahlEntities();
            Mockito.doNothing().when(wahlenValidator).validWahltagIDParamOrThrow("wahltagID", HttpMethod.GET);
            Mockito.doNothing().when(wahlenValidator).validateWahltagForSearchingWahltagID(mOp);

            Mockito.when(wahltagRepository.findById("wahltagID")).thenReturn(Optional.of(searchingForWahltag));
            Mockito.when(wahlRepository.countByWahltag(searchingForWahltag.getWahltag())).thenReturn(3);

            val result = unitUnderTest.getWahlen("wahltagID");
            //Assertions.assertThatCode(()->unitUnderTest.getWahlen("wahltagID"))
              //      .doesNotThrowAnyException();

            val expectedResult = wahlModelMapper.fromListOfWahlEntityToListOfWahlModel(mockedListOfEntities);
            //warum ist expectedResult size 0???????????????????

            Assertions.assertThat(result).isEqualTo(expectedResult);
            //WireMock.verify(0, WireMock.anyRequestedFor(WireMock.anyUrl()));

        }

        @Test
        void dataIsLoadedFromRemoteEvenIfExistingInRepoAndRepoIsUpdated() {
//            val mockedListOfEntities = createWahlEntities();
//            val mockedClientResponse = createWahltagModelList("2");
//            val mockedMappedSavedEntities = createWahltagModelList("2");
//
//            Mockito.when(wahltageClient.getWahltage(LocalDate.now().minusMonths(3))).thenReturn(mockedClientResponse);
//            Mockito.when(wahltagModelMapper.fromWahltagModelToWahltagEntityList(mockedClientResponse)).thenReturn(mockedListOfEntities);
//            Mockito.when(wahltagRepository.findAllByOrderByWahltagAsc()).thenReturn(mockedListOfEntities);
//            Mockito.when(wahltagModelMapper.fromWahltagEntityToWahltagModelList(mockedListOfEntities)).thenReturn(mockedMappedSavedEntities);
//
//            val result = unitUnderTest.getWahltage();
//            Assertions.assertThat(result).isSameAs(mockedMappedSavedEntities);
//            Mockito.verify(wahltagRepository).saveAll(mockedListOfEntities);

        }
    }

    @Nested
    class ResetWahlen {

        @Test
        void dataSuccessfullyReseted() {
//            ArgumentCaptor<List<Wahl>> reqCaptor = ArgumentCaptor.forClass(List.class);
//
//            val wahlenToReset = createWahlEntities();
//            Mockito.when(wahlRepository.findAll()).thenReturn(wahlenToReset);
//            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.resetWahlen());
//
//            val resetedWahlen = createWahlEntities().stream().map(this::resetWahl).toList();
//
//            Mockito.verify(wahlRepository).saveAll(reqCaptor.capture());
//            Assertions.assertThat(reqCaptor.getValue()).containsExactlyInAnyOrderElementsOf(resetedWahlen);
        }



        private Wahl resetWahl(Wahl wahl) {
            wahl.setFarbe(new Farbe(0, 0, 0));
            wahl.setReihenfolge(0);
            wahl.setWaehlerverzeichnisnummer(1);
            return wahl;
        }
    }

    private List<Wahl> createWahlEntities() {
        Wahl wahl1 = new Wahl();
        wahl1.setWahlID("wahlid1");
        wahl1.setName("wahl1");
        wahl1.setNummer("0");
        wahl1.setFarbe(new Farbe(1, 1, 1));
        wahl1.setWahlart(Wahlart.BAW);
        wahl1.setReihenfolge(1);
        wahl1.setWaehlerverzeichnisnummer(1);
        wahl1.setWahltag(LocalDate.now().plusMonths(1));

        Wahl wahl2 = new Wahl();
        wahl2.setWahlID("wahlid2");
        wahl2.setName("wahl2");
        wahl2.setNummer("1");
        wahl2.setFarbe(new Farbe(2, 2, 2));
        wahl2.setWahlart(Wahlart.LTW);
        wahl2.setReihenfolge(2);
        wahl2.setWaehlerverzeichnisnummer(2);
        wahl2.setWahltag(LocalDate.now().plusMonths(2));

        Wahl wahl3 = new Wahl();
        wahl3.setWahlID("wahlid3");
        wahl3.setName("wahl3");
        wahl3.setNummer("2");
        wahl3.setFarbe(new Farbe(3, 3, 3));
        wahl3.setWahlart(Wahlart.EUW);
        wahl3.setReihenfolge(3);
        wahl3.setWaehlerverzeichnisnummer(3);
        wahl3.setWahltag(LocalDate.now().plusMonths(3));
        List<Wahl> lw = new ArrayList<Wahl>();
        lw.add(wahl1);
        lw.add(wahl2);
        lw.add(wahl3);

        return lw;//List.of(wahl1, wahl2, wahl3);
    }

}