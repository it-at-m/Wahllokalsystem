import type { Stimmzettel } from "@/types/dse/Stimmzettel.ts";

import { useStimmzettelService } from "@/composables/dse/stimmzettelService.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/StimmzettelGueltigkeitEnum.ts";

export function useStimmzettelkennungDialogUtils() {
  const { getStimmzettel } = useStimmzettelService();

  async function getNextStimmzettelNumber(
    wahlID: string,
    wahlbezirkID: string,
    teamID: string
  ) {
    const stimmzettelList = await getStimmzettel(wahlID, wahlbezirkID, teamID);
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
