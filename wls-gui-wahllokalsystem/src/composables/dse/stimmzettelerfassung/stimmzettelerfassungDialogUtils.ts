import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";
import type { ComputedRef } from "vue";

import { useStimmzettelManager } from "@/composables/dse/stimmzettelerfassung/stimmzettelManager.ts";

export function useStimmzettelerfassungDialogUtils(
  stimmzettelkennung: ComputedRef<number>,
  wahlvorschlaege: Wahlvorschlag[],
  wahlID: string
) {
  const stimmzettelManager = useStimmzettelManager(
    stimmzettelkennung,
    wahlvorschlaege,
    wahlID
  );

  return {
    stimmzettelManager,
  };
}
