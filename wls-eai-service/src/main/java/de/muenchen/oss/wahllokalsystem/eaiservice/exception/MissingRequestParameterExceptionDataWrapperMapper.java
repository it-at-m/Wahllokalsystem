package de.muenchen.oss.wahllokalsystem.eaiservice.exception;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.WahldatenController;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionDataWrapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
public class MissingRequestParameterExceptionDataWrapperMapper {

    private static final String WAHLDATEN_SERVLET_PATH = WahldatenController.WAHLDATEN_REQUEST_MAPPING;

    private static final String PATTERN_ANY_OTHER_CHAR = ".*";

    final Map<String, Map<Pattern, ExceptionDataWrapper>> mapMissingRequestParameters = new HashMap<>();

    {
        addExceptionDataWrapper("includingSince", WAHLDATEN_SERVLET_PATH + "/wahltage", ExceptionConstants.LOADWAHLTAGE_TAG_FEHLT);
        addExceptionDataWrapper("forDate", WAHLDATEN_SERVLET_PATH + "/wahlbezirk", ExceptionConstants.LOADWAHLBEZIRKE_WAHLTAG_FEHLT);
        addExceptionDataWrapper("withNummer", WAHLDATEN_SERVLET_PATH + "/wahlbezirk", ExceptionConstants.LOADWAHLBEZIRKE_NUMMER_FEHLT);
        addExceptionDataWrapper("forDate", WAHLDATEN_SERVLET_PATH + "/wahlen", ExceptionConstants.LOADWAHLEN_WAHLTAG_FEHLT);
        addExceptionDataWrapper("withNummer", WAHLDATEN_SERVLET_PATH + "/wahlen", ExceptionConstants.LOADWAHLEN_NUMMER_FEHLT);
        addExceptionDataWrapper("forDate", WAHLDATEN_SERVLET_PATH + "/basisdaten", ExceptionConstants.LOADBASISDATEN_TAG_FEHLT);
        addExceptionDataWrapper("withNummer", WAHLDATEN_SERVLET_PATH + "/basisdaten", ExceptionConstants.LOADBASISDATEN_NUMMER_FEHLT);
    }

    public Optional<ExceptionDataWrapper> getExceptionDataWrapperForMissingRequestParameterByName(final String servletPath, final String requestParameterName) {
        val mappingsForParameter = mapMissingRequestParameters.get(requestParameterName);
        if (mappingsForParameter == null) {
            return Optional.empty();
        }

        val patternMapping = mappingsForParameter.keySet().stream().filter(pattern -> pattern.matcher(servletPath).matches()).findFirst();
        return patternMapping.map(mappingsForParameter::get);
    }

    private void addExceptionDataWrapper(final String parameterName, final String pathPattern, final ExceptionDataWrapper exceptionDataWrapper) {
        val mapForParameter = mapMissingRequestParameters.getOrDefault(parameterName, new HashMap<>());

        mapForParameter.put(Pattern.compile(pathPattern), exceptionDataWrapper);

        mapMissingRequestParameters.put(parameterName, mapForParameter);
    }
}
