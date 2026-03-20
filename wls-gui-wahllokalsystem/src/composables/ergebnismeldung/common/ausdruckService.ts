import type { MeldungsartEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";

import {
  AusdruckControllerApi,
  Configuration,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useAusdruckMapper } from "@/composables/ergebnismeldung/common/ausdruckMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ERGEBNISMELDUNG_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { addNotification } = useUserNotificationService();
const { meldungsartEnumToDto, toAusdruckWriteDTO } = useAusdruckMapper();

export function useAusdruckService() {
  const ausdruckControllerApi = new AusdruckControllerApi(
    new Configuration({
      basePath: ERGEBNISMELDUNG_SERVICE_API_URL,
    })
  );

  async function postAusdruck(
    wahlbezirkID: string,
    wahlID: string,
    meldungsart: MeldungsartEnum,
    ausdruck: string,
    sendNotification = true
  ) {
    try {
      await ausdruckControllerApi.postAusdruck(
        wahlID,
        wahlbezirkID,
        meldungsartEnumToDto(meldungsart),
        toAusdruckWriteDTO(ausdruck)
      );
      if (sendNotification) {
        addNotification(
          "Ausdruck erfolgreich gespeichert",
          UserNotificationCategoryEnum.SUCCESS
        );
      }
    } catch {
      if (sendNotification) {
        addNotification(
          `Fehler beim Senden des Ausdrucks der ${meldungsart}`,
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw new Error(`post ausdruck failed`);
    }
  }

  return { postAusdruck };
}
