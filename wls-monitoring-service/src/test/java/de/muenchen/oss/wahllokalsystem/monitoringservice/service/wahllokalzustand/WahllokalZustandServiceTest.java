package de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WahllokalZustandServiceTest {

    @Mock
    WahllokalZustandValidator wahllokalZustandValidator;
    @Mock
    WahllokalZustandClient wahllokalZustandClient;

    @InjectMocks
    WahllokalZustandService unitUnderTest;

    @Nested
    class PostLastSeen {

        @Test
        void should_notThrowException_when_ModelIsGiven() {
            val wahllokalZustandModel = WahllokalZustandModel.builder().build();
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postLastSeen(wahllokalZustandModel.wahlbezirkID()));
        }
    }

    @Nested
    class PostLetzteAbmeldung {

        @Test
        void should_notThrowException_when_ModelIsGiven() {
            val wahllokalZustandModel = WahllokalZustandModel.builder().build();
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postLetzteAbmeldung(wahllokalZustandModel.wahlbezirkID()));
        }
    }

    @Nested
    class PostSchnellmeldungSendungsuhrzeit {

        @Test
        void should_notThrowException_when_ModelIsGiven() {
            val sendungsdatenModel = SendungsdatenModel.builder().bezirkUndWahlID(new BezirkUndWahlID("wahlID", "wahlbezirkID")).build();
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postSchnellmeldungSendungsuhrzeit(sendungsdatenModel));
        }
    }

    @Nested
    class PostSchnellmeldungDruckuhrzeit {

        @Test
        void should_notThrowException_when_ModelIsGiven() {
            val druckdatenModel = DruckdatenModel.builder().bezirkUndWahlID(new BezirkUndWahlID("wahlID", "wahlbezirkID")).build();
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postSchnellmeldungDruckuhrzeit(druckdatenModel));
        }
    }

    @Nested
    class PostNiederschriftSendungsuhrzeit {

        @Test
        void should_notThrowException_when_ModelIsGiven() {
            val sendungsdatenModel = SendungsdatenModel.builder().bezirkUndWahlID(new BezirkUndWahlID("wahlID", "wahlbezirkID")).build();
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postNiederschriftSendungsuhrzeit(sendungsdatenModel));
        }
    }

    @Nested
    class PostNiederschriftDruckuhrzeit {

        @Test
        void should_notThrowException_when_ModelIsGiven() {
            val druckdatenModel = DruckdatenModel.builder().bezirkUndWahlID(new BezirkUndWahlID("wahlID", "wahlbezirkID")).build();
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postNiederschriftDruckuhrzeit(druckdatenModel));
        }
    }

    @Nested
    class PostTestExceptions {

        @Test
        void should_notThrowException_when_ModelIsGiven() {
            val druckdatenModel = DruckdatenModel.builder().bezirkUndWahlID(new BezirkUndWahlID("wahlID", "wahlbezirkID")).build();
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postNiederschriftDruckuhrzeit(druckdatenModel));
        }
    }
}
