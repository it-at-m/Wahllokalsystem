import type { Begruendung } from "@/types/ergebnismeldung/common/Begruendung.ts";
import type { Ergebnisse } from "@/types/ergebnismeldung/common/Ergebnisse.ts";
import type { Stimmzettelumschlaege } from "@/types/ergebnismeldung/common/Stimmzettelumschlaege.ts";
import type { Wahl } from "@/types/wahl/Wahl.ts";

import { storeToRefs } from "pinia";

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
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";
import { MbwRoutesEnum } from "@/types/navigation/MbwRoutesEnum.ts";
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
const { axiosConfigWrapper, getNullOn204OrElseResponseData } =
  useCommonApiUtils();
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
      if (
        responseData !== undefined &&
        responseData !== null &&
        !responseData.ergebnisse.find((ergebnis) => ergebnis.ergebnis === null)
      ) {
        const { setStepDone } = useWorkflowStore();
        const { isMbwStapelAErfasst, isMbwStapelBErfasst } =
          storeToRefs(useWorkflowStore());
        switch (stapelArt) {
          case StapelArtEnum.MbwDUngueltig:
            setStepDone(
              wahlID,
              wahlbezirkID,
              MbwRoutesEnum.MBW_STAPEL_D_UNGUELTIG
            );
            break;
          case StapelArtEnum.MbwA:
            if (isMbwStapelBErfasst.value) {
              setStepDone(
                wahlID,
                wahlbezirkID,
                MbwRoutesEnum.MBW_STAPEL_A_AND_B
              );
            } else {
              useWorkflowStore().isMbwStapelAErfasst = true;
            }
            break;
          case StapelArtEnum.MbwB:
            if (isMbwStapelAErfasst.value) {
              setStepDone(
                wahlID,
                wahlbezirkID,
                MbwRoutesEnum.MBW_STAPEL_A_AND_B
              );
            } else {
              useWorkflowStore().isMbwStapelBErfasst = true;
            }
            break;
          //TODO Überprüfung für Stapel_BC wird mit #2471 umgesetzt
        }
      }
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
    await ergebnismeldungsControllerApi.sendErgebnisse(
      wahlID,
      wahlbezirkID,
      waehlerverzeichnisNummer,
      SendErgebnisseMeldungsartEnum.V3,
      hauptwahlbezirkID,
      undefined,
      axiosConfigWrapper().requestAsOnlineOnly()
    );

    if (sendNotification) {
      addNotification(
        "Ergebnismeldung erfolgreich versendet",
        UserNotificationCategoryEnum.SUCCESS
      );
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
      if (
        responseData?.anzahlWaehler !== null &&
        responseData?.anzahlWaehler !== undefined
      ) {
        const { setStepDone } = useWorkflowStore();
        setStepDone(
          wahl.wahlID,
          wahlbezirkID,
          MbwRoutesEnum.MBW_AUSZAEHLUNG_STIMMZETTEL
        );
      }
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

  async function postNiederschrift(
    wahlID: string,
    wahlbezirkID: string,
    waehlerverzeichnisNummer: number,
    hauptwahlbezirkID: string,
    sendNotification = true
  ) {
    try {
      await ergebnismeldungsControllerApi.sendErgebnisse(
        wahlID,
        wahlbezirkID,
        waehlerverzeichnisNummer,
        SendErgebnisseMeldungsartEnum.V1,
        hauptwahlbezirkID,
        undefined,
        axiosConfigWrapper().requestAsPostOnlineOnlyButDirtyOnFail()
      );
      if (sendNotification) {
        addNotification(
          `Niederschrift erfolgreich versendet.`,
          UserNotificationCategoryEnum.SUCCESS
        );
      }
    } catch (error) {
      if (sendNotification) {
        addNotification(
          `Senden der Niederschrift fehlgeschlagen.`,
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
    postNiederschrift,
  };
}
