import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";

import { ref } from "vue";

import { useStimmzettelService } from "@/composables/dse/stimmzettelerfassung/stimmzettelService.ts";
import { useStimmzettelerfassungTeamStatusListState } from "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusListState.ts";

export function useAllStimmzettelOfWahlbezirkState(
  wahlID: string,
  wahlbezirkID: string
) {
  const { teamstatusList, loadTeamStatusListe } =
    useStimmzettelerfassungTeamStatusListState(wahlID, wahlbezirkID);
  const { getStimmzettel } = useStimmzettelService();

  const stimmzettelOfWahlbezirk = ref<PersistedStimmzettel[]>([]);
  const isLoading = ref(false);

  async function loadStimmzettelOfWahlbezirk() {
    isLoading.value = true;
    try {
      stimmzettelOfWahlbezirk.value = [];
      await loadTeamStatusListe();
      const registeredTeams = teamstatusList.value.map((team) => team.teamID);
      const stimmzettelOfAllTeams: PersistedStimmzettel[] = [];

      for (const teamId of registeredTeams) {
        const stimmzettelOfTeam = await getStimmzettel(
          wahlID,
          wahlbezirkID,
          teamId
        );
        stimmzettelOfAllTeams.push(...stimmzettelOfTeam);
      }

      stimmzettelOfWahlbezirk.value = stimmzettelOfAllTeams;
    } finally {
      isLoading.value = false;
    }
  }

  return {
    isLoading,
    stimmzettelOfWahlbezirk,

    loadStimmzettelOfWahlbezirk,
  };
}
