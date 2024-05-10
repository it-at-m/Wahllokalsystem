package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.konfiguration.Konfiguration;
import java.util.Map;
import java.util.Optional;
import lombok.val;
import org.mapstruct.Mapper;

@Mapper
public interface KonfigurationModelMapper {

    KonfigurationModel toModel(Konfiguration konfigurationEntity);

    Map<KonfigurationKonfigKey, Map<WahlbezirkArt, KonfigurationKonfigKey>> alternativeKeys = Map.of(
            KonfigurationKonfigKey.FRUEHESTE_EROEFFNUNGSZEIT, Map.of(WahlbezirkArt.UWB, KonfigurationKonfigKey.FRUEHESTE_EROEFFNUNGSZEIT_UW, WahlbezirkArt.BWB,
                    KonfigurationKonfigKey.FRUEHESTE_EROEFFNUNGSZEIT_BW),
            KonfigurationKonfigKey.SPAETESTE_EROEFFNUNGSZEIT, Map.of(WahlbezirkArt.UWB, KonfigurationKonfigKey.SPAETESTE_EROEFFNUNGSZEIT_UW, WahlbezirkArt.BWB,
                    KonfigurationKonfigKey.SPAETESTE_EROEFFNUNGSZEIT_BW),
            KonfigurationKonfigKey.FRUEHESTE_SCHLIESSUNGSZEIT, Map.of(WahlbezirkArt.UWB, KonfigurationKonfigKey.FRUEHESTE_EROEFFNUNGSZEIT_UW, WahlbezirkArt.BWB,
                    KonfigurationKonfigKey.FRUEHESTE_SCHLIESSUNGSZEIT_BW)
    );

    default Optional<KonfigurationKonfigKey> getAlternativKey(final KonfigurationKonfigKey konfigKey, final WahlbezirkArt wahlbezirkArt) {
        val alternativKey = alternativeKeys.get(konfigKey);
        if (alternativKey == null) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(alternativKey.get(wahlbezirkArt));
        }
    }
}
