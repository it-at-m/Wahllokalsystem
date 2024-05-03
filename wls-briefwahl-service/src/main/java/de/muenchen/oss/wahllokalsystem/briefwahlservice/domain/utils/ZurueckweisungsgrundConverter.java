package de.muenchen.oss.wahllokalsystem.briefwahlservice.domain.utils;

import de.muenchen.oss.wahllokalsystem.briefwahlservice.common.beanstandetewahlbriefe.Zurueckweisungsgrund;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

@Converter
public class ZurueckweisungsgrundConverter implements AttributeConverter<Zurueckweisungsgrund[], String> {

    private static final String SPLIT_CHAR = ",";

    @Override
    public String convertToDatabaseColumn(Zurueckweisungsgrund[] enumArray) {
        return StringUtils.join(enumArray, SPLIT_CHAR);
    }

    @Override
    public Zurueckweisungsgrund[] convertToEntityAttribute(String dbData) {
        val dbDataSplitted = dbData.split(SPLIT_CHAR);
        return Arrays.stream(dbDataSplitted).map(Zurueckweisungsgrund::valueOf)
                .toArray(Zurueckweisungsgrund[]::new);
    }
}
