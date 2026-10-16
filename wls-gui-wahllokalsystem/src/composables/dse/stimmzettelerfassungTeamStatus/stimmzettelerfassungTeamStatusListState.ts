import type { StimmzettelerfassungTeamStatusEntry } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEntry.ts";

import { readonly, ref } from "vue";

import { useStimmzettelerfassungTeamStatusEntryTools } from "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusEntryTools.ts";
import { useStimmzettelerfassungTeamStatusService } from "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusService.ts";

export function useStimmzettelerfassungTeamStatusListState(
  wahlID: string,
  wahlbezirkID: string
) {
  const { compareByTeamID } = useStimmzettelerfassungTeamStatusEntryTools();

  const { loadErfassungTeamStatusListe } =
    useStimmzettelerfassungTeamStatusService();

  const teamstatusList = ref<StimmzettelerfassungTeamStatusEntry[]>([]);
  const isTeamStatusListLoading = ref(false);
  const lastTeamstatusLoadingTime = ref<Date>();

  async function loadTeamStatusListe() {
    try {
      isTeamStatusListLoading.value = true;
      const loaded = await loadErfassungTeamStatusListe(
        wahlID,
        wahlbezirkID,
        true
      );
      if (loaded) {
        loaded.sort(compareByTeamID);
        teamstatusList.value = loaded;
        lastTeamstatusLoadingTime.value = new Date();
      }
    } finally {
      isTeamStatusListLoading.value = false;
    }
  }

  return {
    teamstatusList,
    lastTeamstatusLoadingTime: readonly(lastTeamstatusLoadingTime),
    isTeamStatusListLoading: readonly(isTeamStatusListLoading),
    loadTeamStatusListe,
  };
}
