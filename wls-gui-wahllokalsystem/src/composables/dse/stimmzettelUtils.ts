import type { Stimmzettel } from "@/types/dse/Stimmzettel.ts";

import { StimmzettelGueltigkeitEnum } from "@/types/dse/StimmzettelGueltigkeitEnum.ts";

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

  function getEmptyStimmzettelWithStimmzettelkennung(
    stimmzettelkennung: number
  ): Stimmzettel {
    return {
      stimmzettelkennung: stimmzettelkennung,
      gueltigkeit: StimmzettelGueltigkeitEnum.Valid,
      invalideVotes: 0,
      beschlussfassung: null,
      beschlussvorschlag: [],
      wahlvorschlaege: [],
    };
  }

  return {
    getNextStimmzettelNumber,
    getEmptyStimmzettelWithStimmzettelkennung,
  };
}
