package de.muenchen.refarch.gateway.configuration;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import edu.umd.cs.findbugs.annotations.SuppressMatchType;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("refarch.security")
public class SecurityProperties {
    /**
     * List of url patterns excluded from csrf protection.
     */
    @SuppressFBWarnings(value = "EI_EXPOSE_REP", matchType = SuppressMatchType.EXACT)
    private List<String> csrfWhitelisted = List.of();
}
