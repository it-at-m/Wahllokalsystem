import type { StimmzettelerfassungTeamStatus } from "@/types/dse/StimmzettelerfassungTeamStatus.ts";

import {
  Configuration,
  StimmzettelerfassungTeamStatusControllerApi,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useStimmzettelerfassungTeamStatusMapper } from "@/composables/dse/stimmzettelerfassungTeamStatusMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ERGEBNISMELDUNG_SERVICE_API_URL } from "@/constants.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

export function useStimmzettelerfassungStatusTeamService() {
  const { getNullOn204OrElseResponseData } = useCommonApiUtils();
  const { addNotification } = useUserNotificationService();
  const { dtoToModel, modelToDto } = useStimmzettelerfassungTeamStatusMapper();

  const stimmzettelerfassungTeamStatusControllerApi =
    new StimmzettelerfassungTeamStatusControllerApi(
      new Configuration({ basePath: ERGEBNISMELDUNG_SERVICE_API_URL })
    );

  async function loadErfassungTeamStatus(
    wahlID: string,
    wahlbezirkID: string,
    teamID: string,
    sendNotification = true
  ): Promise<StimmzettelerfassungTeamStatus | null> {
    if (!teamID) {
      addNotification(
        `Fehler beim Laden des Team-Status: Fehlender Parameter teamID`,
        UserNotificationCategoryEnum.ERROR
      );
      return null;
    } else if (!wahlID) {
      addNotification(
        `Fehler beim Laden des Team-Status: Fehlender Parameter wahlID`,
        UserNotificationCategoryEnum.ERROR
      );
      return null;
    } else if (!wahlbezirkID) {
      addNotification(
        `Fehler beim Laden des Team-Status: Fehlender Parameter wahlBezirkID`,
        UserNotificationCategoryEnum.ERROR
      );
      return null;
    }
    const { wahlenActions } = useWahlenStore();
    try {
      const response =
        await stimmzettelerfassungTeamStatusControllerApi.getStimmzettelerfassungTeamStatus(
          wahlID,
          wahlbezirkID,
          teamID
        );
      const responseData = getNullOn204OrElseResponseData(response);
      let result = null;
      if (responseData) {
        result = dtoToModel(responseData);
      }
      if (sendNotification) {
        const wahlname =
          wahlenActions.getWahlNameOrBlankStringById(wahlID) || "";
        addNotification(
          `Status '${result?.status}' für ${wahlname} erfolgreich geladen.`,
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

  async function postErfassungTeamStatus(
    wahlID: string,
    wahlbezirkID: string,
    teamID: string,
    status: StimmzettelerfassungTeamStatus,
    sendNotification = true
  ) {
    const { wahlenActions } = useWahlenStore();
    const wahlname = wahlenActions.getWahlNameOrBlankStringById(wahlID) || "";
    try {
      await stimmzettelerfassungTeamStatusControllerApi.saveStimmzettelerfassungTeamStatus(
        wahlID,
        wahlbezirkID,
        teamID,
        modelToDto(status)
      );
      if (sendNotification) {
        addNotification(
          `Team-Status für ${wahlname} erfolgreich gespeichert.`,
          UserNotificationCategoryEnum.SUCCESS
        );
      }
    } catch {
      if (sendNotification) {
        addNotification(
          `Fehler beim Speichern des Team-Status für ${wahlname}.`,
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw new Error(`Post Team-Status für ${wahlname} failed.`);
    }
  }

  return {
    loadErfassungTeamStatus,
    postErfassungTeamStatus,
  };
}
