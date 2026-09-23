import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";

import { useKandidatTools } from "@/composables/dse/stimmzettelerfassung/kandidatTools.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";

export function usePersistedStimmzettelTools() {
  const { hasOnlyReststimme } = useKandidatTools();

  function matchesMBWStapelA(stimmzettel: PersistedStimmzettel) {
    return (
      stimmzettel.gueltigkeit === StimmzettelGueltigkeitEnum.Valid &&
      stimmzettel.invalideVotes === 0 &&
      stimmzettel.wahlvorschlaege.length === 1 &&
      stimmzettel.wahlvorschlaege.every((wahlvorschlag) =>
        wahlvorschlag.kandidaten.every(hasOnlyReststimme)
      )
    );
  }

  function matchesMBWStapelB(stimmzettel: PersistedStimmzettel) {
    return (
      stimmzettel.gueltigkeit === StimmzettelGueltigkeitEnum.Valid &&
      stimmzettel.wahlvorschlaege.length === 1 &&
      stimmzettel.wahlvorschlaege.every((wahlvorschlag) =>
        wahlvorschlag.kandidaten.some(
          (kandidat) => !hasOnlyReststimme(kandidat)
        )
      )
    );
  }

  function matchesMBWStapelBC(stimmzettel: PersistedStimmzettel) {
    return (
      stimmzettel.gueltigkeit === StimmzettelGueltigkeitEnum.Valid &&
      !matchesMBWStapelA(stimmzettel)
    );
  }

  function matchesMBWStapelDUngueltig(stimmzettel: PersistedStimmzettel) {
    return (
      stimmzettel.gueltigkeit === StimmzettelGueltigkeitEnum.Leer ||
      (stimmzettel.gueltigkeit === StimmzettelGueltigkeitEnum.Invalid &&
        !stimmzettel.beschlussfassung)
    );
  }

  function matchesMBWStapelEUngueltig(stimmzettel: PersistedStimmzettel) {
    return (
      stimmzettel.gueltigkeit === StimmzettelGueltigkeitEnum.Leer ||
      (stimmzettel.gueltigkeit === StimmzettelGueltigkeitEnum.Invalid &&
        !!stimmzettel.beschlussfassung)
    );
  }

  return {
    matchesMBWStapelA,
    matchesMBWStapelB,
    matchesMBWStapelBC,
    matchesMBWStapelDUngueltig,
    matchesMBWStapelEUngueltig,
  };
}
