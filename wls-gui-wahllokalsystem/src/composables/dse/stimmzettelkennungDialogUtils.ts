import type { Stimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";

export function useStimmzettelkennungDialogUtils() {
  function getNextStimmzettelNumber(stimmzettelList: Stimmzettel[]) {
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
