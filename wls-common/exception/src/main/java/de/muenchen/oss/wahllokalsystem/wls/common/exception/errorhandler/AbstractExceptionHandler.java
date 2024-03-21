package de.muenchen.oss.wahllokalsystem.wls.common.exception.errorhandler;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.DTOMapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionKonstanten;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractExceptionHandler {

    private static final HttpStatus HTTP_STATUS_TECHNISCHER_FEHLER = HttpStatus.INTERNAL_SERVER_ERROR;
    private static final HttpStatus HTTP_STATUS_FACHLICHER_FEHLER = HttpStatus.BAD_REQUEST;
    private static final HttpStatus HTTP_STATUS_SICHERHEITSFEHLER = HttpStatus.FORBIDDEN;
    private static final HttpStatus HTTP_STATUS_INFRASTRUKTURELLER_FEHLER = HttpStatus.INTERNAL_SERVER_ERROR;

    private final DTOMapper dtoMapper;

    protected WlsExceptionDTO getWahlExceptionDTO(@NonNull final Throwable throwable) {
        log.debug("Throwable > {}", throwable.toString());
        final WlsExceptionDTO data;

        if (throwable instanceof WlsException wlsException) {
            data = dtoMapper.toDTO(wlsException);
        } else if (throwable instanceof AccessDeniedException) {
            data = createForAccessDeniedException(throwable);
        } else if (throwable instanceof HttpMessageNotReadableException) {
            data = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionKonstanten.CODE_HTTP_MESSAGE_NOT_READABLE, getService(),
                    String.format("HTTP-Nachricht nicht lesbar: %s", throwable.getMessage()));
        } else if (throwable instanceof TransientDataAccessException) {
            data = createForTransientException(throwable);
        } else {
            data = new WlsExceptionDTO(WlsExceptionCategory.T, ExceptionKonstanten.CODE_ALLGEMEIN_UNBEKANNT, getService(),
                    String.format("Ursache: %s, Nachricht: %s", throwable.getClass(), throwable.getMessage()));
        }

        log.debug("Erzeugte Fehlerdaten > {}", data);
        return data;
    }

    protected abstract String getService();

    protected WlsExceptionDTO createForTransientException(final Throwable throwable) {
        return new WlsExceptionDTO(WlsExceptionCategory.T, ExceptionKonstanten.CODE_TRANSIENT, getService(),
                String.format("Temporäres Problem, Ursache: %s, Nachricht: %s", throwable.getClass(), throwable.getMessage()));
    }

    protected WlsExceptionDTO createForAccessDeniedException(final Throwable throwable) {
        return new WlsExceptionDTO(WlsExceptionCategory.S, ExceptionKonstanten.CODE_SECURITY_ACCESS_DENIED, getService(), throwable.getMessage());
    }

    protected ResponseEntity<WlsExceptionDTO> createResponse(final WlsExceptionDTO wlsExceptionDTO) {
        return switch (wlsExceptionDTO.category()) {
        case T -> new ResponseEntity<>(wlsExceptionDTO,
                ExceptionKonstanten.CODE_TRANSIENT.equals(wlsExceptionDTO.code()) ? HttpStatus.CONFLICT : HTTP_STATUS_TECHNISCHER_FEHLER);
        case F -> ExceptionKonstanten.CODE_ENTITY_NOT_FOUND.equals(wlsExceptionDTO.code()) ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(wlsExceptionDTO, HTTP_STATUS_FACHLICHER_FEHLER);
        case I -> new ResponseEntity<>(wlsExceptionDTO,
                HTTP_STATUS_INFRASTRUKTURELLER_FEHLER);
        case S -> new ResponseEntity<>(wlsExceptionDTO, HTTP_STATUS_SICHERHEITSFEHLER);
        };
    }

}
