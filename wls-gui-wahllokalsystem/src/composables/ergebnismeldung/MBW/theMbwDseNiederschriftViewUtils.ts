import { onActivated } from "vue";

import { useAllStimmzettelOfWahlbezirkState } from "@/composables/dse/allStimmzettelOfWahlbezirkState.ts";
import { useMbwStimmzettelFilterService } from "@/composables/dse/mbwStimmzettelFilterService.ts";
import { useWahlvorschlaegeState } from "@/composables/dse/stimmzettelerfassung/wahlvorschlaegeState.ts";
import { useMbwErgebnisseAndWahlvorschlagStapelSumReactiveMapper } from "@/composables/ergebnismeldung/MBW/mbwErgebnisseAndWahlvorschlagStapelSumReactiveMapper.ts";

export function useMbwNiederschriftViewUtils(
  wahlID: string,
  wahlbezirkID: string
) {
  const { stimmzettelOfWahlbezirk, loadStimmzettelOfWahlbezirk } =
    useAllStimmzettelOfWahlbezirkState(wahlID, wahlbezirkID);
  const { wahlvorschlaege } = useWahlvorschlaegeState(wahlID, wahlbezirkID);
  const {
    stapelA,
    stapelASumGroupedByWahlvorschlag,
    stapelB,
    stapelBSumGroupedByWahlvorschlag,
    stapelBC,
    stapelDUngueltig,
    stapelEUngueltig,
  } = useMbwStimmzettelFilterService(stimmzettelOfWahlbezirk);

  const { wahlvorschlaegeErgebnisseStapelAAndB } =
    useMbwErgebnisseAndWahlvorschlagStapelSumReactiveMapper(
      wahlvorschlaege,
      stapelASumGroupedByWahlvorschlag,
      stapelBSumGroupedByWahlvorschlag
    );

  onActivated(async () => {
    await loadStimmzettelOfWahlbezirk();
  });

  return {
    wahlvorschlaege,
    stimmzettelOfWahlbezirk,
    wahlvorschlaegeErgebnisseStapelAAndB,
    stapelA,
    stapelB,
    stapelBC,
    stapelDUngueltig,
    stapelEUngueltig,
  };
}
