import type { StimmzettelOfTeamDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
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
    sendNotification = true,
    forceRefresh = false
  ): Promise<PersistedStimmzettel[]> {
    try {
      const response = forceRefresh
        ? await _getStimmzettelAndRefreshOfflineData(
            wahlID,
            wahlbezirkID,
            teamID
          )
        : await _getStimmzettelOfflineFirst(wahlID, wahlbezirkID, teamID);

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
    sendNotification = true,
    justStore = false
  ) {
    try {
      const dtosToSend = stimmzettelList.map((stimmzettel) =>
        toDTO(stimmzettel)
      );
      if (justStore) {
        await _saveStimmzettelListToStore(
          wahlID,
          wahlbezirkID,
          teamID,
          dtosToSend
        );
      } else {
        await _saveStimmzettelListToBackend(
          wahlID,
          wahlbezirkID,
          teamID,
          dtosToSend
        );
      }

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
    teamID: string,
    stimmzettel: PersistedStimmzettel,
    sendNotification = true
  ) {
    try {
      const dtoToSend = toSingleStimmzettelDTO(stimmzettel);
      await stimmzettelControllerApi.postSingleStimmzettel(
        wahlID,
        wahlbezirkID,
        teamID,
        stimmzettel.stimmzettelkennung,
        dtoToSend
      );
      if (sendNotification) {
        addNotification(
          "Speichern des Stimmzettels erfolgreich",
          UserNotificationCategoryEnum.SUCCESS
        );
      }
    } catch (error) {
      if (sendNotification) {
        addNotification(
          "Speichern des Stimmzettels ist fehlgeschlagen",
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

  async function _getStimmzettelAndRefreshOfflineData(
    wahlID: string,
    wahlbezirkID: string,
    teamID: string
  ) {
    return await stimmzettelControllerApi.getStimmzettel(
      wahlID,
      wahlbezirkID,
      teamID,
      axiosConfigWrapper().requestAsForceRefresh()
    );
  }

  async function _getStimmzettelOfflineFirst(
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

  async function _saveStimmzettelListToStore(
    wahlID: string,
    wahlbezirkID: string,
    teamID: string,
    stimmzettelList: StimmzettelOfTeamDTO[]
  ) {
    await stimmzettelControllerApi.postStimmzettel(
      wahlID,
      wahlbezirkID,
      teamID,
      stimmzettelList,
      axiosConfigWrapper().requestAsSaveOfflineOnly()
    );
  }

  async function _saveStimmzettelListToBackend(
    wahlID: string,
    wahlbezirkID: string,
    teamID: string,
    stimmzettelList: StimmzettelOfTeamDTO[]
  ) {
    await stimmzettelControllerApi.postStimmzettel(
      wahlID,
      wahlbezirkID,
      teamID,
      stimmzettelList,
      axiosConfigWrapper().requestAsOnlineFirst()
    );
  }

  return {
    getStimmzettel,
    saveStimmzettel,
    saveSingleStimmzettel,
    getAnzahlStimmzettel,
  };
}
