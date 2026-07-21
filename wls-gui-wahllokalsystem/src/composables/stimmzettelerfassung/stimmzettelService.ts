import {
  Configuration,
  StimmzettelControllerApi,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { ERGEBNISMELDUNG_SERVICE_API_URL } from "@/constants.ts";

export function useStimmzettelService() {
  const ergebnismeldungConfiguration = new Configuration({
    basePath: ERGEBNISMELDUNG_SERVICE_API_URL,
  });

  const stimmzettelControllerApi = new StimmzettelControllerApi(
    ergebnismeldungConfiguration
  );

  async function loadAnzahlStimmzettel(
    wahlID: string,
    wahlbezirkID: string
  ): Promise<number> {
    const response = await stimmzettelControllerApi.getAnzahlStimmzettel(
      wahlID,
      wahlbezirkID
    );

    return response.data;
  }

  return {
    loadAnzahlStimmzettel,
  };
}
