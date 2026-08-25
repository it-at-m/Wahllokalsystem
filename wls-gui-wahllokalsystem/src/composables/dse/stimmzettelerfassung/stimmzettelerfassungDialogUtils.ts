import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { useStimmzettelManager } from "@/composables/dse/stimmzettelerfassung/stimmzettelManager.ts";

export function useStimmzettelerfassungDialogUtils(
  wahlvorschlaege: Wahlvorschlag[]
) {
  const stimmzettelManager = useStimmzettelManager(wahlvorschlaege);

  return {
    stimmzettelManager,
  };
}
