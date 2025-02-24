package de.muenchen.oss.wahllokalsystem.adminservice.service.wahltage;

import static org.mockito.Mockito.times;

import de.muenchen.oss.wahllokalsystem.adminservice.service.common.WahltageClient;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class WahltageServiceTest {

    @Mock
    WahltageClient wahltageClient;

    @InjectMocks
    WahltageService unitUnderTest;

    @Nested
    class GetWahltage {

        @Test
        void should_verifyApiCall_when_serviceIsCalled() {
            unitUnderTest.getWahltage();

            Mockito.verify(wahltageClient, times(1)).getWahltage();
        }
    }
}
