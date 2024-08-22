package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.errorhandler.AbstractExceptionHandler;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.DTOMapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ServiceIDFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends AbstractExceptionHandler {

    private final ServiceIDFormatter serviceIDFormatter;

    // todo: could not autowire fehlermeldung bei dtoMapper ignorieren weil bei anderen auch?
    public GlobalExceptionHandler(final ServiceIDFormatter serviceIDFormatter, final DTOMapper dtoMapper) {
        super(dtoMapper);
        this.serviceIDFormatter = serviceIDFormatter;
    }

    @ExceptionHandler
    public ResponseEntity<WlsExceptionDTO> handleThrowables(final Throwable throwable) {
        log.info("handling throwable", throwable);
        return createResponse(getWahlExceptionDTO(throwable));
    }

    @Override
    protected String getService() {
        return serviceIDFormatter.getId();
    }
}