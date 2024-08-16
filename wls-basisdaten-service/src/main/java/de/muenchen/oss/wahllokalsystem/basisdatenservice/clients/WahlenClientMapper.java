package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.ObjectPropertyChecker;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface WahlenClientMapper {

    WahlenClientMapper INSTANCE = Mappers.getMapper(WahlenClientMapper.class);

    @Mapping(target = "waehlerverzeichnisnummer", source = ".", qualifiedByName = "setWZeroIfNotExisting")
    @Mapping(target = "reihenfolge", source = ".", qualifiedByName = "setRZeroIfNotExisting")
    @Mapping(target = "farbe", ignore = true)
    @Mapping(target = "wahlID", source = "identifikator")
    WahlModel toModel(WahlDTO wahlDTO);

    @Named("setWZeroIfNotExisting")
    default Long setWZeroIfNotExisting(final WahlDTO wahlDTO) throws NoSuchFieldException, IllegalAccessException {
        if (ObjectPropertyChecker.objectHasProperty(wahlDTO, "waehlerverzeichnisnummer")) {
            ObjectPropertyChecker.getValueFromField(wahlDTO, "waehlerverzeichnisnummer");
        }
        return 0L;
    }

    @Named("setRZeroIfNotExisting")
    default Long setRZeroIfNotExisting(final WahlDTO wahlDTO) throws NoSuchFieldException, IllegalAccessException {
        if (ObjectPropertyChecker.objectHasProperty(wahlDTO, "reihenfolge")) {
            ObjectPropertyChecker.getValueFromField(wahlDTO, "reihenfolge");
        }
        return 0L;
    }

    List<WahlModel> fromRemoteClientSetOfWahlDTOtoListOfWahlModel(Set<WahlDTO> wahlDTO);
}
