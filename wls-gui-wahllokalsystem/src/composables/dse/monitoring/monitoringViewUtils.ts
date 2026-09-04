import { onActivated } from "vue";

import { useStimmzettelerfassungTeamStatusListState } from "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusListState.ts";
import { useStimmzettelerfassungStatusState } from "@/composables/dse/stimmzettelerfassungWorkflowStatus/stimmzettelerfassungStatusState.ts";

export function useMonitoringViewUtils(wahlID: string, wahlbezirkID: string) {
  const stimmzettelerfassungState = useStimmzettelerfassungStatusState(
    wahlID,
    wahlbezirkID
  );
  const stimmzettelerfassungTeamState =
    useStimmzettelerfassungTeamStatusListState(wahlID, wahlbezirkID);

  async function onMonitoringSynchronisierenClicked() {
    await stimmzettelerfassungTeamState.loadTeamStatusListe();
  }

  onActivated(async () => {
    await Promise.allSettled([
      stimmzettelerfassungTeamState.loadTeamStatusListe(),
      stimmzettelerfassungState.loadWorkflowStatus(),
    ]);
  });

  return {
    onMonitoringSynchronisierenClicked,

    ...stimmzettelerfassungTeamState,
    ...stimmzettelerfassungState,
  };
}
