import type { Begruendung } from "@/types/ergebnismeldung/common/Begruendung.ts";
import type { Ergebnisse } from "@/types/ergebnismeldung/common/Ergebnisse.ts";
import type { Stimmzettelumschlaege } from "@/types/ergebnismeldung/common/Stimmzettelumschlaege.ts";
import type { Wahl } from "@/types/wahl/Wahl.ts";

import {
  BegruendungControllerApi,
  Configuration,
  ErgebnismeldungControllerApi,
  ErgebnisseControllerApi,
  SendErgebnisseMeldungsartEnum,
  StimmzettelumschlaegeControllerApi,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useErgebnisMapper } from "@/composables/ergebnismeldung/common/ergebnisMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ERGEBNISMELDUNG_SERVICE_API_URL } from "@/constants.ts";
import { useStatusStore } from "@/stores/statusStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const {
  toErgebnisseModel,
  toErgebnisseDto,
  toGetErgebnisseStapelartEnum,
  toPostErgebnisseStapelartEnum,
  toStimmzettelumschlaegeDto,
  toStimmzettelumschlaegeModel,
  toBegruendungModel,
  toBegruendungDto,
} = useErgebnisMapper();
const { getNullOn204OrElseResponseData } = useCommonApiUtils();
const { addNotification } = useUserNotificationService();

export function useErgebnisService() {
  const ergebnisMeldungConfiguration = new Configuration({
    basePath: ERGEBNISMELDUNG_SERVICE_API_URL,
  });

  const ergebnisseControllerAPI = new ErgebnisseControllerApi(
    ergebnisMeldungConfiguration
  );
  const ergebnismeldungsControllerApi = new ErgebnismeldungControllerApi(
    ergebnisMeldungConfiguration
  );
  const stimmzettelumschlaegeControllerAPI =
    new StimmzettelumschlaegeControllerApi(ergebnisMeldungConfiguration);
  const begruendungControllerApi = new BegruendungControllerApi(
    ergebnisMeldungConfiguration
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
      return responseData ? toErgebnisseModel(responseData) : null;
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
        toErgebnisseDto(ergebnisse)
      );
      if (sendNotification) {
        addNotification(
          `Ergebnisse für Stapelart ${stapelArt} gespeichert.`,
          UserNotificationCategoryEnum.SUCCESS
        );
      }
      useStatusStore().setStepDone(wahlID, wahlbezirkID, stapelArt);
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
      await ergebnismeldungsControllerApi.sendErgebnisse(
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

  async function postStimmzettelumschlaege(
    wahl: Wahl,
    wahlbezirkID: string,
    stimmzettelumschlaege: Stimmzettelumschlaege,
    stimmzettelTermForWahl: string,
    sendNotification = true
  ): Promise<void> {
    try {
      await stimmzettelumschlaegeControllerAPI.postStimmzettelumschlaege(
        wahl.wahlID,
        wahlbezirkID,
        toStimmzettelumschlaegeDto(
          stimmzettelumschlaege,
          wahl.wahlID,
          wahlbezirkID
        )
      );
      if (sendNotification) {
        addNotification(
          `${stimmzettelTermForWahl} für ${wahl.name} erfolgreich gespeichert.`,
          UserNotificationCategoryEnum.SUCCESS
        );
      }
    } catch (error) {
      if (sendNotification) {
        addNotification(
          `Speichern der ${stimmzettelTermForWahl} für ${wahl.name} fehlgeschlagen.`,
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw error;
    }
  }

  async function getStimmzettelumschlaege(
    wahl: Wahl,
    wahlbezirkID: string,
    stimmzettelTermForWahl: string,
    sendNotification = true
  ) {
    try {
      const response =
        await stimmzettelumschlaegeControllerAPI.getStimmzettelumschlaege(
          wahl.wahlID,
          wahlbezirkID
        );
      if (sendNotification) {
        addNotification(
          `${stimmzettelTermForWahl} für ${wahl.name} erfolgreich geladen.`,
          UserNotificationCategoryEnum.SUCCESS
        );
      }
      const responseData = getNullOn204OrElseResponseData(response);
      return responseData ? toStimmzettelumschlaegeModel(responseData) : null;
    } catch (e) {
      if (sendNotification) {
        addNotification(
          `Laden der ${stimmzettelTermForWahl} für ${wahl.name} fehlgeschlagen.`,
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw e;
    }
  }

  async function getBegruendungStimmzettelumschlaege(
    wahl: Wahl,
    wahlbezirkID: string,
    stimmzettelTermForWahl: string,
    sendNotification = true
  ) {
    try {
      const response = await begruendungControllerApi.getBegruendung(
        wahlbezirkID,
        wahl.wahlID,
        StapelArtEnum.StimmzettelUmschlaege
      );
      if (sendNotification) {
        addNotification(
          `Begründung ${stimmzettelTermForWahl} für ${wahl.name} erfolgreich geladen.`,
          UserNotificationCategoryEnum.SUCCESS
        );
      }
      const responseData = getNullOn204OrElseResponseData(response);
      return responseData ? toBegruendungModel(responseData) : null;
    } catch (e) {
      if (sendNotification) {
        addNotification(
          `Laden der Begründung ${stimmzettelTermForWahl} für ${wahl.name} fehlgeschlagen.`,
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw e;
    }
  }

  async function postBegruendung(
    begruendung: Begruendung,
    wahlbezirkID: string,
    sendNotification = true
  ): Promise<void> {
    try {
      await begruendungControllerApi.postBegruendung(
        wahlbezirkID,
        begruendung.wahlID,
        toPostErgebnisseStapelartEnum(begruendung.stapelart),
        toBegruendungDto(begruendung, wahlbezirkID)
      );
      if (sendNotification) {
        addNotification(
          `Begründung erfolgreich gespeichert.`,
          UserNotificationCategoryEnum.SUCCESS
        );
      }
    } catch (error) {
      if (sendNotification) {
        addNotification(
          `Speichern der Begründung fehlgeschlagen.`,
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw error;
    }
  }

  return {
    getErgebnisse,
    postErgebnisse,
    postSchnellmeldung,
    postStimmzettelumschlaege,
    getStimmzettelumschlaege,
    getBegruendungStimmzettelumschlaege,
    postBegruendung,
  };
}
