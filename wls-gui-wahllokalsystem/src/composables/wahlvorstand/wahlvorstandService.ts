import type { Wahlvorstand } from "@/types/wahlvorstand/Wahlvorstand";

import {
  Configuration,
  WahlvorstandControllerApi,
} from "@/api/wls-clients/generated-wahlvorstand-api";
import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { useWahlvorstandMapper } from "@/composables/wahlvorstand/wahlvorstandMapper";
import { useWahlvorstandComparators } from "@/composables/wahlvorstand/wahlvorstandUtils.ts";
import { WAHLVORSTAND_SERVICE_API_URL } from "@/constants";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { toModel, toDto } = useWahlvorstandMapper();

const userNotificationService = useUserNotificationService();
const { compareWahlvorstandsMitglieder } = useWahlvorstandComparators();
const { axiosConfigWrapper } = useCommonApiUtils();

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
        forceUpdate,
        axiosConfigWrapper().requestAsOnlineFirst()
      );
      if (sendNotification) {
        userNotificationService.addNotification(
          "Die Anwesenheit wurde aktualisiert.",
          UserNotificationCategoryEnum.SUCCESS
        );
      }

      const wahlvorstand = toModel(response.data);
      wahlvorstand?.wahlvorstandsmitglieder.sort(
        compareWahlvorstandsMitglieder
      );
      return wahlvorstand;
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
        wahlvorstandDto,
        axiosConfigWrapper().requestAsOnlineFirst()
      );
      userNotificationService.addNotification(
        "Die Anwesenheit wurde erfolgreich gespeichert.",
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
