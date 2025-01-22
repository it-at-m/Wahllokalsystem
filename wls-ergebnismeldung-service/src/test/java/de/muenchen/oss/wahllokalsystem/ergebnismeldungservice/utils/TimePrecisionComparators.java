package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

public class TimePrecisionComparators {

    public static final Comparator<LocalDateTime> LOCAL_DATE_TIME_PRECISION_MILLISECONDS = Comparator
            .comparing(dateTime -> dateTime.truncatedTo(ChronoUnit.MILLIS));
    public static final Comparator<Instant> INSTANT_PRECISION_MILLISECONDS = Comparator.comparing(instant -> instant.truncatedTo(ChronoUnit.MILLIS));
}
