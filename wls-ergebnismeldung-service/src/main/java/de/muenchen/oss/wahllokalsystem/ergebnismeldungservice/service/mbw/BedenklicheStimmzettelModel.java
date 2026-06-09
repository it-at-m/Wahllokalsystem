package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw;

import java.util.Set;

public record BedenklicheStimmzettelModel(
    int orderIndex, Set<SupplementModel> supplements, ValidityModel validity) {}
