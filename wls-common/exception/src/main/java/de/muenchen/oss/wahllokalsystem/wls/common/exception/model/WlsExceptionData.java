package de.muenchen.oss.wahllokalsystem.wls.common.exception.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WlsExceptionData {

    private String message;
    private String serviceName;
    private String code;
    private Throwable cause;
}
