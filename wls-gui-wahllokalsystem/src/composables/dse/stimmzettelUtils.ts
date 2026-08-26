import type { Kandidat as DSEKandidat } from "@/types/dse/Kandidat.ts";
import type { Stimmzettel as PersistedStimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";
import type { Stimmzettel } from "@/types/dse/Stimmzettel.ts";
import type { Wahlvorschlag as DSEWahlvorschlag } from "@/types/dse/Wahlvorschlag.ts";
import type { Kandidat } from "@/types/wahlvorschlaege/Kandidat.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { MULTIPLIER_FOR_ORDNUNGSZAHL } from "@/constants.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/StimmzettelGueltigkeitEnum.ts";

export function useStimmzettelUtils() {
  function createStimmzettelWithWahlvorschlaege(
    wahlvorschlaege: Wahlvorschlag[]
  ): Stimmzettel {
    const initWahlvorschlaege = wahlvorschlaege.map(_toDSEWahlvorschlag);
    return {
      stimmzettelkennung: 0,
      beschlussvorschlag: [],
      beschlussfassung: null,
      gueltigkeit: null,
      invalideVotes: 0,
      wahlvorschlaege: initWahlvorschlaege,
    };
  }

  function getEmptyStimmzettelWithStimmzettelkennung(
    stimmzettelkennung: number
  ): PersistedStimmzettel {
    return {
      stimmzettelkennung: stimmzettelkennung,
      gueltigkeit: StimmzettelGueltigkeitEnum.Valid,
      invalideVotes: 0,
      beschlussfassung: null,
      beschlussvorschlag: [],
      wahlvorschlaege: [],
    };
  }

  function _toDSEWahlvorschlag(wahlvorschlag: Wahlvorschlag): DSEWahlvorschlag {
    const dseWahlvorschlag: DSEWahlvorschlag = {
      wahlvorschlagID: wahlvorschlag.identifikator,
      ordnungszahl: wahlvorschlag.ordnungszahl,
      kandidaten: [],
      selected: false,
      ungueltigeStimmen: 0,
      gueltigeStimmen: 0,
      erhaeltStimmen: wahlvorschlag.erhaeltStimmen,
      kurzname: wahlvorschlag.kurzname,
    };

    dseWahlvorschlag.kandidaten =
      wahlvorschlag.kandidaten
        ?.map((kandidat) => _toDSEKandidat(kandidat, dseWahlvorschlag))
        .flat() ?? [];
    return dseWahlvorschlag;
  }

  function _toDSEKandidat(
    kandidat: Kandidat,
    wahlvorschlagOfKandiat: DSEWahlvorschlag
  ): DSEKandidat[] {
    const result: DSEKandidat[] = [];
    for (let nennung = 1; nennung <= kandidat.anzahlNennungen; nennung++) {
      result.push({
        kandidatId: kandidat.identifikator,
        nennung: nennung,
        listenposition: kandidat.listenposition,
        ordnungszahl:
          MULTIPLIER_FOR_ORDNUNGSZAHL * wahlvorschlagOfKandiat.ordnungszahl +
          kandidat.listenposition,
        einzelstimmen: null,
        durchgestrichen: false,
        reststimmen: null,
        ungueltigeStimmen: null,
        name: kandidat.name,
        owningWahlvorschlag: wahlvorschlagOfKandiat,
      });
    }

    return result;
  }

  function isVorgemerktFuerBeschluss(
    stimmzettel: PersistedStimmzettel
  ): boolean {
    return stimmzettel.beschlussvorschlag.length > 0;
  }

  function getVormerkungsgrund(stimmzettel: PersistedStimmzettel): string {
    if (!isVorgemerktFuerBeschluss(stimmzettel)) {
      return "";
    }
    return stimmzettel.beschlussvorschlag.map((grund) => grund.text).join(", ");
  }

  return {
    createStimmzettelWithWahlvorschlaege,
    getEmptyStimmzettelWithStimmzettelkennung,
    isVorgemerktFuerBeschluss,
    getVormerkungsgrund,
  };
}
