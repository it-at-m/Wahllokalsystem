package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

public class LocalDateTimeComparators {

    public static final Comparator<LocalDateTime> PRECISION_MILLISECONDS = Comparator.comparing(dateTime -> dateTime.truncatedTo(ChronoUnit.MILLIS));
}
