package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.authentication;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.common.security.AuthenticationHandler;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
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
    private final Collection<AuthenticationHandler> authenticationHandlers;

    //TODO Signatur um Exception erweitern zu besseren Dokumentation
    public WahlbezirkArtModel getWahlbezirkArtOfCurrentAuthentication() {
        /* TODO mit der Logik aus dem Stimmzettelumschlägen zusammenlegen */
        val currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        val authenticationHandler = authenticationHandlers.stream().filter(handler -> handler.canHandle(currentAuthentication)).findFirst();
        try {
            val wahlbezirkOfUser = authenticationHandler.get().getDetail(WAHLBEZIRK_ART_USER_DETAIL_KEY, currentAuthentication);
            return wahlbezirkOfUser.map(WahlbezirkArtModel::valueOf).get();
        } catch (Exception e) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.WAHLBEZIRKART_NOT_LOADABLE);
        }
    }
}
