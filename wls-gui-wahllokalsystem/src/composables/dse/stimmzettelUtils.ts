import type { Kandidat as DSEKandidat } from "@/types/dse/Kandidat.ts";
import type { Stimmzettel as PersistedStimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";
import type { Stimmzettel } from "@/types/dse/Stimmzettel.ts";
import type { Wahlvorschlag as DSEWahlvorschlag } from "@/types/dse/Wahlvorschlag.ts";
import type { Kandidat } from "@/types/wahlvorschlaege/Kandidat.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

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
    const dseKandidaten: DSEKandidat[] =
      wahlvorschlag.kandidaten?.map(_toDSEKandidat).flat() ?? [];
    return {
      wahlvorschlagID: wahlvorschlag.identifikator,
      ordnungszahl: wahlvorschlag.ordnungszahl,
      kandidaten: dseKandidaten,
      selected: false,
    };
  }

  function _toDSEKandidat(kandidat: Kandidat): DSEKandidat[] {
    const result: DSEKandidat[] = [];
    for (let nennung = 1; nennung <= kandidat.anzahlNennungen; nennung++) {
      result.push({
        kandidatId: kandidat.identifikator,
        nennung: nennung,
        listenposition: kandidat.listenposition,
        votesByVoter: null,
        isDiscarded: false,
        votesByWahlvorschlag: null,
        invalidVotes: null,
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
