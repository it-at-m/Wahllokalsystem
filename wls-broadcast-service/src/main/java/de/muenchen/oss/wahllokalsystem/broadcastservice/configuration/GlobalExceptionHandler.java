package de.muenchen.oss.wahllokalsystem.broadcastservice.configuration;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.errorhandler.AbstractExceptionHandler;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.DTOMapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends AbstractExceptionHandler {

    @Value("${service.info.oid}")
    String serviceOid;

    public GlobalExceptionHandler(DTOMapper mapper) {
        super(mapper);
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException.class)
    public WlsException handleException(Exception throwable) {
        WlsException wlsException;
        try {
            WlsExceptionDTO wlsExceptionDTO = getWahlExceptionDTO(throwable);
            wlsException = TechnischeWlsException.withCode(wlsExceptionDTO.code()).inService(getService()).withCause(throwable)
                    .buildWithMessage(wlsExceptionDTO.message());
        } catch (WlsException e) {
            wlsException = e;
        }
        return wlsException;
    }

    @Override
    protected String getService() {
        return this.serviceOid;
    }
}
