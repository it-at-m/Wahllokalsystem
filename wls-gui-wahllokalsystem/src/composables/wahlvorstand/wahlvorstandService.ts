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
    {
      forceUpdate = false,
      sendNotification = true,
    }: {
      forceUpdate?: boolean;
      sendNotification?: boolean;
    } = {}
  ): Promise<Wahlvorstand> {
    try {
      const response = await wahlvorstandControllerApi.getWahlvorstand(
        wahlbezirkID,
        forceUpdate
      );
      if (sendNotification) {
        userNotificationService.addNotification(
          "Die Anwesenheit wurde aktualisiert.",
          UserNotificationCategoryEnum.SUCCESS
        );
      }
      return _sortWahlvorstand(toModel(response.data));
    } catch (error) {
      if (sendNotification) {
        userNotificationService.addNotification(
          "Das Aktualisieren der Anwesenheit schlug fehl.",
          UserNotificationCategoryEnum.ERROR
        );
      }
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

  function _sortWahlvorstand(wahlvorstand: Wahlvorstand) {
    const WahlvorstandFunktion = {
      W: 0,
      SWB: 1,
      SB: 2,
      SSB: 3,
      B: 4,
    };

    wahlvorstand?.wahlvorstandsmitglieder.sort((a, b) => {
      const functionComparison =
        WahlvorstandFunktion[a.funktion] - WahlvorstandFunktion[b.funktion];
      if (functionComparison !== 0) {
        return functionComparison;
      }
      const familiennameComparison = a.familienname.localeCompare(
        b.familienname
      );
      if (familiennameComparison !== 0) {
        return familiennameComparison;
      }

      return a.vorname.localeCompare(b.vorname);
    });

    return wahlvorstand;
  }

  return {
    getWahlvorstand,
    saveWahlvorstand,
  };
}
