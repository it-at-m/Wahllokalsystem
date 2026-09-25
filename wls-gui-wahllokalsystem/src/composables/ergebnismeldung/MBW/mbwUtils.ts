import type { MeldungsartEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnismeldung/MBW/MbwErgebnisseAndWahlvorschlag.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { storeToRefs } from "pinia";
import { ref } from "vue";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useLogging } from "@/composables/common/logging.ts";
import { useAusdruckService } from "@/composables/ergebnismeldung/common/ausdruckService.ts";
import { useAWerteService } from "@/composables/ergebnismeldung/common/aWerteService.ts";
import { useErgebnisService } from "@/composables/ergebnismeldung/common/ergebnisService.ts";
import { useStatusService } from "@/composables/ergebnismeldung/common/statusService.ts";
import { useStatusUtils } from "@/composables/ergebnismeldung/common/statusUtils.ts";
import { useMbwErgebnisAndWahlvorschlagMapper } from "@/composables/ergebnismeldung/MBW/mbwErgebnisAndWahlvorschlagMapper.ts";
import { useWahlvorschlaegeService } from "@/composables/wahlvorschlaege/wahlvorschlaegeService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { MeldungValidierungsstatusEnum } from "@/types/ergebnismeldung/common/MeldungValidierungsstatusEnum.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";

const { postErgebnisse, getErgebnisse, postSchnellmeldung, postNiederschrift } =
  useErgebnisService();
const { getWahlvorschlaege } = useWahlvorschlaegeService();
const { getAWerteForWahlbezirkAndWahl } = useAWerteService();
const { logError } = useLogging("mbwUtils");
const { postStatus } = useStatusService();
const { loadStatusByWahlIdAndWahlbezirkId } = useStatusUtils();
const { toYyyyMmDdWithTimeWithoutTimezoneOffset } = useDateTimeFormatter();

