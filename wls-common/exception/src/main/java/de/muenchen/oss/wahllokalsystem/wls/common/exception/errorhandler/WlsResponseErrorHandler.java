package de.muenchen.oss.wahllokalsystem.wls.common.exception.errorhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.InfrastrukturelleWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.SicherheitsWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.builder.states.CodeIsSet;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionKonstanten;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.client.DefaultResponseErrorHandler;

@Slf4j
@RequiredArgsConstructor
@Getter
@Setter
public class WlsResponseErrorHandler extends DefaultResponseErrorHandler {

    private final ObjectMapper mapper;

    @Override
    public void handleError(@NonNull final ClientHttpResponse response) throws WlsException {
        final WlsException createdException;
        try {
            val wlsExceptionDTO = mapper.readValue(response.getBody(), WlsExceptionDTO.class);
            log.debug("HttpStatus: {} - {}", response.getStatusCode(), response.getStatusText());

            createdException = createException(wlsExceptionDTO);
            log.debug("Erstellte Exception: {}", createdException.toString());
        } catch (Exception e) {
            log.error("Beim Erzeugen der WLS-Exception kam es zu einem Fehler. Erzeuge einen Standard-WlsException", e);
            throw createUnknownTechnischeWlsExceptionWithCause(e);
        }

        throw createdException;
    }

    private TechnischeWlsException createUnknownTechnischeWlsExceptionWithCause(final Throwable cause) {
        return TechnischeWlsException.withCode(ExceptionKonstanten.CODE_ALLGEMEIN_UNBEKANNT).inService(ExceptionKonstanten.SERVICE_UNBEKANNT).withCause(cause)
                .buildWithMessage(buildUndefinedErrorMessageWithCauseMessages(cause));
    }

    private String buildUndefinedErrorMessageWithCauseMessages(final Throwable cause) {
        val sb = new StringBuilder(ExceptionKonstanten.MESSAGE_UNBEKANNTER_FEHLER);

        sb.append("\nWegen: ").append(cause.getClass());

        val message = cause.getMessage();
        if (!message.isBlank()) {
            sb.append(" mit:\n").append(message);
        }

        return sb.toString();
    }

    private WlsException createException(final WlsExceptionDTO wahlExceptionDTO) {
        val category = wahlExceptionDTO.category();
        log.debug("Erzeugen einer Exception aus der Kategorie: {}", category);

        return switch (category) {
        case F -> completeWithDTOData(FachlicheWlsException.withCode(wahlExceptionDTO.code()), wahlExceptionDTO);
        case I -> completeWithDTOData(InfrastrukturelleWlsException.withCode(wahlExceptionDTO.code()), wahlExceptionDTO);
        case S -> completeWithDTOData(SicherheitsWlsException.withCode(wahlExceptionDTO.code()), wahlExceptionDTO);
        case T -> completeWithDTOData(TechnischeWlsException.withCode(wahlExceptionDTO.code()), wahlExceptionDTO);
        };
    }

    private WlsException completeWithDTOData(final CodeIsSet<?> startedWlsExceptionCreation, final WlsExceptionDTO dtoData) {
        return startedWlsExceptionCreation.inService(dtoData.service()).buildWithMessage(dtoData.message());
    }
}
