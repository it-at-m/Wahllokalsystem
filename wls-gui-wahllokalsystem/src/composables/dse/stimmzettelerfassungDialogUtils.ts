import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { useStimmzettelManager } from "@/composables/dse/stimmzettelManager.ts";

export function useStimmzettelerfassungDialogUtils(
  wahlvorschlaege: Wahlvorschlag[],
  wahlID: string
) {
  const stimmzettelManager = useStimmzettelManager(wahlvorschlaege, wahlID);

  return {
    stimmzettelManager,
  };
}
