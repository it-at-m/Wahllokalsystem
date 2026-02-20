import type { BeanstandeteWahlbriefeDTO } from "@/api/wls-clients/generated-briefwahl-api";
import type { BeanstandeteWahlbriefe } from "@/types/briefwahl/BeanstandeteWahlbriefe.ts";

import { ZurueckweisungsgrundEnum } from "@/types/briefwahl/ZurueckweisungsgrundEnum.ts";

export function useBeanstandeteWahlbriefeMapper() {
  function toModel(
    beanstandeteWahlbriefeDto: BeanstandeteWahlbriefeDTO
  ): BeanstandeteWahlbriefe {
    const beanstandeteWahlbriefe: BeanstandeteWahlbriefe = {
      wahlbezirkID: beanstandeteWahlbriefeDto.wahlbezirkID,
      waehlerverzeichnisNummer:
        beanstandeteWahlbriefeDto.waehlerverzeichnisNummer,
      beanstandeteWahlbriefe: new Map(),
    };

    Object.entries(beanstandeteWahlbriefeDto.beanstandeteWahlbriefe).forEach(
      ([wahlID, values]) => {
        const enumValues = values.map((value) => _getEnumValue(value));
        beanstandeteWahlbriefe.beanstandeteWahlbriefe.set(wahlID, enumValues);
      }
    );
    return beanstandeteWahlbriefe;
  }

  function zurueckweisungsgrundStringToEnumValue(
    value: string
  ): ZurueckweisungsgrundEnum {
    switch (value) {
      case "Zugelassen":
        return ZurueckweisungsgrundEnum.Zugelassen;
      case "Wahlschein ungültig laut Liste":
        return ZurueckweisungsgrundEnum.ScheinUngueltig;
      case "Kein Original-Wahlschein":
        return ZurueckweisungsgrundEnum.KeinOriginalSchein;
      case "Unterschrift auf Wahlschein fehlt":
        return ZurueckweisungsgrundEnum.UnterschriftFehlt;
      case "Stimmzettelumschlag fehlt":
        return ZurueckweisungsgrundEnum.UmschlagFehlt;
      case "Lose Stimmzettel":
        return ZurueckweisungsgrundEnum.LoseStimmzettel;
      case "Wahlbrief und Stimmzettelumschlag offen":
        return ZurueckweisungsgrundEnum.WahlbriefUndUmschlagOffen;
      case "Wahlscheine ungleich Stimmzettelumschläge":
        return ZurueckweisungsgrundEnum.ScheineUngleichUmschlaege;
      case "Nicht-amtlicher Stimmzettelumschlag":
        return ZurueckweisungsgrundEnum.UmschlagNichtAmtlich;
      case "Stimmzettelumschlag gefährdet Wahlgeheimnis":
        return ZurueckweisungsgrundEnum.UmschlagGefaehrdetWahlgeheimnis;
      case "Gegenstand im Stimmzettelumschlag":
        return ZurueckweisungsgrundEnum.GegenstandImUmschlag;
      case "Für diese Wahl nicht wahlberechtigt":
        return ZurueckweisungsgrundEnum.NichtWahlberechtigt;
      default:
        throw new Error("Ungültiger Zurückweisungsgrund");
    }
  }

  function zurueckweisungsgrundEnumToDisplayString(
    grund: ZurueckweisungsgrundEnum | null
  ): string {
    switch (grund) {
      case ZurueckweisungsgrundEnum.Zugelassen:
        return "Zugelassen";
      case ZurueckweisungsgrundEnum.ScheinUngueltig:
        return "Wahlschein ungültig laut Liste";
      case ZurueckweisungsgrundEnum.KeinOriginalSchein:
        return "Kein Original-Wahlschein";
      case ZurueckweisungsgrundEnum.UnterschriftFehlt:
        return "Unterschrift auf Wahlschein fehlt";
      case ZurueckweisungsgrundEnum.UmschlagFehlt:
        return "Stimmzettelumschlag fehlt";
      case ZurueckweisungsgrundEnum.LoseStimmzettel:
        return "Lose Stimmzettel";
      case ZurueckweisungsgrundEnum.WahlbriefUndUmschlagOffen:
        return "Wahlbrief und Stimmzettelumschlag offen";
      case ZurueckweisungsgrundEnum.ScheineUngleichUmschlaege:
        return "Wahlscheine ungleich Stimmzettelumschläge";
      case ZurueckweisungsgrundEnum.UmschlagNichtAmtlich:
        return "Nicht-amtlicher Stimmzettelumschlag";
      case ZurueckweisungsgrundEnum.UmschlagGefaehrdetWahlgeheimnis:
        return "Stimmzettelumschlag gefährdet Wahlgeheimnis";
      case ZurueckweisungsgrundEnum.GegenstandImUmschlag:
        return "Gegenstand im Stimmzettelumschlag";
      case ZurueckweisungsgrundEnum.NichtWahlberechtigt:
        return "Für diese Wahl nicht wahlberechtigt";
      default:
        return "";
    }
  }

  function _getEnumValue(value: string): ZurueckweisungsgrundEnum {
    switch (value) {
      case "ZUGELASSEN":
        return ZurueckweisungsgrundEnum.Zugelassen;
      case "SCHEIN_UNGUELTIG":
        return ZurueckweisungsgrundEnum.ScheinUngueltig;
      case "KEIN_ORIGINAL_SCHEIN":
        return ZurueckweisungsgrundEnum.KeinOriginalSchein;
      case "UNTERSCHRIFT_FEHLT":
        return ZurueckweisungsgrundEnum.UnterschriftFehlt;
      case "UMSCHLAG_FEHLT":
        return ZurueckweisungsgrundEnum.UmschlagFehlt;
      case "LOSE_STIMMZETTEL":
        return ZurueckweisungsgrundEnum.LoseStimmzettel;
      case "WAHLBRIEF_UND_UMSCHLAG_OFFEN":
        return ZurueckweisungsgrundEnum.WahlbriefUndUmschlagOffen;
      case "SCHEINE_UNGLEICH_UMSCHLAEGE":
        return ZurueckweisungsgrundEnum.ScheineUngleichUmschlaege;
      case "UMSCHLAG_NICHT_AMTLICH":
        return ZurueckweisungsgrundEnum.UmschlagNichtAmtlich;
      case "UMSCHLAG_GEFAEHRDET_WAHLGEHEIMNIS":
        return ZurueckweisungsgrundEnum.UmschlagGefaehrdetWahlgeheimnis;
      case "GEGENSTAND_IM_UMSCHLAG":
        return ZurueckweisungsgrundEnum.GegenstandImUmschlag;
      case "NICHT_WAHLBERECHTIGT":
        return ZurueckweisungsgrundEnum.NichtWahlberechtigt;
      default:
        throw new Error(`Ungültiger Zurückweisungsgrund: ${value}`);
    }
  }

  return {
    toModel,
    zurueckweisungsgrundStringToEnumValue,
    zurueckweisungsgrundEnumToDisplayString,
  };
}
