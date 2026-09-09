import type { Stimmzettel as PersistedStimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";
import type { Kandidat as DSEKandidat } from "@/types/dse/stimmzettelerfassung/Kandidat.ts";
import type { Stimmzettel } from "@/types/dse/stimmzettelerfassung/Stimmzettel.ts";
import type { Wahlvorschlag as DSEWahlvorschlag } from "@/types/dse/stimmzettelerfassung/Wahlvorschlag.ts";
import type { Kandidat } from "@/types/wahlvorschlaege/Kandidat.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { useTextFormatter } from "@/composables/common/textFormatter.ts";
import { WAHLVORSCHLAG_NUMBER_MULTIPLIER_FOR_ORDNUNGSZAHL } from "@/constants.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";

function _useStimmzettelUtils() {
  function createStimmzettelWithWahlvorschlaege(
    wahlvorschlaege: Wahlvorschlag[]
  ): Stimmzettel {
    const initWahlvorschlaege = wahlvorschlaege.map(_toDSEWahlvorschlag);
    return {
      wahlvorstandBeschlussvorschlag: [],
      systemBeschlussvorschlag: [],
      beschlussfassung: null,
      gueltigkeit: StimmzettelGueltigkeitEnum.Valid,
      invalideVotes: 0,
      wahlvorschlaege: initWahlvorschlaege,
    };
  }

  function getEmptyStimmzettelWithStimmzettelkennung(
    stimmzettelkennung: number
  ): PersistedStimmzettel {
    return {
      stimmzettelkennung: stimmzettelkennung,
      teamID: "",
      gueltigkeit: StimmzettelGueltigkeitEnum.Valid,
      invalideVotes: 0,
      beschlussfassung: null,
      wahlvorstandBeschlussvorschlag: [],
      systemBeschlussvorschlag: [],
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
          WAHLVORSCHLAG_NUMBER_MULTIPLIER_FOR_ORDNUNGSZAHL *
            wahlvorschlagOfKandiat.ordnungszahl +
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
    return (
      stimmzettel.systemBeschlussvorschlag.length > 0 ||
      stimmzettel.wahlvorstandBeschlussvorschlag.length > 0
    );
  }

  function getVormerkungsgrund(stimmzettel: PersistedStimmzettel): string {
    const { mapSystemBeschlussgrundText } = useTextFormatter();
    const wahlvorstandVorschlaege =
      stimmzettel.wahlvorstandBeschlussvorschlag.map(
        (vorschlag) => vorschlag.text
      );
    const systemVorschlaege = stimmzettel.systemBeschlussvorschlag.map(
      (vorschlag) => mapSystemBeschlussgrundText(vorschlag.reason)
    );
    return [...systemVorschlaege, ...wahlvorstandVorschlaege].join(", ");
  }

  return {
    createStimmzettelWithWahlvorschlaege,
    getEmptyStimmzettelWithStimmzettelkennung,
    isVorgemerktFuerBeschluss,
    getVormerkungsgrund,
  };
}

/**
 * @deprecated TODO is an tools composable. Does not serve any high level function
 * maybe split into separate tools for different types
 */
export const useStimmzettelUtils = _useStimmzettelUtils;
export const useStimmzettelTools = _useStimmzettelUtils;
