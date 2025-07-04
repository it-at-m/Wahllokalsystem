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
      ([key, values]) => {
        const enumValues = values.map((value) => {
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
        });

        beanstandeteWahlbriefe.beanstandeteWahlbriefe.set(key, enumValues);
      }
    );

    return beanstandeteWahlbriefe;
  }

  return { toModel };
}
