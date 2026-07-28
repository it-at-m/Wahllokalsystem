import { useStimmzettelService } from "@/composables/dse/stimmzettelService.ts";

export function useStimmzettelUtils() {
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

  return {
    getNextStimmzettelNumber,
  };
}
