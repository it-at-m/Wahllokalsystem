package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.mapping;

import lombok.Getter;
import org.mapstruct.Mapper;

import java.util.HashMap;
import java.util.Map;

@Getter
@Mapper
public class BWBFunktionsnamenmapping {
    private final Map<String, Map<String, String>> bwbFunktion = new HashMap<>();

}
