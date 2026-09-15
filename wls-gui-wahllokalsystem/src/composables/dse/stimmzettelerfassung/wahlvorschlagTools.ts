import type { Wahlvorschlag as PersistedWahlvorschlag } from "@/types/dse/persistedStimmzettel/Wahlvorschlag.ts";

import { useKandidatTools } from "@/composables/dse/stimmzettelerfassung/kandidatTools.ts";

const { sortKandidaten } = useKandidatTools();

export function useWahlvorschlagTools() {
  function sortWahlvorschlaege(wahlvorschlaege: PersistedWahlvorschlag[]) {
    return wahlvorschlaege
      .slice()
      .sort((x, y) => x.wahlvorschlagID.localeCompare(y.wahlvorschlagID))
      .map(
        (wv) =>
          ({
            wahlvorschlagID: wv.wahlvorschlagID,
            selected: wv.selected,
            kandidaten: sortKandidaten(wv.kandidaten ?? []),
          }) as PersistedWahlvorschlag
      );
  }

  return {
    sortWahlvorschlaege,
  };
}
