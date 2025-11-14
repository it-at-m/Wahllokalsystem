import type { Wahl } from "@/types/wahl/Wahl.ts";
import type { Ref } from "vue";

import { computed } from "vue";

export function useWaehlerverzeichnisGetter(
  wahlenState: Ref<{ wahlen: Wahl[] | null }>
) {
  const waehlerverzeichnisNummern = computed(() => {
    if (!wahlenState.value.wahlen) return [];

    const nummern = new Set<number>();

    for (const wahl of wahlenState.value.wahlen) {
      nummern.add(wahl.waehlerverzeichnisNummer);
    }
    return Array.from(nummern);
  });

  return { waehlerverzeichnisNummern };
}
