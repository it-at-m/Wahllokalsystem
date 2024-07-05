package de.muenchen.oss.wahllokalsystem.eaiservice.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.errorhandler.AbstractExceptionHandler;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.DTOMapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ServiceIDFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends AbstractExceptionHandler {
    private final ServiceIDFormatter serviceIDFormatter;

    public GlobalExceptionHandler(final ServiceIDFormatter serviceIDFormatter, final DTOMapper dtoMapper) {
        super(dtoMapper);
        this.serviceIDFormatter = serviceIDFormatter;
    }

    @ExceptionHandler
    public ResponseEntity<WlsExceptionDTO> handleThrowables(final Throwable throwable) {
        log.info("handling throwable", throwable);
        return createResponse(getWahlExceptionDTO(throwable));
    }

    @ExceptionHandler
    public ResponseEntity<Void> handleNotFoundException(final NotFoundException notFoundException) {
        log.debug("not found entity {} with id {}", notFoundException.getEntityClass(), notFoundException.getRequestedID());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler
    public ResponseEntity<Void> handleNoSearchResultFoundException(final NoSearchResultFoundException noSearchResultFoundException) {
        log.debug("no search result of {} found for search parameter {}", noSearchResultFoundException.getNotFoundEntityClass(),
            noSearchResultFoundException.getSearchParameter());
        return ResponseEntity.notFound().build();
    }

    @Override
    protected String getService() {
        return serviceIDFormatter.getId();
    }
}
