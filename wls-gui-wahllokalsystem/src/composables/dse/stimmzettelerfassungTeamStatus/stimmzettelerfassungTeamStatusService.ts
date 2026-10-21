import type { StimmzettelerfassungTeamStatusDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { StimmzettelerfassungTeamStatus } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatus.ts";
import type { StimmzettelerfassungTeamStatusEntry } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEntry.ts";

import { ref } from "vue";

import {
  Configuration,
  StimmzettelerfassungTeamStatusControllerApi,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useStimmzettelerfassungTeamStatusMapper } from "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ERGEBNISMELDUNG_SERVICE_API_URL } from "@/constants.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

export function useStimmzettelerfassungTeamStatusService() {
  const { axiosConfigWrapper, getNullOn204OrElseResponseData } =
    useCommonApiUtils();
  const { addNotification } = useUserNotificationService();
  const { dtoEntryToModelEntry, dtoToModel, modelToDto } =
    useStimmzettelerfassungTeamStatusMapper();

  const stimmzettelerfassungTeamStatusControllerApi =
    new StimmzettelerfassungTeamStatusControllerApi(
      new Configuration({ basePath: ERGEBNISMELDUNG_SERVICE_API_URL })
    );

  const isSaving = ref(false);

  async function loadErfassungTeamStatus(
    wahlID: string,
    wahlbezirkID: string,
    teamID: string,
    sendNotification = true,
    forceRefresh = false
  ): Promise<StimmzettelerfassungTeamStatus | null> {
    const { wahlenActions } = useWahlenStore();
    try {
      const response = forceRefresh
        ? await _loadErfassungTeamStatusAndRefreshOfflineData(
            wahlID,
            wahlbezirkID,
            teamID
          )
        : await _loadErfassungTeamStatusOfflineFirst(
            wahlID,
            wahlbezirkID,
            teamID
          );

      const responseData = getNullOn204OrElseResponseData(response);

      const result = responseData ? dtoToModel(responseData) : null;
      if (sendNotification) {
        const wahlname =
          wahlenActions.getWahlNameOrBlankStringById(wahlID) || "";
        addNotification(
          `Team-Status '${result?.status}' für ${wahlname} erfolgreich geladen.`,
          UserNotificationCategoryEnum.SUCCESS
        );
      }
      return result;
    } catch {
      const wahlname = wahlenActions.getWahlNameOrBlankStringById(wahlID) || "";
      if (sendNotification) {
        addNotification(
          `Fehler beim Laden des Team-Status für ${wahlname}.`,
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw new Error(`Get Team-Status für ${wahlname} failed.`);
    }
  }

  async function loadErfassungTeamStatusListe(
    wahlID: string,
    wahlbezirkID: string,
    sendNotification = true,
    forceRefresh = false
  ): Promise<StimmzettelerfassungTeamStatusEntry[]> {
    const { wahlenActions } = useWahlenStore();
    try {
      const response = forceRefresh
        ? await _getStimmzettelerfassungTeamStatusListAndRefreshOfflineData(
            wahlID,
            wahlbezirkID
          )
        : await _getStimmzettelerfassungTeamStatusListOfflineFirst(
            wahlID,
            wahlbezirkID
          );
      const responseData = getNullOn204OrElseResponseData(response);

      return (
        responseData?.map((teamStatusDTO) =>
          dtoEntryToModelEntry(teamStatusDTO)
        ) ?? []
      );
    } catch {
      const wahlname = wahlenActions.getWahlNameOrBlankStringById(wahlID) || "";
      if (sendNotification) {
        addNotification(
          `Aktualisierung der Team-Status ist fehlgeschlagen.`,
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw new Error(`Get Team-Status für ${wahlname} failed.`);
    }
  }

  async function postErfassungTeamStatus(
    wahlID: string,
    wahlbezirkID: string,
    teamID: string,
    status: StimmzettelerfassungTeamStatus,
    sendNotification = true,
    forceOnline = false
  ) {
    const { wahlenActions } = useWahlenStore();
    const wahlname = wahlenActions.getWahlNameOrBlankStringById(wahlID) || "";
    try {
      isSaving.value = true;
      const dto = modelToDto(status);
      const savedStatus = forceOnline
        ? await _postErfassungTeamStatusOnlineOnlyWithStoreOnSuccess(
            wahlID,
            wahlbezirkID,
            teamID,
            dto
          )
        : await _postErfassungTeamStatusOnlineFirst(
            wahlID,
            wahlbezirkID,
            teamID,
            dto
          );

      return dtoToModel(savedStatus.data);
    } catch {
      if (sendNotification) {
        addNotification(
          `Fehler beim Speichern des Team-Status für ${wahlname}.`,
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw new Error(`Post Team-Status für ${wahlname} failed.`);
    } finally {
      isSaving.value = false;
    }
  }

  async function reopenStimmzettelerfassung(
    wahlID: string,
    wahlbezirkID: string,
    teamID: string,
    sendNotification = true
  ) {
    const { wahlenActions } = useWahlenStore();
    const wahlname = wahlenActions.getWahlNameOrBlankStringById(wahlID) || "";
    try {
      isSaving.value = true;
      await stimmzettelerfassungTeamStatusControllerApi.reopenStimmzettelerfassung(
        wahlID,
        wahlbezirkID,
        teamID,
        axiosConfigWrapper().requestAsOnlineOnly()
      );
      if (sendNotification) {
        addNotification(
          `Rückkehr zur Bearbeitung für ${wahlname} erfolgreich gespeichert.`,
          UserNotificationCategoryEnum.SUCCESS
        );
      }
    } catch {
      if (sendNotification) {
        addNotification(
          `Fehler beim Speichern der Rückkehr zur Bearbeitung für ${wahlname}.`,
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw new Error(
        `Post reopenStimmzettelerfassung für ${wahlname} failed.`
      );
    } finally {
      isSaving.value = false;
    }
  }

  async function _getStimmzettelerfassungTeamStatusListOfflineFirst(
    wahlID: string,
    wahlbezirkID: string
  ) {
    return await stimmzettelerfassungTeamStatusControllerApi.getStimmzettelerfassungTeamStatusList(
      wahlID,
      wahlbezirkID
    );
  }
  async function _getStimmzettelerfassungTeamStatusListAndRefreshOfflineData(
    wahlID: string,
    wahlbezirkID: string
  ) {
    return await stimmzettelerfassungTeamStatusControllerApi.getStimmzettelerfassungTeamStatusList(
      wahlID,
      wahlbezirkID,
      axiosConfigWrapper().requestAsForceRefresh()
    );
  }

  async function _loadErfassungTeamStatusOfflineFirst(
    wahlID: string,
    wahlbezirkID: string,
    teamID: string
  ) {
    return await stimmzettelerfassungTeamStatusControllerApi.getStimmzettelerfassungTeamStatus(
      wahlID,
      wahlbezirkID,
      teamID
    );
  }

  async function _loadErfassungTeamStatusAndRefreshOfflineData(
    wahlID: string,
    wahlbezirkID: string,
    teamID: string
  ) {
    return await stimmzettelerfassungTeamStatusControllerApi.getStimmzettelerfassungTeamStatus(
      wahlID,
      wahlbezirkID,
      teamID,
      axiosConfigWrapper().requestAsForceRefresh()
    );
  }

  async function _postErfassungTeamStatusOnlineOnlyWithStoreOnSuccess(
    wahlID: string,
    wahlbezirkID: string,
    teamID: string,
    dto: StimmzettelerfassungTeamStatusDTO
  ) {
    return await stimmzettelerfassungTeamStatusControllerApi.saveStimmzettelerfassungTeamStatus(
      wahlID,
      wahlbezirkID,
      teamID,
      dto,
      axiosConfigWrapper().requestAsPostOnlineOnlyAndStoreOnSuccess()
    );
  }

  async function _postErfassungTeamStatusOnlineFirst(
    wahlID: string,
    wahlbezirkID: string,
    teamID: string,
    dto: StimmzettelerfassungTeamStatusDTO
  ) {
    return await stimmzettelerfassungTeamStatusControllerApi.saveStimmzettelerfassungTeamStatus(
      wahlID,
      wahlbezirkID,
      teamID,
      dto
    );
  }

  return {
    isSaving,
    loadErfassungTeamStatus,
    loadErfassungTeamStatusListe,
    postErfassungTeamStatus,
    reopenStimmzettelerfassung,
  };
}
