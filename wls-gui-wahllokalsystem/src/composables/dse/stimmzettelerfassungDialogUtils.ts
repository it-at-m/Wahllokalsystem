import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { computed } from "vue";

import { useWahlvorschlagMapper } from "@/composables/dse/wahlvorschlagMapper.ts";

const { toWahlvorschlagAnzeigen } = useWahlvorschlagMapper();

export function useStimmzettelerfassungDialogUtils(
  wahlvorschlaege: Wahlvorschlag[]
) {
  const wahlvorschlaegeToDisplay = computed(() =>
    wahlvorschlaege.map(toWahlvorschlagAnzeigen)
  );

  return {
    wahlvorschlaegeToDisplay,
  };
}
