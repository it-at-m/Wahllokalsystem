import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";
import type { Ergebnis } from "@/types/ergebnismeldung/common/Ergebnis.ts";
import type { Ergebnisse } from "@/types/ergebnismeldung/common/Ergebnisse.ts";
import type { MeldungsartEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import type { Status } from "@/types/ergebnismeldung/common/Status.ts";
import type { NiederschriftBeanstandeteWahlbriefe } from "@/types/ergebnismeldung/MBW/niederschrift/NiederschriftBeanstandeteWahlbriefe";
import type { NiederschriftDruckInputBase } from "@/types/ergebnismeldung/MBW/niederschrift/NiederschriftDruckInputBase.ts";
import type {
  NiederschriftDruckInputBWB,
  NiederschriftUhrzeit,
} from "@/types/ergebnismeldung/MBW/niederschrift/NiederschriftDruckInputBWB.ts";
import type { NiederschriftDruckInputUWB } from "@/types/ergebnismeldung/MBW/niederschrift/NiederschriftDruckInputUWB.ts";
import type { NiederschriftEreignisse } from "@/types/ergebnismeldung/MBW/niederschrift/NiederschriftEreignisse.ts";
import type { NiederschriftGueltigeStimme } from "@/types/ergebnismeldung/MBW/niederschrift/NiederschriftGueltigeStimme";
import type { NiederschriftGueltigeStimmenErgebnisGesamt } from "@/types/ergebnismeldung/MBW/niederschrift/NiederschriftGueltigeStimmenErgebnisGesamt";
import type { NiederschriftWahlbriefdaten } from "@/types/ergebnismeldung/MBW/niederschrift/NiederschriftWahlbriefdaten";
import type { NiederschriftWahlvorstandsmitglied } from "@/types/ergebnismeldung/MBW/niederschrift/NiederschriftWahlvorstandsmitglied";
import type { Wahl } from "@/types/wahl/Wahl.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { storeToRefs } from "pinia";
import { computed, ref } from "vue";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useLogging } from "@/composables/common/logging.ts";
import { useStringNumberMapTools } from "@/composables/common/stringNumberMapTools.ts";
import { useCommonPrintService } from "@/composables/drucken/commonPrintService.ts";
import { useAllStimmzettelOfWahlbezirkState } from "@/composables/dse/allStimmzettelOfWahlbezirkState.ts";
import { useMbwStimmzettelFilterService } from "@/composables/dse/mbwStimmzettelFilterService.ts";
import { useStimmzettelZusammenfassungUtils } from "@/composables/dse/stimmzettelerfassung/stimmzettelZusammenfassungUtils.ts";
import { useAWerteService } from "@/composables/ergebnismeldung/common/aWerteService.ts";
import { useBWerteService } from "@/composables/ergebnismeldung/common/bWerteService.ts";
import { useErgebnisService } from "@/composables/ergebnismeldung/common/ergebnisService.ts";
import { useWahlscheineService } from "@/composables/ergebnismeldung/common/wahlscheineService.ts";
import { useErgebnisTools } from "@/composables/ergebnismeldung/ergebnisTools.ts";
import { useBedenklicheStimmzettelService } from "@/composables/ergebnismeldung/MBW/bedenklicheStimmzettelService.ts";
import { useStimmabgabevermerkeService } from "@/composables/stimmabgabevermerke/stimmabgabevermerkeService.ts";
import { useWaehlerverzeichnisService } from "@/composables/wahlhandlung/waehlerverzeichnisService.ts";
import { useWahlvorbereitungService } from "@/composables/wahlhandlung/wahlvorbereitungService.ts";
import { useWahlvorstandService } from "@/composables/wahlvorstand/wahlvorstandService.ts";
import { useEreignisStore } from "@/stores/ereignisStore.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { useWahlvorschlaegeStore } from "@/stores/wahlvorschlaegeStore.ts";
import { ZurueckweisungsgrundEnum } from "@/types/briefwahl/ZurueckweisungsgrundEnum.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";
import { ValidityEnum } from "@/types/ergebnismeldung/MBW/bedenklicheStimmzettel/ValidityEnum.ts";
import { Partei } from "@/types/ergebnismeldung/MBW/niederschrift/NiederschriftDruckInputBWB.ts";
import { EingenommenerWahlscheinStimmzettelartEnum } from "@/types/stimmabgabevermerke/EingenommenerWahlscheinStimmzettelartEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const { logError } = useLogging("mbwUtilsNiederschrift");
const { toGermanDate, toHhMm } = useDateTimeFormatter();
const { getErgebnisse } = useErgebnisService();
const { createWithWahlvorschlagIDAndErgebnis } = useErgebnisTools();

