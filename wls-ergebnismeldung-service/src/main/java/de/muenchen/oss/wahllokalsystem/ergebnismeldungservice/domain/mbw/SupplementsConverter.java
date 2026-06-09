package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

@Converter
public class SupplementsConverter implements AttributeConverter<Set<Supplement>, String> {

  private static final String SPLIT_CHAR = ",";

  @Override
  public String convertToDatabaseColumn(Set<Supplement> attribute) {
    return StringUtils.join(attribute, SPLIT_CHAR);
  }

  @Override
  public Set<Supplement> convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return Collections.emptySet();
    }

    val dbDataSplitted = dbData.split(SPLIT_CHAR);
    return Arrays.stream(dbDataSplitted)
        .filter(StringUtils::isNotBlank)
        .map(Supplement::valueOf)
        .collect(Collectors.toSet());
  }
}
