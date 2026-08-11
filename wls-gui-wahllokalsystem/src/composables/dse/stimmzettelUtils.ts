import type { Stimmzettel } from "@/types/dse/Stimmzettel.ts";

export function useStimmzettelUtils() {
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