export function useMbwUtils(wahlID: string, wahlbezirkID: string) {
  const { mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse } =
    useMbwErgebnisAndWahlvorschlagMapper(wahlID, wahlbezirkID);

  const { wahlenActions } = useWahlenStore();
  const { currentUserWahlbezirkID } = storeToRefs(useUserStore());

  const { postAusdruck } = useAusdruckService();

  const isErgebnisseSaving = ref<boolean>(false);
  const isSendingSchnellmeldung = ref<boolean>(false);
  const isSendingNiederschrift = ref<boolean>(false);

  async function saveGueltigeErgebnisse(
    ergebnisse: MbwErgebnisseAndWahlvorschlag[]
  ) {
    isErgebnisseSaving.value = true;

    try {
      await postErgebnisse(
        wahlbezirkID,
        wahlID,
        StapelArtEnum.MbwA,
        mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse(
          StapelArtEnum.MbwA,
          ergebnisse
        ),
        true
      );

      await postErgebnisse(
        wahlbezirkID,
        wahlID,
        StapelArtEnum.MbwB,
        mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse(
          StapelArtEnum.MbwB,
          ergebnisse
        ),
        true
      );
    } catch {
      throw new Error("Fehler beim Speichern der Ergebnisse");
    } finally {
      isErgebnisseSaving.value = false;
    }
  }

  async function loadAndCombineErgebnisseAndWahlvorschlaege() {
    const ergebnisse: MbwErgebnisseAndWahlvorschlag[] = [];

    const wahlvorschlaege = await _loadWahlvorschlaege();
    const ergebnisseStapelA = await _loadGueltigeErgebnisseByStapelArt(
      StapelArtEnum.MbwA
    );
    const ergebnisseStapelB = await _loadGueltigeErgebnisseByStapelArt(
      StapelArtEnum.MbwB
    );

    for (const wahlvorschlag of wahlvorschlaege.wahlvorschlaege) {
      const ergebnisStapelAForWahlvorschlag =
        ergebnisseStapelA?.ergebnisse.find(
          (ergebnis) => ergebnis.wahlvorschlagID === wahlvorschlag.identifikator
        );
      const ergebnisStapelBForWahlvorschlag =
        ergebnisseStapelB?.ergebnisse.find(
          (ergebnis) => ergebnis.wahlvorschlagID === wahlvorschlag.identifikator
        );

      ergebnisse.push({
        wahlvorschlag: wahlvorschlag,
        ergebnisStapelA:
          ergebnisStapelAForWahlvorschlag ??
          _createEmptyErgebnisForWahlvorschlag(wahlvorschlag),
        ergebnisStapelB:
          ergebnisStapelBForWahlvorschlag ??
          _createEmptyErgebnisForWahlvorschlag(wahlvorschlag),
      });
    }
    return ergebnisse;
  }

  async function sendSchnellmeldung() {
    isSendingSchnellmeldung.value = true;
    const status = await loadStatusByWahlIdAndWahlbezirkId(
      wahlID,
      wahlbezirkID
    );

    try {
      const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);
      if (!wahl) {
        logError(`zur wahlID ${wahlID} existiert keine Wahl`);
      } else {
        await postSchnellmeldung(
          wahlID,
          wahlbezirkID,
          currentUserWahlbezirkID.value,
          wahl.waehlerverzeichnisNummer
        )
          .then(() => {
            status.schnellmeldung.uebermittelt = true;
          })
          .catch(() => {
            status.schnellmeldung.uebermittelt = false;
          })
          .finally(async () => {
            status.schnellmeldung.validierungsstatus =
              MeldungValidierungsstatusEnum.Valide;
            status.schnellmeldung.sendeuhrzeit =
              toYyyyMmDdWithTimeWithoutTimezoneOffset(new Date());
            await postStatus(wahlID, wahlbezirkID, status, false);
          });
      }
    } finally {
      isSendingSchnellmeldung.value = false;
    }
  }

  async function updateStatusAfterSchnellmeldungDrucken() {
    const status = await loadStatusByWahlIdAndWahlbezirkId(
      wahlID,
      wahlbezirkID
    );
    status.schnellmeldung.gedruckt = true;
    await postStatus(wahlID, wahlbezirkID, status, false);
  }

  async function sendNiederschrift() {
    isSendingNiederschrift.value = true;
    const status = await loadStatusByWahlIdAndWahlbezirkId(
      wahlID,
      wahlbezirkID
    );

    try {
      const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);
      if (!wahl) {
        logError(`zur wahlID ${wahlID} existiert keine Wahl`);
      } else {
        await postNiederschrift(
          wahlID,
          wahlbezirkID,
          wahl.waehlerverzeichnisNummer,
          currentUserWahlbezirkID.value
        )
          .then(() => {
            status.niederschrift.uebermittelt = true;
          })
          .catch(() => {
            status.niederschrift.uebermittelt = false;
          })
          .finally(async () => {
            status.niederschrift.validierungsstatus =
              MeldungValidierungsstatusEnum.Valide;
            status.niederschrift.sendeuhrzeit =
              toYyyyMmDdWithTimeWithoutTimezoneOffset(new Date());
            await postStatus(wahlID, wahlbezirkID, status, false);
          });
      }
    } finally {
      isSendingNiederschrift.value = false;
    }
  }

  async function sendAusdruckNiederschrift(
    meldungsart: MeldungsartEnum,
    ausdruck: string
  ) {
    const status = await loadStatusByWahlIdAndWahlbezirkId(
      wahlID,
      wahlbezirkID
    );
    try {
      await postAusdruck(wahlbezirkID, wahlID, meldungsart, ausdruck).then(
        async () => {
          status.niederschrift.gedruckt = true;
          await postStatus(wahlID, wahlbezirkID, status, false);
        }
      );
    } catch {
      logError("Fehler beim Speichern des Ausdrucks");
    }
  }

  async function _loadGueltigeErgebnisseByStapelArt(stapelArt: StapelArtEnum) {
    try {
      return await getErgebnisse(wahlbezirkID, wahlID, stapelArt, false);
    } catch {
      throw new Error("Fehler beim Laden der Ergebnisse");
    }
  }

  async function _loadWahlvorschlaege() {
    try {
      return await getWahlvorschlaege(wahlID, wahlbezirkID);
    } catch {
      throw new Error("Fehler beim Laden der Wahlvorschläge");
    }
  }

  function _createEmptyErgebnisForWahlvorschlag(wahlvorschlag: Wahlvorschlag) {
    return {
      wahlvorschlagID: wahlvorschlag.identifikator,
      kandidatID: null,
      wahlvorschlagsOrdnungszahl: wahlvorschlag.ordnungszahl,
      ergebnis: null,
      numIndex: null,
    };
  }

  return {
    isErgebnisseSaving,
    isSendingSchnellmeldung,
    isSendingNiederschrift,
    saveGueltigeErgebnisse,
    loadAndCombineErgebnisseAndWahlvorschlaege,
    getAWerteForWahlbezirkAndWahl: () =>
      getAWerteForWahlbezirkAndWahl(wahlbezirkID, wahlID),
    sendSchnellmeldung,
    sendAusdruckNiederschrift,
    updateStatusAfterSchnellmeldungDrucken,
    sendNiederschrift,
  };
}
