import type { StimmzettelerfassungTeamStatusEntry } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEntry.ts";

import { readonly, ref } from "vue";

import { useStimmzettelerfassungTeamStatusService } from "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusService.ts";

export function useStimmzettelerfassungTeamStatusListState(
  wahlID: string,
  wahlbezirkID: string
) {
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
