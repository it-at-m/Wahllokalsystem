package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.authentication;

import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.common.security.AuthenticationHandler;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @Mock
    Collection<AuthenticationHandler> authenticationHandlers;

    @InjectMocks
    AuthenticationService unitUnderTest;

    @Nested
    class GetWahlbezirkArtOfCurrentAuthenticationOrThrow {

        @Test
        void should_returnWahlbezirkArt_when_handlerIsFoundAndDetailsContainWahlbezirkArt() {
            val currentAuthentication = new TestingAuthenticationToken("principal", "credentials");
            SecurityContextHolder.getContext().setAuthentication(currentAuthentication);

            val mockedAuthenticationHandler = Mockito.mock(AuthenticationHandler.class);

            Mockito.when(authenticationHandlers.stream()).thenReturn(Stream.of(mockedAuthenticationHandler));
            Mockito.when(mockedAuthenticationHandler.canHandle(Mockito.any())).thenReturn(true);
            Mockito.when(mockedAuthenticationHandler.getDetail(eq("wahlbezirksArt"), eq(currentAuthentication))).thenReturn(Optional.of("BWB"));

            val result = unitUnderTest.getWahlbezirkArtOfCurrentAuthenticationOrThrow();

            Assertions.assertThat(result).isEqualTo(WahlbezirkArtModel.BWB);
        }

        @Test
        void should_throwNullPointerException_when_noHandlerIsFound() {
            val currentAuthentication = new TestingAuthenticationToken("principal", "credentials");
            SecurityContextHolder.getContext().setAuthentication(currentAuthentication);

            val mockedAuthenticationHandler = Mockito.mock(AuthenticationHandler.class);

            Mockito.when(authenticationHandlers.stream()).thenReturn(Stream.of(mockedAuthenticationHandler));
            Mockito.when(mockedAuthenticationHandler.canHandle(Mockito.any())).thenReturn(false);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlbezirkArtOfCurrentAuthenticationOrThrow())
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void should_throwNullPointerException_when_handlerIsFoundButDetailsContainNoWahlbezirkArt() {
            val currentAuthentication = new TestingAuthenticationToken("principal", "credentials");
            SecurityContextHolder.getContext().setAuthentication(currentAuthentication);

            val mockedAuthenticationHandler = Mockito.mock(AuthenticationHandler.class);
            val mockedWlsException = FachlicheWlsException.withCode("000").buildWithMessage("sth failed");

            Mockito.when(authenticationHandlers.stream()).thenReturn(Stream.of(mockedAuthenticationHandler));
            Mockito.when(mockedAuthenticationHandler.canHandle(Mockito.any())).thenReturn(true);
            Mockito.when(mockedAuthenticationHandler.getDetail(eq("wahlbezirksArt"), eq(currentAuthentication))).thenReturn(Optional.empty());
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.WAHLBEZIRKART_NOT_LOADABLE)).thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlbezirkArtOfCurrentAuthenticationOrThrow()).isSameAs(mockedWlsException);
        }
    }
}
