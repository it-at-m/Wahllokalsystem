import type { StimmzettelerfassungTeamStatusEntry } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEntry.ts";

import { readonly, ref } from "vue";

import { useStimmzettelerfassungTeamStatusService } from "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusService.ts";

export function useStimmzettelerfassungTeamStatusState(
  wahlID: string,
  wahlbezirkID: string
) {
  const { loadErfassungTeamStatusListe } =
    useStimmzettelerfassungTeamStatusService();

  const teamstatusList = ref<StimmzettelerfassungTeamStatusEntry[]>([]);
  const isTeamStatusLoading = ref(false);
  const lastTeamstatusLoadingTime = ref<Date>();

  async function loadTeamStatusListe() {
    try {
      isTeamStatusLoading.value = true;
      const loaded = await loadErfassungTeamStatusListe(
        wahlID,
        wahlbezirkID,
        true
      );
      if (loaded) {
        teamstatusList.value = loaded;
        lastTeamstatusLoadingTime.value = new Date();
      }
    } finally {
      isTeamStatusLoading.value = false;
    }
  }

  return {
    teamstatusList,
    lastTeamstatusLoadingTime: readonly(lastTeamstatusLoadingTime),
    isTeamStatusLoading: readonly(isTeamStatusLoading),
    loadTeamStatusListe,
  };
}
