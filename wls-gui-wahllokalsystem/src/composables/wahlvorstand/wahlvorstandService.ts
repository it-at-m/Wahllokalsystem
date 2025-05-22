import type { Wahlvorstand } from "@/types/wahlvorstand/Wahlvorstand";

import {
  Configuration,
  WahlvorstandControllerApi,
} from "@/api/wls-clients/generated-wahlvorstand-api";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { useWahlvorstandMapper } from "@/composables/wahlvorstand/wahlvorstandMapper";
import { WAHLVORSTAND_SERVICE_API_URL } from "@/constants";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { toModel, toDto } = useWahlvorstandMapper();

const userNotificationService = useUserNotificationService();

export function useWahlvorstandService() {
  const wahlvorstandControllerApi = new WahlvorstandControllerApi(
    new Configuration({
      basePath: WAHLVORSTAND_SERVICE_API_URL,
    })
  );

  async function getWahlvorstand(
    wahlbezirkID: string,
    forceUpdate = false
  ): Promise<Wahlvorstand> {
    try {
      const response = await wahlvorstandControllerApi.getWahlvorstand(
        wahlbezirkID,
        forceUpdate
      );
      userNotificationService.addNotification(
        "Die Anwesenheit wurde aktualisiert.",
        UserNotificationCategoryEnum.SUCCESS
      );
      return toModel(response.data);
    } catch (error) {
      userNotificationService.addNotification(
        "Das Aktualisieren der Anwesenheit schlug fehl.",
        UserNotificationCategoryEnum.ERROR
      );
      throw error;
    }
  }

  async function saveWahlvorstand(
    wahlbezirkID: string,
    wahlvorstand: Wahlvorstand
  ): Promise<{
    updateDatetime: Date;
  }> {
    const now = new Date();
    const wahlvorstandDto = toDto(wahlvorstand, now);

    try {
      await wahlvorstandControllerApi.postWahlvorstand(
        wahlbezirkID,
        wahlvorstandDto
      );
      userNotificationService.addNotification(
        "Der Anwesenheit wurde erfolgreich gespeichert.",
        UserNotificationCategoryEnum.SUCCESS
      );
    } catch (error) {
      userNotificationService.addNotification(
        "Das Speichern der Anwesenheit schlug fehl.",
        UserNotificationCategoryEnum.ERROR
      );
      throw error;
    }

    return {
      updateDatetime: now,
    };
  }

  return {
    getWahlvorstand,
    saveWahlvorstand,
  };
}
