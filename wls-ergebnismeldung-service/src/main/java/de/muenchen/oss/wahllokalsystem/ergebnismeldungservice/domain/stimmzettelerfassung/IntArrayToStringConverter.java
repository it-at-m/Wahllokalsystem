package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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
      // to get a modifiable list to work with
      return Arrays.stream(dbData.split(SEPARATOR))
          .map(Integer::parseInt)
          .collect(Collectors.toCollection(ArrayList::new));
    } else {
      return new ArrayList<>();
    }
  }
}
