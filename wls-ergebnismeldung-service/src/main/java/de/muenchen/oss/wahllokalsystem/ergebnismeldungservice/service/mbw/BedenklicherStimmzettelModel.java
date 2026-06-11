package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw;

import java.util.Set;

public record BedenklicherStimmzettelModel(
    int orderIndex, Set<SupplementModel> supplements, ValidityModel validity) {}
