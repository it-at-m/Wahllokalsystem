package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.konfiguration.Konfiguration;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KennbuchstabenListeModel;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KennbuchstabenListenModel;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KennbuchstabenModel;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KonfigurationKonfigKey;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KonfigurationModel;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KonfigurationSetModel;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.WahlbezirkArt;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface KonfigurationModelMapper {

    KonfigurationModel toModel(Konfiguration konfigurationEntity);

    @Mapping(target = "standardwert", source = ".", qualifiedByName = "mapStandardwertFromModel")
    Konfiguration toEntity(KonfigurationSetModel konfigurationSetModel);

    Map<KonfigurationKonfigKey, Map<WahlbezirkArt, KonfigurationKonfigKey>> alternativeKeys = Map.of(
            KonfigurationKonfigKey.FRUEHESTE_EROEFFNUNGSZEIT, Map.of(WahlbezirkArt.UWB, KonfigurationKonfigKey.FRUEHESTE_EROEFFNUNGSZEIT_UW, WahlbezirkArt.BWB,
                    KonfigurationKonfigKey.FRUEHESTE_EROEFFNUNGSZEIT_BW),
            KonfigurationKonfigKey.SPAETESTE_EROEFFNUNGSZEIT, Map.of(WahlbezirkArt.UWB, KonfigurationKonfigKey.SPAETESTE_EROEFFNUNGSZEIT_UW, WahlbezirkArt.BWB,
                    KonfigurationKonfigKey.SPAETESTE_EROEFFNUNGSZEIT_BW),
            KonfigurationKonfigKey.FRUEHESTE_SCHLIESSUNGSZEIT, Map.of(WahlbezirkArt.UWB, KonfigurationKonfigKey.FRUEHESTE_EROEFFNUNGSZEIT_UW, WahlbezirkArt.BWB,
                    KonfigurationKonfigKey.FRUEHESTE_SCHLIESSUNGSZEIT_BW));

    char KENNBUCHSTABEN_LISTEN_SEPARATOR = '$';
    char KENNBUCHSTABEN_LISTE_SEPARATOR = ';';
    char KENNBUCHSTABEN_SEPARATOR = ',';

    default Optional<KonfigurationKonfigKey> getAlternativKey(final KonfigurationKonfigKey konfigKey, final WahlbezirkArt wahlbezirkArt) {
        val alternativKey = alternativeKeys.get(konfigKey);
        if (alternativKey == null) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(alternativKey.get(wahlbezirkArt));
        }
    }

    @Named("mapStandardwertFromModel")
    default String mapStandardwertFromModel(final KonfigurationSetModel konfigurationSetModel) {
        if (konfigurationSetModel.standardwert() != null) {
            return konfigurationSetModel.standardwert();
        } else {
            return konfigurationSetModel.wert();
        }
    }

    default KennbuchstabenListenModel toKennbuchstabenListenModel(final String kennbuchstabenListenAsString) {
        val stringSeparated = StringUtils.split(kennbuchstabenListenAsString, KENNBUCHSTABEN_LISTEN_SEPARATOR);
        val listen = Arrays.stream(stringSeparated).map(this::toKennbuchstabenListeModel).toList();

        return new KennbuchstabenListenModel(listen);
    }

    default KennbuchstabenListeModel toKennbuchstabenListeModel(final String kennbuchstabenListeAsString) {
        val stringSeparated = StringUtils.split(kennbuchstabenListeAsString, KENNBUCHSTABEN_LISTE_SEPARATOR);
        val listen = Arrays.stream(stringSeparated).map(this::toKennbuchstabenModel).toList();

        return new KennbuchstabenListeModel(listen);
    }

    default KennbuchstabenModel toKennbuchstabenModel(final String kennbuchstabenAsString) {
        return new KennbuchstabenModel(List.of(StringUtils.split(kennbuchstabenAsString, KENNBUCHSTABEN_SEPARATOR)));
    }
}
