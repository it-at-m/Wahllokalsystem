package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.client.WahlvorschlagControllerApi;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlvorschlaegeDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege.WahlvorschlaegeModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
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
class ClientImplTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @Mock
    WahlvorschlagControllerApi wahlvorschlagControllerApi;

    @Mock
    WahlvorschlaegeClientMapper wahlvorschlaegeClientMapper;

    @InjectMocks
    ClientImpl unitUnderTest;

    @Nested
    class GetWahlvorschlaege {

        @Test
        void clientResponseIsMapped() {
            val bezirkUndWahlID = new BezirkUndWahlID("wahlID", "wahlbezirkID");

            val mockedClientResponse = new WahlvorschlaegeDTO();
            val mockedMappedClientResponse = WahlvorschlaegeModel.builder().build();

            Mockito.when(wahlvorschlagControllerApi.loadWahlvorschlaege(eq(bezirkUndWahlID.getWahlID()), eq(bezirkUndWahlID.getWahlbezirkID())))
                    .thenReturn(mockedClientResponse);
            Mockito.when(wahlvorschlaegeClientMapper.toModel(mockedClientResponse)).thenReturn(mockedMappedClientResponse);

            val result = unitUnderTest.getWahlvorschlaege(bezirkUndWahlID);

            Assertions.assertThat(result).isSameAs(mockedMappedClientResponse);
        }

        @Test
        void exceptionWhenClientResponseIsNull() {
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(wahlvorschlagControllerApi.loadWahlvorschlaege(any(), any())).thenReturn(null);
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.NULL_FROM_CLIENT)).thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlvorschlaege(new BezirkUndWahlID("", ""))).isSameAs(mockedWlsException);
        }
    }

}
