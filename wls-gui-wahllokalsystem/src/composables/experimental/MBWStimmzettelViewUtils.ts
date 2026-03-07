import type { StimmzettelSnapshot } from "@/types/experimental/StimmzettelSnapshot.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { ref } from "vue";

import { useStimmzettelService } from "@/composables/experimental/stimmzettelService.ts";
import { useWahlvorschlaegeService } from "@/composables/wahlvorschlaege/wahlvorschlaegeService.ts";
import { useWahlvorschlagUtils } from "@/composables/wahlvorschlaege/wahlvorschlagUtils.ts";

export function useMBWStimmzettelViewUtils(
  wahlID: string,
  wahlbezirkID: string
) {
  const { getWahlvorschlaege } = useWahlvorschlaegeService();
  const {
    sortWahlvorschlaegeByOrdnungszahl,
    compareKandidatenByListenPosition,
  } = useWahlvorschlagUtils();
  const { postStimmzettel, getStimmzettel } = useStimmzettelService();

  const isLoadingWahlvorschlaege = ref(false);
  const isSavingStimmzettel = ref(false);
  const isLoadingStimmzettel = ref(false);

  async function loadWahlvorschlaege() {
    isLoadingWahlvorschlaege.value = true;
    try {
      const result = await _loadAndSortWahlvorschlaege();
      sortWahlvorschlaegeByOrdnungszahl(result);
      return result;
    } finally {
      isLoadingWahlvorschlaege.value = false;
    }
  }

  async function loadStimmzettel() {
    isLoadingStimmzettel.value = true;
    try {
      const loadedStimmzettel = await getStimmzettel(wahlID, wahlbezirkID);
      return loadedStimmzettel ?? [];
    } finally {
      isLoadingStimmzettel.value = false;
    }
  }

  async function saveStimmzettel(stimmzettel: StimmzettelSnapshot[]) {
    isSavingStimmzettel.value = true;
    try {
      await postStimmzettel(wahlID, wahlbezirkID, stimmzettel);
    } finally {
      isSavingStimmzettel.value = false;
    }
  }

  //TODO das gleiche wie in den mbwStapelBC-Utils. Sollte zentral zur Verfügung stehen, muss aber nicht bei jedem Load verwendet werden
  async function _loadAndSortWahlvorschlaege() {
    return getWahlvorschlaege(wahlID, wahlbezirkID)
      .then((wahlvorschlaege) =>
        sortWahlvorschlaegeByOrdnungszahl(wahlvorschlaege)
      )
      .then((wahlvorschlaege) => {
        wahlvorschlaege.wahlvorschlaege.forEach((wahlvorschlag) => {
          _sortKandidatenByListenPosition(wahlvorschlag);
        });
        return wahlvorschlaege;
      });
  }

  function _sortKandidatenByListenPosition(wahlvorschlag: Wahlvorschlag) {
    if (wahlvorschlag.kandidaten) {
      wahlvorschlag.kandidaten = [...wahlvorschlag.kandidaten].sort(
        compareKandidatenByListenPosition
      );
    }
  }

  return {
    isLoadingStimmzettel,
    isLoadingWahlvorschlaege,
    isSavingStimmzettel,

    loadStimmzettel,
    loadWahlvorschlaege,
    saveStimmzettel,
  };
}
