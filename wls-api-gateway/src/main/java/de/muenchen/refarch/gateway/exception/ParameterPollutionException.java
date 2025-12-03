package de.muenchen.refarch.gateway.exception;

import de.muenchen.refarch.gateway.filter.GlobalRequestParameterPollutionFilter;
import java.io.Serial;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Used in {@link GlobalRequestParameterPollutionFilter} to signal a possible parameter pollution
 * attack.
 */
@ResponseStatus(
        code = HttpStatus.BAD_REQUEST,
        reason = "parameter pollution"
)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ParameterPollutionException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    // default Ctor
}
