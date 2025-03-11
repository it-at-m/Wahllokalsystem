package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.authentication;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.authentication.AuthDetailRetriever;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final String WAHLBEZIRK_ART_USER_DETAIL_KEY = "wahlbezirksArt";

    private final ExceptionFactory exceptionFactory;
    private final Collection<AuthDetailRetriever> authDetailRetrivers;

    public WahlbezirkArtModel getWahlbezirkArtOfCurrentAuthenticationOrThrow() throws FachlicheWlsException {
        val currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        val authDetailRetriver = authDetailRetrivers.stream().filter(handler -> handler.canHandle(currentAuthentication)).findFirst();
        val wahlbezirkOfUser = authDetailRetriver
                .flatMap(handler -> handler.getDetail(WAHLBEZIRK_ART_USER_DETAIL_KEY, currentAuthentication))
                .map(WahlbezirkArtModel::valueOf);

        return wahlbezirkOfUser.orElseThrow(() -> exceptionFactory.createFachlicheWlsException(ExceptionConstants.WAHLBEZIRKART_NOT_LOADABLE));
    }
}
