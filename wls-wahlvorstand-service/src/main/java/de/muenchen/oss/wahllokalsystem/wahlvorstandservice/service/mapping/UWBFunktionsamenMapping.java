package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.mapping;

import lombok.Getter;
import org.mapstruct.Mapper;

import java.util.HashMap;
import java.util.Map;

@Getter
@Mapper
public class UWBFunktionsamenMapping {
    private final Map<String, Map<String, String>> uwbFunktion = new HashMap<>();

}
