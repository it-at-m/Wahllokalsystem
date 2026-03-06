package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettel;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

@Converter
public class IntArrayToStringConverter implements AttributeConverter<List<Integer>, String> {

  private static final String SEPARATOR = ";";

  @Override
  public String convertToDatabaseColumn(List<Integer> attribute) {
    return StringUtils.join(attribute, SEPARATOR);
  }

  @Override
  public List<Integer> convertToEntityAttribute(String dbData) {
    if (StringUtils.isNotBlank(dbData)) {
      return Arrays.stream(dbData.split(",")).map(Integer::parseInt).toList();
    } else {
      return Collections.emptyList();
    }
  }
}
