import type { Wahlvorschlag as PersistedWahlvorschlag } from "@/types/dse/persistedStimmzettel/Wahlvorschlag.ts";

import { useKandidatTools } from "@/composables/dse/stimmzettelerfassung/kandidatTools.ts";

const { sortAndDeepCloneKandidaten } = useKandidatTools();

export function useWahlvorschlagTools() {
  function sortWahlvorschlaege(wahlvorschlaege: PersistedWahlvorschlag[]) {
    return wahlvorschlaege
      .slice()
      .sort((wahlvorschlag1, wahlvorschlag2) =>
        _compareWahlvorschlaegeById(wahlvorschlag1, wahlvorschlag2)
      )
      .map((wv) => ({
        wahlvorschlagID: wv.wahlvorschlagID,
        selected: wv.selected,
        kandidaten: sortAndDeepCloneKandidaten(wv.kandidaten ?? []),
      }));
  }

  function _compareWahlvorschlaegeById(
    wv1: PersistedWahlvorschlag,
    wv2: PersistedWahlvorschlag
  ) {
    return wv1.wahlvorschlagID.localeCompare(wv2.wahlvorschlagID);
  }

  return {
    sortWahlvorschlaege,
  };
}
