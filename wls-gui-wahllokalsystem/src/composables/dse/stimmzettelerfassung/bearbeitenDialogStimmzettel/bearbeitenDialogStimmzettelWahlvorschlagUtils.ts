import type { DseStimmzettel } from "@/types/dse/stimmzettelerfassung/DseStimmzettel.ts";
import type { Ref } from "vue";

export function useBearbeitenDialogStimmzettelWahlvorschlagUtils(
  stimmzettel: Ref<DseStimmzettel>
) {
  function getWahlvorschlagByOrdnungszahl(ordnungszahl: number) {
    return stimmzettel.value.wahlvorschlaege.find(
      (wahlvorschlag) => wahlvorschlag.ordnungszahl === ordnungszahl
    );
  }

  return {
    getWahlvorschlagByOrdnungszahl,
  };
}
