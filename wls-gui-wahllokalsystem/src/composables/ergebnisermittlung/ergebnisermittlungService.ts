import type { Begruendung } from "@/types/ergebnisermittlung/Begruendung.ts";
import type { Stimmzettelumschlaege } from "@/types/ergebnisermittlung/Stimmzettelumschlaege.ts";
import type { Wahl } from "@/types/wahl/Wahl.ts";

import {
  BegruendungControllerApi,
  Configuration,
  StimmzettelumschlaegeControllerApi,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useErgebnisermittlungMapper } from "@/composables/ergebnisermittlung/ergebnisermittlungMapper.ts";
import { useErgebnisMapper } from "@/composables/ergebnismeldung/common/ergebnisMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ERGEBNISMELDUNG_SERVICE_API_URL } from "@/constants.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const { toDto, toModel } = useErgebnisermittlungMapper();
const { toBegruendungModel, toBegruendungDto, toPostErgebnisseStapelartEnum } =
  useErgebnisMapper();
const { addNotification } = useUserNotificationService();
const { getNullOn204OrElseResponseData } = useCommonApiUtils();

export function useErgebnisermittlungService() {
  const ergebnisMeldungConfiguration = new Configuration({
    basePath: ERGEBNISMELDUNG_SERVICE_API_URL,
  });

  const stimmzettelumschlaegeControllerAPI =
    new StimmzettelumschlaegeControllerApi(ergebnisMeldungConfiguration);

  const begruendungControllerApi = new BegruendungControllerApi(
    ergebnisMeldungConfiguration
  );

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
        toDto(stimmzettelumschlaege, wahl.wahlID, wahlbezirkID)
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
      return responseData ? toModel(responseData) : null;
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
    postStimmzettelumschlaege,
    getStimmzettelumschlaege,
    getBegruendungStimmzettelumschlaege,
    postBegruendung,
  };
}
