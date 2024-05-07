package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration;

import java.util.Map;
import java.util.Optional;
import lombok.val;
import org.mapstruct.Mapper;

@Mapper
public interface KonfigurationModelMapper {

    Map<KonfigurationKonfigKey, Map<WahlbezirkArt, KonfigurationKonfigKey>> alternativeKeys = Map.of(
            KonfigurationKonfigKey.FRUEHESTE_EROEFFNUNGSZEIT, Map.of(WahlbezirkArt.UWB, KonfigurationKonfigKey.FRUEHESTE_EROEFFNUNGSZEIT_UW, WahlbezirkArt.BWB,
                    KonfigurationKonfigKey.FRUEHESTE_EROEFFNUNGSZEIT_BW),
            KonfigurationKonfigKey.SPAETESTE_EROEFFNUNGSZEIT, Map.of(WahlbezirkArt.UWB, KonfigurationKonfigKey.SPAETESTE_EROEFFNUNGSZEIT_UW, WahlbezirkArt.BWB,
                    KonfigurationKonfigKey.SPAETESTE_EROEFFNUNGSZEIT_BW),
            KonfigurationKonfigKey.FRUEHESTE_SCHLIESSUNGSZEIT, Map.of(WahlbezirkArt.UWB, KonfigurationKonfigKey.FRUEHESTE_EROEFFNUNGSZEIT_UW, WahlbezirkArt.BWB,
                    KonfigurationKonfigKey.FRUEHESTE_SCHLIESSUNGSZEIT_BW)
    );

    default String toString(final KonfigurationKonfigKey konfigKey, final WahlbezirkArt wahlbezirkArt) {
        val alternativKey = getAlternativKey(konfigKey, wahlbezirkArt);

        return alternativKey.orElse(konfigKey).name();

    }

    default Optional<KonfigurationKonfigKey> getAlternativKey(final KonfigurationKonfigKey konfigKey, final WahlbezirkArt wahlbezirkArt) {
        val alternativKey = alternativeKeys.get(konfigKey);
        return Optional.ofNullable(alternativKey.get(wahlbezirkArt));
    }
}
