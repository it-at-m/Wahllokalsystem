import type { MeldungsartEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import type { SchnellmeldungDruckInput } from "@/types/ergebnismeldung/common/SchnellmeldungDruckInput.ts";
import type { Status } from "@/types/ergebnismeldung/common/Status.ts";
import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnismeldung/MBW/MbwErgebnisseAndWahlvorschlag.ts";
import type { Wahl } from "@/types/wahl/Wahl.ts";

import { storeToRefs } from "pinia";
import { computed } from "vue";

import { useLogging } from "@/composables/common/logging.ts";
import { useNumberFormatter } from "@/composables/common/numberFormatter.ts";
import { useCommonPrintService } from "@/composables/drucken/commonPrintService.ts";
import { useAllStimmzettelOfWahlbezirkState } from "@/composables/dse/allStimmzettelOfWahlbezirkState.ts";
import { useMbwStimmzettelFilterService } from "@/composables/dse/mbwStimmzettelFilterService.ts";
import { useAWerteService } from "@/composables/ergebnismeldung/common/aWerteService.ts";
import { useBWerteService } from "@/composables/ergebnismeldung/common/bWerteService.ts";
import { useErgebnisService } from "@/composables/ergebnismeldung/common/ergebnisService.ts";
import { useBedenklicheStimmzettelService } from "@/composables/ergebnismeldung/MBW/bedenklicheStimmzettelService.ts";
import { useMbwErgebnisseAndWahlvorschlagStapelSumReactiveMapper } from "@/composables/ergebnismeldung/MBW/mbwErgebnisseAndWahlvorschlagStapelSumReactiveMapper.ts";
import { useMbwUtils } from "@/composables/ergebnismeldung/MBW/mbwUtils.ts";
import { useWahlvorschlaegeService } from "@/composables/wahlvorschlaege/wahlvorschlaegeService.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { ValidityEnum } from "@/types/ergebnismeldung/MBW/bedenklicheStimmzettel/ValidityEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

export function useMbwSchnellmeldungDruckUtils(
  wahlID: string,
  wahlbezirkID: string
) {
  const { convertToSixDigitArray } = useNumberFormatter();
  const { logError } = useLogging("mbwSchnellmeldungDruckUtils");

  const { isDseAktiv } = storeToRefs(useInfomanagementStore());
  const { currentUserWahlbezirkNummer, currentUserWahlbezirksArt } =
    storeToRefs(useUserStore());

  const { getErgebnisse } = useErgebnisService();
  const { getWahlvorschlaege } = useWahlvorschlaegeService();
  const { stimmzettelOfWahlbezirk, loadStimmzettelOfWahlbezirk } =
    useAllStimmzettelOfWahlbezirkState(wahlID, wahlbezirkID);
  const {
    stapelASumGroupedByWahlvorschlag,
    stapelBSumGroupedByWahlvorschlag,
    stapelDUngueltig,
    stapelEUngueltig,
  } = useMbwStimmzettelFilterService(stimmzettelOfWahlbezirk);
  const { loadAndCombineErgebnisseAndWahlvorschlaege } = useMbwUtils(
    wahlID,
    wahlbezirkID
  );
  const { getAWerteForWahlbezirkAndWahl } = useAWerteService();
  const { getBWerteForWahlbezirkAndWahl } = useBWerteService(
    wahlID,
    wahlbezirkID
  );
  const { getBedenklicheStimmzettel } = useBedenklicheStimmzettelService();
  const { createFooter, createBarcode } = useCommonPrintService();

  async function _getWahlvorschlaegeAndErgebnisseAB(): Promise<
    MbwErgebnisseAndWahlvorschlag[]
  > {
    if (isDseAktiv.value) {
      const wahlvorschlaege = await getWahlvorschlaege(wahlID, wahlbezirkID);
      const { wahlvorschlaegeErgebnisseStapelAAndB } =
        useMbwErgebnisseAndWahlvorschlagStapelSumReactiveMapper(
          computed(() => wahlvorschlaege.wahlvorschlaege),
          stapelASumGroupedByWahlvorschlag,
          stapelBSumGroupedByWahlvorschlag
        );
      return wahlvorschlaegeErgebnisseStapelAAndB.value;
    } else {
      return await loadAndCombineErgebnisseAndWahlvorschlaege();
    }
  }

  async function prepareDataForSchnellmeldungDruck(
    wahl: Wahl,
    status: Status,
    meldungsart: MeldungsartEnum
  ): Promise<SchnellmeldungDruckInput> {
    let aWerte = undefined;
    if (currentUserWahlbezirksArt.value == WahlbezirksArtEnum.UWB) {
      aWerte = await getAWerteForWahlbezirkAndWahl(wahlbezirkID, wahlID);
    }

    const bWerte = await getBWerteForWahlbezirkAndWahl();
    if (isDseAktiv.value) {
      await loadStimmzettelOfWahlbezirk();
    }

    const ergebnisseAndWahlvorschlaege =
      await _getWahlvorschlaegeAndErgebnisseAB();

    let gueltigeStimmenGesamt = 0;
    for (const vorschlag of ergebnisseAndWahlvorschlaege) {
      gueltigeStimmenGesamt +=
        (vorschlag.ergebnisStapelA.ergebnis ?? 0) +
        (vorschlag.ergebnisStapelB.ergebnis ?? 0);
    }

    const ungueltigeStimmen = await _getUngueltigeStimmenzettel();

    const stimmenGesamt = gueltigeStimmenGesamt + ungueltigeStimmen;

    const footer = createFooter(status, meldungsart);

    const jpegUrl = createBarcode(
      wahl,
      meldungsart,
      currentUserWahlbezirksArt.value,
      currentUserWahlbezirkNummer.value
    );

    return {
      meldungsArt: meldungsart,
      wahlbezirksArt: currentUserWahlbezirksArt.value,
      aktuelleWahl: wahl,
      footer: footer,
      alleStimmen: convertToSixDigitArray(stimmenGesamt),
      gueltigeStimmenListe: ergebnisseAndWahlvorschlaege,
      gueltigeStimmenGesamt: convertToSixDigitArray(gueltigeStimmenGesamt),
      ungueltigeStimmen: convertToSixDigitArray(ungueltigeStimmen),
      bWerte: bWerte,
      aWerte: aWerte,
      wahlbezirkNummer: currentUserWahlbezirkNummer.value || "",
      barcode: jpegUrl,
      sendOk: status.schnellmeldung.uebermittelt || false,
    };
  }

  async function _getUngueltigeStimmenzettel() {
    return isDseAktiv.value
      ? _getUngueltigeStimmenStimmzettelByStimmzettel()
      : _getUngueltigeStimmzettelByStapel();
  }

  async function _getUngueltigeStimmzettelByStapel() {
    try {
      const loadedErgebnisse = await getErgebnisse(
        wahlbezirkID,
        wahlID,
        "MBW_D_UNGUELTIG",
        false
      );
      const bedenklicheStimmzettel =
        (await getBedenklicheStimmzettel(wahlID, wahlbezirkID)) ?? [];
      const ungueltigeBedenklicheStimmzettel = bedenklicheStimmzettel.filter(
        (stimmzettel) => stimmzettel.validity === ValidityEnum.INVALID
      );

      return (
        (loadedErgebnisse?.ergebnisse[0]?.ergebnis ?? 0) +
        ungueltigeBedenklicheStimmzettel.length
      );
    } catch (error) {
      logError("Fehler beim Laden der Ergebnisse: ", error);
      throw error;
    }
  }

  async function _getUngueltigeStimmenStimmzettelByStimmzettel() {
    return stapelEUngueltig.value.length + stapelDUngueltig.value.length;
  }

  return {
    prepareDataForSchnellmeldungDruck,
  };
}
