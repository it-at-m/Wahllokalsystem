import type { StimmzettelerfassungTeamStatus } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatus.ts";
import type { StimmzettelerfassungTeamStatusEntry } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEntry.ts";

import { readonly, ref } from "vue";

import { useStimmzettelerfassungTeamStatusDataService } from "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelErfassungTeamStatusDataService.ts";

export function useStimmzettelerfassungTeamStatusListState(
  wahlID: string,
  wahlbezirkID: string
) {
  const { refreshTeamStatusOfAllTeams, getTeamStatusOfAllTeams } =
    useStimmzettelerfassungTeamStatusDataService(wahlID, wahlbezirkID);

  const teamstatusList = ref<StimmzettelerfassungTeamStatusEntry[]>([]);
  const isTeamStatusListLoading = ref(false);
  const lastTeamstatusLoadingTime = ref<Date>();

  async function loadTeamStatusListe() {
    try {
      isTeamStatusListLoading.value = true;
      await refreshTeamStatusOfAllTeams();

      const statusOfAllTeams = await getTeamStatusOfAllTeams();
      const loaded: StimmzettelerfassungTeamStatusEntry[] = [];
      statusOfAllTeams.forEach(
        (status: StimmzettelerfassungTeamStatus, teamID: string) => {
          loaded.push({
            teamID,
            status: status.status,
          });
        }
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
