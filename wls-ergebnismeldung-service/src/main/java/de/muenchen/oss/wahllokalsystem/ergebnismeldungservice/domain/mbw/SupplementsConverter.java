package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
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
      return new HashSet<>();
    }

    val dbDataSplitted = dbData.split(SPLIT_CHAR);
    val result = new HashSet<Supplement>(dbDataSplitted.length);
    Arrays.stream(dbDataSplitted)
        .filter(StringUtils::isNotBlank)
        .map(Supplement::valueOf)
        .forEach(result::add);
    return result;
  }
}
