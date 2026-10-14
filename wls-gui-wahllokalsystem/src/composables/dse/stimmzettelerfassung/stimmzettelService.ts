import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";

import {
  Configuration,
  StimmzettelControllerApi,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useStimmzettelMapper } from "@/composables/dse/stimmzettelerfassung/stimmzettelMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ERGEBNISMELDUNG_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

export function useStimmzettelService() {
  const { addNotification } = useUserNotificationService();
  const { axiosConfigWrapper, getNullOn204OrElseResponseData } =
    useCommonApiUtils();
  const { toModel, toDTO } = useStimmzettelMapper();

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
    sendNotification = true,
    forceRefresh = false
  ): Promise<PersistedStimmzettel[]> {
    try {
      const response = forceRefresh
        ? await _fetchStimmzettelOnlineAndStore(wahlID, wahlbezirkID, teamID)
        : await _fetchStimmzettelOfflineFirst(wahlID, wahlbezirkID, teamID);

      const responseData = getNullOn204OrElseResponseData(response);
      return (
        responseData?.map((stimmzettelDTO) =>
          toModel(stimmzettelDTO, teamID)
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
    stimmzettelList: PersistedStimmzettel[],
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
        dtosToSend
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

  async function _fetchStimmzettelOfflineFirst(
    wahlID: string,
    wahlbezirkID: string,
    teamID: string
  ) {
    return await stimmzettelControllerApi.getStimmzettel(
      wahlID,
      wahlbezirkID,
      teamID
    );
  }

  async function _fetchStimmzettelOnlineAndStore(
    wahlID: string,
    wahlbezirkID: string,
    teamID: string
  ) {
    const response = await stimmzettelControllerApi.getStimmzettel(
      wahlID,
      wahlbezirkID,
      teamID,
      axiosConfigWrapper().requestAsOnlineOnly()
    );
    await stimmzettelControllerApi.postStimmzettel(
      wahlID,
      wahlbezirkID,
      teamID,
      response.data,
      axiosConfigWrapper().requestAsSaveOfflineOnly()
    );
    return response;
  }

  return {
    getStimmzettel,
    saveStimmzettel,
    getAnzahlStimmzettel,
  };
}
