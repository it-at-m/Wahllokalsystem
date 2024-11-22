package de.muenchen.oss.wahllokalsystem.authservice.client;

import de.muenchen.oss.wahllokalsystem.authservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.authservice.eai.infomanagement.client.KonfigurationControllerApi;
import de.muenchen.oss.wahllokalsystem.authservice.eai.infomanagement.model.KonfigurationDTO;
import de.muenchen.oss.wahllokalsystem.authservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.authservice.security.LegalLoginInterval;
import de.muenchen.oss.wahllokalsystem.authservice.security.LoginTimeClient;
import de.muenchen.oss.wahllokalsystem.authservice.service.WelcomeClient;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(Profiles.NOT + Profiles.DUMMY_CLIENTS)
@RequiredArgsConstructor
@Slf4j
public class InfomanagementServiceClient implements WelcomeClient, LoginTimeClient {

    private final ExceptionFactory exceptionFactory;

    private final KonfigurationControllerApi konfigurationControllerApi;

    @Value("${service.config.clients.infomanagement.configkey.welcomeMessage}")
    String konfigKeyWelcomeMessage;

    @Value("${service.config.clients.infomanagement.configkey.fruehesterLogin}")
    String konfigKeyFruehesterLogin;

    @Value("${service.config.clients.infomanagement.configkey.spaetesterLogin}")
    String konfigKeySpaetesterLogin;

    @Value("${service.config.welcomemessage.default}")
    String defaultWelcomeMessage;

    @Value("${service.config.clients.infomanagement.dateformat}")
    String konfigDateFormat;

    @Override
    public String getWelcomeMessage() {
        final KonfigurationDTO konfigurationDTO;
        try {
            konfigurationDTO = getKonfigurationKeyUnauthorized(konfigKeyWelcomeMessage);
        } catch (final Exception e) {
            log.warn("Fehler <{}> bei Abruf der Willkommensnachricht. Nutze Fallback: {}", e.getMessage(), defaultWelcomeMessage);
            return defaultWelcomeMessage;
        }

        if (konfigurationDTO == null) {
            return defaultWelcomeMessage;
        } else {
            return getValueOrDefault(konfigurationDTO);
        }
    }

    @Override
    public LegalLoginInterval getLegalLoginInterval() {
        val fruehesterLogin = getKonfigurationKeyUnauthorized(konfigKeyFruehesterLogin).getWert();
        val spaetesterLogin = getKonfigurationKeyUnauthorized(konfigKeySpaetesterLogin).getWert();

        return new LegalLoginInterval(parseToDateTime(fruehesterLogin), parseToDateTime(spaetesterLogin));
    }

    private LocalDateTime parseToDateTime(final String dateTimeString) {
        if (dateTimeString == null) {
            return null;
        }

        try {
            return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern(konfigDateFormat));
        } catch (final DateTimeParseException e1) {
            throw new DateTimeException(
                    "Unable to construct a time or a date-time from the given configuration (\"" + dateTimeString + "\").");
        }
    }

    private KonfigurationDTO getKonfigurationKeyUnauthorized(final String configKey) {
        try {
            return konfigurationControllerApi.getKonfigurationUnauthorized(configKey);
        } catch (final WlsException wlsException) {
            throw wlsException;
        } catch (final Exception e) {
            log.warn("#getKonfigurationUnauthorized got exception", e);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_KONFIGSERVICE);
        }
    }

    private String getValueOrDefault(final KonfigurationDTO konfigurationDTO) {
        if (StringUtils.isBlank(konfigurationDTO.getWert())) {
            return konfigurationDTO.getStandardwert();
        } else {
            return konfigurationDTO.getWert();
        }
    }
}
