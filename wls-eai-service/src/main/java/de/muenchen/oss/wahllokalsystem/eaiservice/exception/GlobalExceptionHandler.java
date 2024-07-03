package de.muenchen.oss.wahllokalsystem.eaiservice.exception;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.common.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.errorhandler.AbstractExceptionHandler;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.DTOMapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionDataWrapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ServiceIDFormatter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends AbstractExceptionHandler {
    private final ServiceIDFormatter serviceIDFormatter;

    private final MissingRequestParameterExceptionDataWrapperMapper missingRequestParameterExceptionDataWrapperMapper;

    public GlobalExceptionHandler(final ServiceIDFormatter serviceIDFormatter, final DTOMapper dtoMapper,
            final MissingRequestParameterExceptionDataWrapperMapper missingRequestParameterExceptionDataWrapperMappers) {
        super(dtoMapper);
        this.serviceIDFormatter = serviceIDFormatter;
        this.missingRequestParameterExceptionDataWrapperMapper = missingRequestParameterExceptionDataWrapperMappers;
    }

    @ExceptionHandler
    public ResponseEntity<WlsExceptionDTO> handleThrowables(final Throwable throwable) {
        log.info("handling throwable", throwable);
        return createResponse(getWahlExceptionDTO(throwable));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public WlsExceptionDTO handleThrowable(final MissingServletRequestParameterException missingRequestParameter,
            final ServletWebRequest webRequest) {
        val requestParameterName = missingRequestParameter.getParameterName();
        val requestURI = webRequest.getRequest().getRequestURI();

        log.info("missing request parameter -> {}", requestParameterName);
        log.info("path > {}", requestURI);

        return missingRequestParameterExceptionDataWrapperMapper.getExceptionDataWrapperForMissingRequestParameterByName(requestURI, requestParameterName)
                .map(this::createFachlicheWlsExceptionDTO).orElseGet(this::createGenericMissingParameterExceptionDTO);
    }

    @ExceptionHandler
    public ResponseEntity<Void> handleNotFoundException(final NotFoundException notFoundException) {
        log.debug("not found entity {} with id {}", notFoundException.getEntityClass(), notFoundException.getRequestedID());
        return ResponseEntity.notFound().build();
    }

    @Override
    protected String getService() {
        return serviceIDFormatter.getId();
    }

    private WlsExceptionDTO createGenericMissingParameterExceptionDTO() {
        return createFachlicheWlsExceptionDTO(ExceptionConstants.DATENALLGEMEIN_PARAMETER_FEHLEN);
    }

    private WlsExceptionDTO createFachlicheWlsExceptionDTO(final ExceptionDataWrapper exceptionDataWrapper) {
        return new WlsExceptionDTO(WlsExceptionCategory.F, exceptionDataWrapper.code(), getService(), exceptionDataWrapper.message());
    }
}
