import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";



import { computed } from "vue";



import { useStimmzettelManager } from "@/composables/dse/stimmzettelManager.ts";
import { useWahlvorschlagMapper } from "@/composables/dse/wahlvorschlagMapper.ts";


const { toWahlvorschlagAnzeigen } = useWahlvorschlagMapper();

export function useStimmzettelerfassungDialogUtils(
  wahlvorschlaege: Wahlvorschlag[]
) {
  const stimmzettelManager = useStimmzettelManager(wahlvorschlaege);

  const wahlvorschlaegeToDisplay = computed(() =>
    wahlvorschlaege.map(toWahlvorschlagAnzeigen)
  );

  return {
    wahlvorschlaegeToDisplay,
    stimmzettelManager,
  };
}
