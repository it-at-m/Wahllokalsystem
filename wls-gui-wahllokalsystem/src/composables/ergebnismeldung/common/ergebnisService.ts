import type { Ergebnisse } from "@/types/ergebnismeldung/common/Ergebnisse.ts";
import type { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";

import {
  Configuration,
  ErgebnismeldungControllerApi,
  ErgebnisseControllerApi,
  SendErgebnisseMeldungsartEnum,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useErgebnisMapper } from "@/composables/ergebnismeldung/common/ergebnisMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ERGEBNISMELDUNG_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const {
  toModel,
  toDto,
  toGetErgebnisseStapelartEnum,
  toPostErgebnisseStapelartEnum,
} = useErgebnisMapper();
const { getNullOn204OrElseResponseData } = useCommonApiUtils();
const { addNotification } = useUserNotificationService();

export function useErgebnisService() {
  const ergebnisseControllerAPI = new ErgebnisseControllerApi(
    new Configuration({ basePath: ERGEBNISMELDUNG_SERVICE_API_URL })
  );
  const ergebenismeldungsControllerApi = new ErgebnismeldungControllerApi(
    new Configuration({
      basePath: ERGEBNISMELDUNG_SERVICE_API_URL,
    })
  );

  async function getErgebnisse(
    wahlbezirkID: string,
    wahlID: string,
    stapelArt: StapelArtEnum,
    sendNotification = true
  ) {
    try {
      const response = await ergebnisseControllerAPI.getErgebnisse(
        wahlbezirkID,
        wahlID,
        toGetErgebnisseStapelartEnum(stapelArt)
      );

      if (sendNotification) {
        addNotification(
          `Ergebnisse für Stapelart ${stapelArt} erfolgreich geladen.`,
          UserNotificationCategoryEnum.SUCCESS
        );
      }

      const responseData = getNullOn204OrElseResponseData(response);
      return responseData ? toModel(responseData) : null;
    } catch {
      if (sendNotification) {
        addNotification(
          `Fehler beim Laden der Ergebnisse für Stapelart ${stapelArt}.`,
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw new Error(`Get Ergebnisse for Stapelart ${stapelArt} failed.`);
    }
  }

  async function postErgebnisse(
    wahlbezirkID: string,
    wahlID: string,
    stapelArt: StapelArtEnum,
    ergebnisse: Ergebnisse,
    sendNotification = true
  ) {
    try {
      await ergebnisseControllerAPI.postErgebnisse(
        wahlbezirkID,
        wahlID,
        toPostErgebnisseStapelartEnum(stapelArt),
        toDto(ergebnisse)
      );
      if (sendNotification) {
        addNotification(
          `Ergebnisse für Stapelart ${stapelArt} gespeichert.`,
          UserNotificationCategoryEnum.SUCCESS
        );
      }
    } catch {
      if (sendNotification) {
        addNotification(
          `Fehler beim Speichern der Ergebnisse für Stapelart ${stapelArt}.`,
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw new Error(`Post Ergebnisse for Stapelart ${stapelArt} failed.`);
    }
  }

  async function postSchnellmeldung(
    wahlID: string,
    wahlbezirkID: string,
    hauptwahlbezirkID: string,
    waehlerverzeichnisNummer: number,
    sendNotification = true
  ) {
    try {
      await ergebenismeldungsControllerApi.sendErgebnisse(
        wahlID,
        wahlbezirkID,
        waehlerverzeichnisNummer,
        SendErgebnisseMeldungsartEnum.V3,
        hauptwahlbezirkID
      );

      if (sendNotification) {
        addNotification(
          "Ergebnismeldung erfolgreich versendet",
          UserNotificationCategoryEnum.SUCCESS
        );
      }
    } catch (error) {
      if (sendNotification) {
        addNotification(
          "Ergebnismeldung konnte nicht versendet werden",
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw error;
    }
  }

  return { getErgebnisse, postErgebnisse, postSchnellmeldung };
}
