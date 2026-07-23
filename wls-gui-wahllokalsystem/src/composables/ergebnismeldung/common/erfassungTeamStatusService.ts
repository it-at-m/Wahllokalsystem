import {
  Configuration,
  StimmzettelerfassungTeamStatusControllerApi
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import {useCommonApiUtils} from "@/composables/api/commonApiUtils.ts";
import {useErfassungTeamStatusMapper} from "@/composables/ergebnismeldung/common/erfassungTeamStatusMapper.ts";
import {useUserNotificationService} from "@/composables/userNotification/userNotificationService.ts";
import {ERGEBNISMELDUNG_SERVICE_API_URL} from "@/constants.ts";
import {useWahlenStore} from "@/stores/wahlenStore.ts";
import {UserNotificationCategoryEnum} from "@/types/userNotification/UserNotificationCategoryEnum.ts";
import type {ErfassungTeamStatus} from "@/types/dse/ErfassungTeamStatus.ts";
import type {ErfassungTeamStatusEnum} from "@/types/dse/ErfassungTeamStatusEnum.ts";

export function useStimmzettelerfassungStatusTeamService() {
  const { getNullOn204OrElseResponseData } = useCommonApiUtils();
  const { addNotification } = useUserNotificationService();
  const { toErfassungTeamStatus } = useErfassungTeamStatusMapper();

  const stimmzettelerfassungTeamStatusControllerApi = new StimmzettelerfassungTeamStatusControllerApi(
    new Configuration({ basePath: ERGEBNISMELDUNG_SERVICE_API_URL })
  );

  async function loadErfassungTeamStatus(
    teamID: string,
    wahlID: string,
    wahlbezirkID: string,
    sendNotification = true
  ) {
    const { wahlenActions } = useWahlenStore();
    try {
      const response = await stimmzettelerfassungTeamStatusControllerApi.getStimmzettelerfassungTeamStatus(
        wahlID,
        wahlbezirkID,
        teamID
      );

      if (sendNotification) {
        const wahlname =
          wahlenActions.getWahlNameOrBlankStringById(wahlID) || "";
        addNotification(
          `Status für ${wahlname} erfolgreich geladen.`,
          UserNotificationCategoryEnum.SUCCESS
        );
      }
      const responseData = getNullOn204OrElseResponseData(response);

      return toErfassungTeamStatus(responseData);
    } catch {
      const wahlname = wahlenActions.getWahlNameOrBlankStringById(wahlID) || "";
      if (sendNotification) {
        addNotification(
          `Fehler beim Laden des Status für ${wahlname}.`,
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw new Error(`Get Status für ${wahlname} failed.`);
    }
  }

  async function postErfassungTeamStatus(
    wahlID: string,
    wahlbezirkID: string,
    teamID: string,
    status: ErfassungTeamStatus,
    sendNotification = true
  ) {
    const { wahlenActions } = useWahlenStore();
    const wahlname = wahlenActions.getWahlNameOrBlankStringById(wahlID) || "";
    try {
      // API expects the raw status string, extract it from the interface
      await stimmzettelerfassungTeamStatusControllerApi.saveStimmzettelerfassungTeamStatus(wahlID, wahlbezirkID, teamID, status.status);
      if (sendNotification) {
        addNotification(
          `ErfassungTeamStatus für ${wahlname} erfolgreich gespeichert.`,
          UserNotificationCategoryEnum.SUCCESS
        );
      }
    } catch {
      if (sendNotification) {
        addNotification(
          `Fehler beim Speichern des ErfassungTeamStatus für ${wahlname}.`,
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw new Error(`Post ErfassungTeamStatus für ${wahlname} failed.`);
    }
  }

  return {
    loadErfassungTeamStatus,
    postErfassungTeamStatus,
  };
}