export function useMbtUtilsNiederschrift(wahlID: string, wahlbezirkID: string) {
  const { getAWerteForWahlbezirkAndWahl } = useAWerteService();
  const { getUrnenwahlvorbereitung } = useWahlvorbereitungService();
  const { getStimmabgabevermerke } = useStimmabgabevermerkeService();
  const { getBegruendungStimmzettelumschlaege } = useErgebnisService();
  const { getWaehlerverzeichnis } = useWaehlerverzeichnisService();
  const { getWahlvorstand } = useWahlvorstandService();
  const { getWahlscheine } = useWahlscheineService();
  const { getBedenklicheStimmzettel } = useBedenklicheStimmzettelService();

  const { stimmzettelOfWahlbezirk, loadStimmzettelOfWahlbezirk } =
    useAllStimmzettelOfWahlbezirkState(wahlID, wahlbezirkID);
  const {
    stapelA: stapelAStimmzettel,
    stapelB: stapelBStimmzettel,
    stapelBC: stapelBCStimmzettel,
    stapelDUngueltig,
    stapelEUngueltig,
  } = useMbwStimmzettelFilterService(stimmzettelOfWahlbezirk);

  const { getWahlvorschlaegeByWahlIDAndWahlbezirkID } =
    useWahlvorschlaegeStore();

  const { wahlenActions, waehlerverzeichnisActions } = useWahlenStore();
  const {
    eroeffnungsuhrzeitState,
    schliessungsuhrzeitState,
    wahlbriefDatenState,
  } = storeToRefs(useWahlbezirkStore());
  const { currentUserWahlbezirkNummer, currentUserWahlbezirksArt } =
    storeToRefs(useUserStore());
  const { wahlbezirkEreignisse } = storeToRefs(useEreignisStore());
  const { stimmzettelumschlaegeState } = storeToRefs(useWahlenStore());
  const { isDseAktiv } = storeToRefs(useInfomanagementStore());

  const wahlvorschlaegeByWahlIDAndWahlbezirkID =
    getWahlvorschlaegeByWahlIDAndWahlbezirkID(wahlID, wahlbezirkID);
  const { getBWerteForWahlbezirkAndWahl } = useBWerteService(
    wahlID,
    wahlbezirkID
  );
  const { createFooter, createBarcode } = useCommonPrintService();

  const gueltigeStimmenListe = ref<NiederschriftGueltigeStimme[]>([]);
  const gueltigeStimmenErgebnisGesamt =
    ref<NiederschriftGueltigeStimmenErgebnisGesamt>({
      gesamt: 0,
      stapelA: 0,
      stapelB: 0,
      stapelBC: 0,
    });

  async function prepareDataForNiederschriftDruck(
    status: Status,
    meldungsart: MeldungsartEnum,
    wahl: Wahl
  ): Promise<NiederschriftDruckInputBWB | NiederschriftDruckInputUWB> {
    const wahltagFormatiert = toGermanDate(wahl.wahltag);
    const barcode = createBarcode(
      wahl,
      meldungsart,
      currentUserWahlbezirksArt.value,
      currentUserWahlbezirkNummer.value
    );
    const wahlbezirkNummer = currentUserWahlbezirkNummer.value;

    if (isDseAktiv.value) {
      await loadStimmzettelOfWahlbezirk();
    }

    const wahlvorstaende = await _getWahlvorstand();
    const eroeffnungsuhrzeit: NiederschriftUhrzeit = _getEroeffnungsuhrzeit();
    const schliessungsuhrzeit: NiederschriftUhrzeit = _getSchliessungsuhrzeit();
    const anzahlStimmzettel = wahl.stimmzettelumschlaege.anzahlWaehler;
    const anzahlWahlscheine = await _getAnzahlWahlscheine();
    const begruendung = await _getBegruendungStimmzettelumschlaege(wahl);
    const bWerte = await getBWerteForWahlbezirkAndWahl();
    const ungueltigeStimmen = await _getUngueltigeStimmenzettel();
    await _getStimmenListeUndErgebniseGesamt();
    const parteienListe = await _getParteienListe();
    const ereignisse = _getEreignisse();
    const footer = createFooter(status, meldungsart);
    const niederschriftDruckInputBaseData: NiederschriftDruckInputBase = {
      aktuelleWahl: wahl,
      wahltagFormatiert: wahltagFormatiert || "",
      barcode: barcode,
      wahlbezirkNummer: wahlbezirkNummer,
      wahlvorstaende: wahlvorstaende,
      eroeffnungsuhrzeit: eroeffnungsuhrzeit,
      schliessungsuhrzeit: schliessungsuhrzeit,
      anzahlStimmzettel: anzahlStimmzettel || 0,
      anzahlWahlscheine: anzahlWahlscheine || 0,
      begruendungStimmzettelumschlaege: { grund: begruendung },
      bWerte: bWerte.b || 0,
      ungueltigeStimmen: ungueltigeStimmen || 0,
      gueltigeStimmenListe: gueltigeStimmenListe.value,
      gueltigeStimmenErgebnisGesamt: gueltigeStimmenErgebnisGesamt.value,
      parteienListe: parteienListe,
      ereignisse: ereignisse,
      footer: footer,
    };

    if (currentUserWahlbezirksArt.value == WahlbezirksArtEnum.UWB) {
      const anzahlWahltische = await _getAnzahlWahltische();
      const wvz = await _getWaehlerverzeichnisData();
      const aWerte = await getAWerteForWahlbezirkAndWahl(wahlbezirkID, wahlID);
      const aWerteGesamt = aWerte.a1 + (aWerte.a2 || 0);
      return {
        ...niederschriftDruckInputBaseData,
        anzahlStimmabgabevermerke: bWerte.b,
        aWerte: aWerteGesamt,
        a1: aWerte.a1,
        a2: aWerte.a2 || 0,
        wvz: wvz,
        anzahlWahltische: anzahlWahltische || 0,
        b1: bWerte.b1,
      };
    } else {
      const beanstandeteWahlbriefe = _getBeanstandeteWahlbriefe();
      const wahlbriefdaten = _getWahlbriefdaten();
      return {
        ...niederschriftDruckInputBaseData,
        beanstandeteWahlbriefe: beanstandeteWahlbriefe,
        wahlbriefdaten: wahlbriefdaten,
      };
    }
  }

  function _getEroeffnungsuhrzeit() {
    const eroeffnungsuhrzeitInHhMm = toHhMm(
      eroeffnungsuhrzeitState.value.eroeffnungsuhrzeit
    ).split(":");
    return {
      stunde: eroeffnungsuhrzeitInHhMm[0] || "",
      minute: eroeffnungsuhrzeitInHhMm[1] || "",
    };
  }

  function _getSchliessungsuhrzeit() {
    let schliessungsuhrzeitInHhMm;
    if (currentUserWahlbezirksArt.value === WahlbezirksArtEnum.UWB) {
      schliessungsuhrzeitInHhMm = toHhMm(
        schliessungsuhrzeitState.value.schliessungsuhrzeit
      ).split(":");
    } else {
      schliessungsuhrzeitInHhMm = toHhMm(
        stimmzettelumschlaegeState.value.urneneroeffnungsUhrzeitSent
      ).split(":");
    }
    return {
      stunde: schliessungsuhrzeitInHhMm[0] || "",
      minute: schliessungsuhrzeitInHhMm[1] || "",
    };
  }

  async function _getWahlvorstand(): Promise<
    NiederschriftWahlvorstandsmitglied[]
  > {
    try {
      const wahlvorstand = await getWahlvorstand(wahlbezirkID);
      return wahlvorstand.wahlvorstandsmitglieder.map((mitglied) => ({
        nachname: mitglied.familienname ?? "",
        vorname: mitglied.vorname ?? "",
        funktionsName: mitglied.funktionsname ?? "",
      }));
    } catch {
      throw new Error("Wahlvorstand konnte nicht geladen werden");
    }
  }

  function _getWahlbriefdaten(): NiederschriftWahlbriefdaten {
    const wb = wahlbriefDatenState.value.wahlbriefDaten;
    return {
      wahlbriefe: wb.wahlbriefe ?? "",
      verzeichnisseUngueltige: wb.verzeichnisseUngueltige ?? "",
      nachtraege: wb.nachtraege ?? "",
      nachtraeglichUeberbrachte: wb.nachtraeglichUeberbrachte ?? 0,
    } as NiederschriftWahlbriefdaten;
  }

  function _getBeanstandeteWahlbriefe(): NiederschriftBeanstandeteWahlbriefe {
    const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);

    const beanstandeteWahlbriefe: NiederschriftBeanstandeteWahlbriefe = {
      gesamt: 0,
      keinGueltigerWahlschein: 0,
      keineUnterschrift: 0,
      keinStimmzettelumschlag: 0,
      nichtVerschlossen: 0,
      mehrereStimmzettelumschlaege: 0,
      keinAmtlicherStimmzettelumschlag: 0,
      gegenstandImUmschlag: 0,
      gefaehrdetWahlgeheimnis: 0,
      gesamtMinusZugelassen: 0,
      zugelassen: 0,
    } as NiederschriftBeanstandeteWahlbriefe;

    if (!wahl || !wahl.beanstandeteWahlbriefe) {
      return beanstandeteWahlbriefe;
    }

    wahl.beanstandeteWahlbriefe.forEach((grund) => {
      if (!grund) return;
      switch (grund) {
        case ZurueckweisungsgrundEnum.Zugelassen:
          beanstandeteWahlbriefe.gesamt++;
          beanstandeteWahlbriefe.zugelassen++;
          break;
        case ZurueckweisungsgrundEnum.ScheinUngueltig:
        case ZurueckweisungsgrundEnum.KeinOriginalSchein:
          beanstandeteWahlbriefe.gesamt++;
          beanstandeteWahlbriefe.gesamtMinusZugelassen++;
          beanstandeteWahlbriefe.keinGueltigerWahlschein++;
          break;
        case ZurueckweisungsgrundEnum.UnterschriftFehlt:
          beanstandeteWahlbriefe.gesamt++;
          beanstandeteWahlbriefe.gesamtMinusZugelassen++;
          beanstandeteWahlbriefe.keineUnterschrift++;
          break;
        case ZurueckweisungsgrundEnum.UmschlagFehlt:
          beanstandeteWahlbriefe.gesamt++;
          beanstandeteWahlbriefe.gesamtMinusZugelassen++;
          beanstandeteWahlbriefe.keinStimmzettelumschlag++;
          break;
        case ZurueckweisungsgrundEnum.WahlbriefUndUmschlagOffen:
          beanstandeteWahlbriefe.gesamt++;
          beanstandeteWahlbriefe.gesamtMinusZugelassen++;
          beanstandeteWahlbriefe.nichtVerschlossen++;
          break;
        case ZurueckweisungsgrundEnum.ScheineUngleichUmschlaege:
          beanstandeteWahlbriefe.gesamt++;
          beanstandeteWahlbriefe.gesamtMinusZugelassen++;
          beanstandeteWahlbriefe.mehrereStimmzettelumschlaege++;
          break;
        case ZurueckweisungsgrundEnum.UmschlagNichtAmtlich:
          beanstandeteWahlbriefe.gesamt++;
          beanstandeteWahlbriefe.gesamtMinusZugelassen++;
          beanstandeteWahlbriefe.keinAmtlicherStimmzettelumschlag++;
          break;
        case ZurueckweisungsgrundEnum.UmschlagGefaehrdetWahlgeheimnis:
          beanstandeteWahlbriefe.gesamt++;
          beanstandeteWahlbriefe.gesamtMinusZugelassen++;
          beanstandeteWahlbriefe.gefaehrdetWahlgeheimnis++;
          break;
        case ZurueckweisungsgrundEnum.GegenstandImUmschlag:
          beanstandeteWahlbriefe.gesamt++;
          beanstandeteWahlbriefe.gesamtMinusZugelassen++;
          beanstandeteWahlbriefe.gegenstandImUmschlag++;
          break;
        case ZurueckweisungsgrundEnum.NichtWahlberechtigt:
          beanstandeteWahlbriefe.gesamt++;
          beanstandeteWahlbriefe.zugelassen++;
          break;
        default:
          beanstandeteWahlbriefe.gesamt++;
          beanstandeteWahlbriefe.gesamtMinusZugelassen++;
          break;
      }
    });

    return beanstandeteWahlbriefe;
  }

  async function _getUngueltigeStimmenzettel(): Promise<number | undefined> {
    return isDseAktiv.value
      ? _getUngueltigeStimmenStimmzettelByStimmzettel()
      : _getUngueltigeStimmzettelByStapel();
  }

  async function _getUngueltigeStimmzettelByStapel() {
    if (wahlbezirkID) {
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
      }
    }
  }

  async function _getUngueltigeStimmenStimmzettelByStimmzettel() {
    return stapelEUngueltig.value.length + stapelDUngueltig.value.length;
  }

  async function _getStimmenListeUndErgebniseGesamt() {
    let sumStapelA = 0;
    let sumStapelB = 0;
    let sumStapelBC = 0;
    let sumGesamt = 0;
    // @ts-expect-error old code, will be refactored later, will be refactored later
    const ergebnisArray = [];
    const gueltigeStimmabgaben = await _getGueltigeStimmabgabe();
    if (gueltigeStimmabgaben) {
      gueltigeStimmabgaben.forEach((erg) => {
        const listElement = {
          ordnungszahl: erg.ordnungszahl,
          bewerbername: erg.bewerbername,
          parteiname: erg.wahlvorschlag,
          stapelA: erg.stapelA,
          stapelB: erg.stapelB,
          stapelBC: erg.stapelBC,
          gesamt: erg.gesamt,
        };
        ergebnisArray.push(listElement);
        sumStapelA += erg.stapelA;
        sumStapelB += erg.stapelB;
        sumStapelBC += erg.stapelBC;
        sumGesamt += erg.gesamt;
      });

      gueltigeStimmenErgebnisGesamt.value = {
        stapelA: sumStapelA,
        stapelB: sumStapelB,
        stapelBC: sumStapelBC,
        gesamt: sumGesamt,
      };
      // @ts-expect-error old code, will be refactored later
      gueltigeStimmenListe.value = ergebnisArray;
    }
  }

  function _stimmzettelStapelToErgebnisse(
    stimmzettel: PersistedStimmzettel[],
    stapelArt: StapelArtEnum
  ): Ergebnisse {
    const container = new Map<string, number>();
    const sumTool = useStringNumberMapTools(container);
    stimmzettel
      .flatMap((stimmzettel) => stimmzettel.wahlvorschlaege)
      .forEach((wahlvorschlag) =>
        sumTool.add(wahlvorschlag.wahlvorschlagID, 1)
      );

    const ergebnisse: Ergebnis[] = [];
    container.forEach((ergebnis, wahlvorschlagID) => {
      ergebnisse.push(
        createWithWahlvorschlagIDAndErgebnis(wahlvorschlagID, ergebnis)
      );
    });

    return {
      ergebnisse: ergebnisse,
      bezirkUndWahlIDStapelart: {
        wahlID,
        wahlbezirkID,
        stapelArt,
      },
    };
  }

  function _stimmzettelBCToErgebnisse(): Ergebnisse | null {
    if (wahlvorschlaegeByWahlIDAndWahlbezirkID) {
      const { wahlvorschlaegeWithKandidatenErgebnissen } =
        useStimmzettelZusammenfassungUtils(
          stapelBCStimmzettel,
          computed(() => wahlvorschlaegeByWahlIDAndWahlbezirkID.wahlvorschlaege)
        );
      const ergebnisse = wahlvorschlaegeWithKandidatenErgebnissen.value
        .flatMap((wke) => wke.kandidatenErgebnisse)
        .map((ke) => ke.ergebnis);
      return {
        ergebnisse,
        bezirkUndWahlIDStapelart: {
          wahlID,
          wahlbezirkID,
          stapelArt: StapelArtEnum.MbwBC,
        },
      };
    } else {
      return null;
    }
  }

  async function _getGueltigeStimmabgabe() {
    const stapelA = isDseAktiv.value
      ? _stimmzettelStapelToErgebnisse(
          stapelAStimmzettel.value,
          StapelArtEnum.MbwA
        )
      : await getErgebnisse(wahlbezirkID, wahlID, StapelArtEnum.MbwA, false);

    const stapelB = isDseAktiv.value
      ? _stimmzettelStapelToErgebnisse(
          stapelBStimmzettel.value,
          StapelArtEnum.MbwB
        )
      : await getErgebnisse(wahlbezirkID, wahlID, StapelArtEnum.MbwB, false);

    const stapelBC = isDseAktiv.value
      ? _stimmzettelBCToErgebnisse()
      : await getErgebnisse(wahlbezirkID, wahlID, StapelArtEnum.MbwBC, false);

    if (wahlvorschlaegeByWahlIDAndWahlbezirkID) {
      // @ts-expect-error old code, will be refactored later
      const tmpGueltigeStimmabgaben = [];

      wahlvorschlaegeByWahlIDAndWahlbezirkID.wahlvorschlaege.forEach((erg) => {
        const gueltigeStimmabgabe = {
          ordnungszahl: erg.ordnungszahl,
          wahlvorschlag: erg.kurzname,
          wahlvorschlagID: erg.identifikator,
          bewerbername:
            // @ts-expect-error old code, will be refactored later
            erg.kandidaten[0] && erg.kandidaten[0]["name"]
              ? // @ts-expect-error old code, will be refactored later
                erg.kandidaten[0]["name"]
              : "",
          stapelA: 0,
          stapelB: 0,
          stapelBC: 0,
          gesamt: 0,
        };

        if (stapelA) {
          stapelA.ergebnisse.forEach((ergebnis) => {
            if (erg.identifikator === ergebnis.wahlvorschlagID) {
              gueltigeStimmabgabe.stapelA = ergebnis.ergebnis || 0;
            }
          });
        }

        if (stapelB) {
          stapelB.ergebnisse.forEach((ergebnis) => {
            if (erg.identifikator === ergebnis.wahlvorschlagID) {
              gueltigeStimmabgabe.stapelB = ergebnis.ergebnis || 0;
            }
          });
        }

        if (stapelBC) {
          stapelBC.ergebnisse.forEach((ergebnis) => {
            if (erg.identifikator === ergebnis.wahlvorschlagID) {
              gueltigeStimmabgabe.stapelBC += ergebnis.ergebnis || 0;
            }
          });
        }
        gueltigeStimmabgabe.gesamt =
          gueltigeStimmabgabe.stapelA + gueltigeStimmabgabe.stapelB;
        tmpGueltigeStimmabgaben.push(gueltigeStimmabgabe);
      });
      // @ts-expect-error old code, will be refactored later
      tmpGueltigeStimmabgaben.sort((a, b) => a.ordnungszahl > b.ordnungszahl);
      // @ts-expect-error old code, will be refactored later
      return tmpGueltigeStimmabgaben;
    }
  }

  function _createParteeienListe(
    wahlvorschlaege: Wahlvorschlag[],
    // @ts-expect-error old code, will be refactored later
    stapelBC,
    // @ts-expect-error old code, will be refactored later
    forTemplate
  ) {
    // @ts-expect-error old code, will be refactored later
    const pListe = [];
    wahlvorschlaege.forEach((wv) => {
      // @ts-expect-error old code, will be refactored later
      const colSums = [];
      const bcKandidaten = stapelBC.ergebnisse
        // @ts-expect-error old code, will be refactored later
        .filter((k) => k.wahlvorschlagID === wv.identifikator)
        // @ts-expect-error old code, will be refactored later
        .sort((a, b) => (a.listenposition > b.listenposition ? 1 : -1));
      let partei = new Partei(wv.identifikator, wv.kurzname, wv.ordnungszahl);
      // @ts-expect-error old code, will be refactored later
      wv.kandidaten
        .sort((a, b) => (a.listenposition > b.listenposition ? 1 : -1))
        .forEach((kand) => {
          const bckand = bcKandidaten.find(
            // @ts-expect-error old code, will be refactored later
            (bck) => bck.kandidatID === kand.identifikator
          );
          // @ts-expect-error old code, will be refactored later
          kand["ergebnis"] = bckand ? bckand.ergebnis : null;
          // @ts-expect-error old code, will be refactored later
          kand["wahlvorschlagID"] = bckand
            ? bckand.wahlvorschlagID
            : wv.identifikator;
          if (forTemplate && kand.listenposition === 0) {
            // @ts-expect-error old code, will be refactored later
            kand["laufendeNr"] =
              parseInt(partei.ordnungszahl) * 100 +
              //@ts-expect-error old code, will be refactored later
              parseInt(kand.listenposition);
            // @ts-expect-error old code, will be refactored later
            partei["direktKandMit00"] = kand;
          } else {
            partei.pushKandidat(kand);
          }
          colSums[kand.tabellenSpalteInNiederschrift] =
            // @ts-expect-error old code, will be refactored later
            parseInt(colSums[kand.tabellenSpalteInNiederschrift] || 0) +
            // @ts-expect-error old code, will be refactored later
            (parseInt(kand.ergebnis) || 0);
        });
      if (forTemplate) {
        // @ts-expect-error old code, will be refactored later
        partei = _bearbeiteForTemplate(partei, colSums);
      }
      pListe.push(partei);
    });
    // @ts-expect-error old code, will be refactored later
    return pListe;
  }

  //@ts-expect-error old code, will be refactored later
  function _bearbeiteForTemplate(partei, colSums) {
    let maxCols = 0;
    // eslint-disable-next-line @typescript-eslint/prefer-for-of
    for (let row = 0; row < partei._tabledata.length; row++) {
      maxCols =
        maxCols > partei._tabledata[row].length
          ? maxCols
          : partei._tabledata[row].length;
    }

    partei["maxcols"] = [];

    let width1 = 0;
    let width2 = 0;
    if (maxCols) {
      width1 = (0.28 * 17) / maxCols;
      width2 = (0.72 * 17) / maxCols;
    }
    for (let k = 0; k < maxCols; k++) {
      partei.maxcols.push({
        width1: width1.toFixed(2),
        width2: width2.toFixed(2),
        colsum: colSums[k],
      });
    }
    return partei;
  }

  function _getEreignisse() {
    const ereignisData: NiederschriftEreignisse = {
      hasEreignisse: false,
      vorfaelle: [],
      vorkommnisse: [],
    };
    wahlbezirkEreignisse.value.ereigniseintraege.forEach((eintrag) => {
      if (eintrag.ereignisart === "VORFALL") {
        const uhrzeit = toHhMm(eintrag.uhrzeit);
        ereignisData.vorfaelle.push({
          uhrzeit: uhrzeit,
          beschreibung: eintrag.beschreibung || "",
        });
      } else {
        const uhrzeit = toHhMm(eintrag.uhrzeit);
        ereignisData.vorkommnisse.push({
          uhrzeit: uhrzeit,
          beschreibung: eintrag.beschreibung || "",
        });
      }
    });

    return ereignisData;
  }

  async function _getBegruendungStimmzettelumschlaege(wahl: Wahl) {
    try {
      const begruendung = await getBegruendungStimmzettelumschlaege(
        wahl,
        wahlbezirkID,
        "STIMMZETTEL_UMSCHLAEGE"
      );
      if (begruendung && begruendung.grund) {
        const begruendungsArray = begruendung.grund.split(" ");
        let begruendungsString = "";
        let zeileNr = 0;
        begruendungsArray.forEach((wort) => {
          begruendungsString += wort + " ";
          if (
            begruendungsString.length &&
            // @ts-expect-error old code, will be refactored later
            parseInt(begruendungsString.length / 80) !== zeileNr
          ) {
            begruendungsString += "<br/>";
            zeileNr++;
          }
        });
        return begruendungsString;
      }
    } catch {
      throw new Error(
        `Fehler beim Laden der Begruendungen der Stimmzettelumschlaege`
      );
    }
    return "";
  }

  async function _getWaehlerverzeichnisData() {
    const waehlerverzeichnisNummer =
      waehlerverzeichnisActions.getWaehlerverzeichnisNummerOrUndefinedById(
        wahlID
      );
    if (waehlerverzeichnisNummer) {
      let waehlerverzeichnis;
      try {
        waehlerverzeichnis = await getWaehlerverzeichnis(
          wahlbezirkID,
          waehlerverzeichnisNummer
        );
      } catch {
        throw new Error(`Fehler beim Laden des Waehlerverzeichnis`);
      }
      return {
        nachtraeglicheBerichtigung:
          waehlerverzeichnis.nachtraeglicheBerichtigung,
        verzeichnisLagVor: waehlerverzeichnis.waehlerverzeichnisUnchanged,
        berichtigungVorBeginnDerAbstimmung:
          !waehlerverzeichnis.waehlerverzeichnisUnchanged,
      };
    } else {
      throw new Error("Waehlerverzeichnisnummer ist nicht vorhanden");
    }
  }

  async function _getAnzahlWahltische() {
    try {
      const urnenwahlvorbereitung =
        await getUrnenwahlvorbereitung(wahlbezirkID);
      return urnenwahlvorbereitung ? urnenwahlvorbereitung.anzahlWahltische : 0;
    } catch {
      throw new Error("Fehler beim Laden der Urnenwahlvorbereitung");
    }
  }

  async function _getParteienListe() {
    let parteienListeForTemplate;
    const stapelBC = isDseAktiv.value
      ? _stimmzettelBCToErgebnisse()
      : await getErgebnisse(wahlbezirkID, wahlID, StapelArtEnum.MbwBC, false);

    if (wahlvorschlaegeByWahlIDAndWahlbezirkID) {
      parteienListeForTemplate = _createParteeienListe(
        wahlvorschlaegeByWahlIDAndWahlbezirkID.wahlvorschlaege.sort((a, b) =>
          a.ordnungszahl > b.ordnungszahl ? 1 : -1
        ),
        stapelBC,
        true
      );
    }
    return parteienListeForTemplate;
  }

  async function _getAnzahlWahlscheine() {
    try {
      if (currentUserWahlbezirksArt.value === WahlbezirksArtEnum.UWB) {
        const waehlerverzeichnisNummer =
          waehlerverzeichnisActions.getWaehlerverzeichnisNummerOrUndefinedById(
            wahlID
          );
        if (waehlerverzeichnisNummer) {
          const loadedStimmabgabevermerke = await getStimmabgabevermerke(
            wahlbezirkID,
            wahlID,
            waehlerverzeichnisNummer
          );
          if (loadedStimmabgabevermerke) {
            const wahldatenForWahl = loadedStimmabgabevermerke;
            if (wahldatenForWahl?.eingenommeneWahlscheine) {
              const kleineWahlscheine =
                wahldatenForWahl.eingenommeneWahlscheine.get(
                  EingenommenerWahlscheinStimmzettelartEnum.Klein
                );
              return kleineWahlscheine ?? 0;
            }
          }
        }
      } else {
        const loadedWahlscheine = await getWahlscheine(wahlID, wahlbezirkID);
        if (loadedWahlscheine) {
          return loadedWahlscheine.stimmabgabevermerke ?? 0;
        }
      }
    } catch {
      throw new Error(`Fehler beim Laden der Wahlscheine`);
    }
  }

  return {
    prepareDataForNiederschriftDruck,
  };
}
