package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client.briefwahl;

import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.briefwahl.client.BeanstandeteWahlbriefeControllerApi;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.briefwahl.model.BeanstandeteWahlbriefeDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.briefwahl.model.Zurueckweisungsgrund;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BriefwahlClientTest {

    @Mock
    BeanstandeteWahlbriefeControllerApi beanstandeteWahlbriefeControllerApi;

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    BriefwahlClient unitUnderTest;

    @Nested
    class GetAnzahlZurueckgewiesenerWahlbriefe {

        @Test
        void should_returnCountOfWahlbriefeThatAreNotZugelassen_when_apiReturnedListWithWahlbriefe() {
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";
            val waehlerverzeichnissNummer = 1L;

            val mockedBeanstandeteWahlbriefe = new BeanstandeteWahlbriefeDTO().putBeanstandeteWahlbriefeItem(wahlID,
                    List.of(Zurueckweisungsgrund.SCHEINE_UNGLEICH_UMSCHLAEGE, Zurueckweisungsgrund.ZUGELASSEN, Zurueckweisungsgrund.KEIN_ORIGINAL_SCHEIN,
                            Zurueckweisungsgrund.SCHEIN_UNGUELTIG));
            Mockito.when(beanstandeteWahlbriefeControllerApi.getBeanstandeteWahlbriefe(eq(wahlbezirkID), eq(waehlerverzeichnissNummer)))
                    .thenReturn(mockedBeanstandeteWahlbriefe);

            val result = unitUnderTest.getAnzahlZurueckgewiesenerWahlbriefe(wahlbezirkID, wahlID, waehlerverzeichnissNummer);

            Assertions.assertThat(result).isEqualTo(3);
        }

        @Test
        void should_rethrowWlsException_when_apiThrewWlsException() {
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";
            val waehlerverzeichnissNummer = 1L;

            val mockedApiWlsException = FachlicheWlsException.withCode("000").buildWithMessage("");
            Mockito.doThrow(mockedApiWlsException).when(beanstandeteWahlbriefeControllerApi)
                    .getBeanstandeteWahlbriefe(eq(wahlbezirkID), eq(waehlerverzeichnissNummer));

            Assertions.assertThatException()
                    .isThrownBy(() -> unitUnderTest.getAnzahlZurueckgewiesenerWahlbriefe(wahlbezirkID, wahlID, waehlerverzeichnissNummer))
                    .isSameAs(mockedApiWlsException);
        }

        @Test
        void should_throwWlsException_when_apiThrewNonWlsException() {
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";
            val waehlerverzeichnissNummer = 1L;

            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("");
            val mockedApiException = new RuntimeException("api runtime exception");

            Mockito.doThrow(mockedApiException).when(beanstandeteWahlbriefeControllerApi)
                    .getBeanstandeteWahlbriefe(eq(wahlbezirkID), eq(waehlerverzeichnissNummer));
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_BRIEFWAHL)).thenReturn(mockedWlsException);

            Assertions.assertThatException()
                    .isThrownBy(() -> unitUnderTest.getAnzahlZurueckgewiesenerWahlbriefe(wahlbezirkID, wahlID, waehlerverzeichnissNummer))
                    .isSameAs(mockedWlsException);
        }
    }
}
