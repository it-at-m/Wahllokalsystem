import type { Stimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";

import {
  Configuration,
  StimmzettelControllerApi,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useStimmzettelMapper } from "@/composables/dse/stimmzettelMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ERGEBNISMELDUNG_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

export function useStimmzettelService() {
  const { addNotification } = useUserNotificationService();
  const { axiosConfigWrapper, getNullOn204OrElseResponseData } =
    useCommonApiUtils();
  const { toModel, toDTO, toSingleStimmzettelDTO } = useStimmzettelMapper();

  const ergebnismeldungConfiguration = new Configuration({
    basePath: ERGEBNISMELDUNG_SERVICE_API_URL,
  });

  const stimmzettelControllerApi = new StimmzettelControllerApi(
    ergebnismeldungConfiguration
  );

  async function getStimmzettel(
    wahlID: string,
    wahlbezirkID: string,
    teamID: string,
    sendNotification = true
  ): Promise<Stimmzettel[]> {
    try {
      const response = await stimmzettelControllerApi.getStimmzettel(
        wahlID,
        wahlbezirkID,
        teamID,
        axiosConfigWrapper().requestAsOnlineOnly()
      );
      const responseData = getNullOn204OrElseResponseData(response);
      return (
        responseData?.map((stimmzettelDTO) =>
          toModel(teamID, stimmzettelDTO)
        ) ?? []
      );
    } catch (error) {
      if (sendNotification) {
        addNotification(
          "Abrufen der Stimmzettel ist fehlgeschlagen",
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw error;
    }
  }

  async function saveStimmzettel(
    wahlID: string,
    wahlbezirkID: string,
    teamID: string,
    stimmzettelList: Stimmzettel[],
    sendNotification = true
  ) {
    try {
      const dtosToSend = stimmzettelList.map((stimmzettel) =>
        toDTO(stimmzettel)
      );
      await stimmzettelControllerApi.postStimmzettel(
        wahlID,
        wahlbezirkID,
        teamID,
        dtosToSend,

        axiosConfigWrapper().requestAsOnlineOnly()
      );
      if (sendNotification) {
        addNotification(
          "Speichern der Stimmzettel erfolgreich",
          UserNotificationCategoryEnum.SUCCESS
        );
      }
    } catch (error) {
      if (sendNotification) {
        addNotification(
          "Speichern der Stimmzettel ist fehlgeschlagen",
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw error;
    }
  }

  async function saveSingleStimmzettel(
    wahlID: string,
    wahlbezirkID: string,
    stimmzettel: Stimmzettel
  ) {
    const dto = toSingleStimmzettelDTO(stimmzettel);
    await stimmzettelControllerApi.postSingleStimmzettel(
      wahlID,
      wahlbezirkID,
      stimmzettel.teamID,
      stimmzettel.stimmzettelkennung,
      dto,
      axiosConfigWrapper().requestAsOnlineOnly()
    );
  }

  async function getAnzahlStimmzettel(
    wahlID: string,
    wahlbezirkID: string,
    sendNotification = true
  ): Promise<number> {
    try {
      const result = await stimmzettelControllerApi.getAnzahlStimmzettel(
        wahlID,
        wahlbezirkID,
        axiosConfigWrapper().requestAsOnlineOnly()
      );
      return result.data;
    } catch (error) {
      if (sendNotification) {
        addNotification(
          "Abrufen der Anzahl der Stimmzettel ist fehlgeschlagen",
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw error;
    }
  }

  return {
    getStimmzettel,
    saveStimmzettel,
    saveSingleStimmzettel,
    getAnzahlStimmzettel,
  };
}
