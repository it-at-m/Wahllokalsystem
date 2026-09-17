import type { PersistedStimmzettel } from "@/types/dse/persistedStimmzettel/PersistedStimmzettel.ts";

export function useStimmzettelkennungDialogUtils() {
  function getNextStimmzettelNumber(stimmzettelList: PersistedStimmzettel[]) {
    return stimmzettelList.length > 0
      ? Math.max(
          ...stimmzettelList.map(
            (stimmzettel) => stimmzettel.stimmzettelkennung
          )
        ) + 1
      : 1;
  }

  return {
    getNextStimmzettelNumber,
  };
}
