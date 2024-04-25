package de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DTOMapper {

    @Mapping(target = "service", source = "serviceName")
    WlsExceptionDTO toDTO(WlsException wlsException);

    default WlsExceptionCategory toDTOCategory(final de.muenchen.oss.wahllokalsystem.wls.common.exception.model.WlsExceptionCategory exceptionCategory) {
        if (exceptionCategory == null) {
            return null;
        }
        return switch (exceptionCategory) {
        case FACHLICH -> WlsExceptionCategory.F;
        case INFRASTRUKTUR -> WlsExceptionCategory.I;
        case SECURITY -> WlsExceptionCategory.S;
        case TECHNISCH -> WlsExceptionCategory.T;
        };
    }

    //WlsException fromWlsExceptionDTOtoWlsException(WlsExceptionDTO wlsExceptionDTO);
}
