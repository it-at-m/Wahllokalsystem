import type { DseKandidat } from "@/types/dse/stimmzettelerfassung/DseKandidat.ts";
import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";
import type { DseStimmzettel } from "@/types/dse/stimmzettelerfassung/DseStimmzettel.ts";
import type { DseWahlvorschlag } from "@/types/dse/stimmzettelerfassung/DseWahlvorschlag.ts";
import type { Kandidat } from "@/types/wahlvorschlaege/Kandidat.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { useBeschlussgrundTools } from "@/composables/dse/beschlussfassung/beschlussgrundTools.ts";
import { useSystemBeschlussgrundReasonEnumTools } from "@/composables/dse/stimmzettelerfassung/systemBeschlussgrundReasonEnumTools.ts";
import { useWahlvorschlagTools } from "@/composables/dse/stimmzettelerfassung/wahlvorschlagTools.ts";
import { WAHLVORSCHLAG_NUMBER_MULTIPLIER_FOR_ORDNUNGSZAHL } from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";

const { sortWahlvorstandBeschlussgruende, sortSystemBeschlussgruende } =
  useBeschlussgrundTools();
const { sortAndDeepCloneWahlvorschlaege } = useWahlvorschlagTools();

export function useStimmzettelTools() {
  function createStimmzettelWithWahlvorschlaege(
    wahlvorschlaege: Wahlvorschlag[]
  ): DseStimmzettel {
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
      teamID: useUserStore().currentUserTeamName,
      gueltigkeit: StimmzettelGueltigkeitEnum.Valid,
      invalideVotes: 0,
      beschlussfassung: null,
      wahlvorstandBeschlussvorschlag: [],
      systemBeschlussvorschlag: [],
      wahlvorschlaege: [],
    };
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
    const { mapSystemBeschlussgrundReasonEnumToText } =
      useSystemBeschlussgrundReasonEnumTools();
    const wahlvorstandVorschlaege =
      stimmzettel.wahlvorstandBeschlussvorschlag.map(
        (vorschlag) => vorschlag.text
      );
    const systemVorschlaege = stimmzettel.systemBeschlussvorschlag.map(
      (vorschlag) => mapSystemBeschlussgrundReasonEnumToText(vorschlag.reason)
    );
    return [...systemVorschlaege, ...wahlvorstandVorschlaege].join(", ");
  }

  function normalizePersistedStimmzettel(
    stimmzettel: PersistedStimmzettel
  ): PersistedStimmzettel {
    const systemBeschluss = sortSystemBeschlussgruende(
      stimmzettel.systemBeschlussvorschlag ?? []
    );
    const wvBeschluss = sortWahlvorstandBeschlussgruende(
      stimmzettel.wahlvorstandBeschlussvorschlag ?? []
    );
    const wvSorted = sortAndDeepCloneWahlvorschlaege(
      stimmzettel.wahlvorschlaege ?? []
    );

    return {
      stimmzettelkennung: stimmzettel.stimmzettelkennung,
      teamID: stimmzettel.teamID,
      wahlvorschlaege: wvSorted,
      invalideVotes: stimmzettel.invalideVotes ?? 0,
      gueltigkeit: stimmzettel.gueltigkeit,
      wahlvorstandBeschlussvorschlag: wvBeschluss,
      systemBeschlussvorschlag: systemBeschluss,
      beschlussfassung: stimmzettel.beschlussfassung
        ? { ...stimmzettel.beschlussfassung }
        : null,
    };
  }

  function resetDseStimmzettel(stimmzettel: DseStimmzettel): DseStimmzettel {
    stimmzettel.wahlvorschlaege.map((wahlvorschlag) => {
      wahlvorschlag.selected = false;
      wahlvorschlag.kandidaten.map((kandidat) => {
        kandidat.einzelstimmen = null;
        kandidat.ungueltigeStimmen = null;
        kandidat.reststimmen = null;
        kandidat.durchgestrichen = false;
      });
    });
    stimmzettel.gueltigkeit = StimmzettelGueltigkeitEnum.Valid;
    stimmzettel.wahlvorstandBeschlussvorschlag = [];
    stimmzettel.systemBeschlussvorschlag = [];
    stimmzettel.beschlussfassung = null;
    stimmzettel.invalideVotes = 0;

    return stimmzettel;
  }

  function isSamePersistedStimmzettel(
    stimmzettel1: PersistedStimmzettel,
    stimmzettel2: PersistedStimmzettel
  ): boolean {
    return (
      stimmzettel1.stimmzettelkennung === stimmzettel2.stimmzettelkennung &&
      stimmzettel1.teamID == stimmzettel2.teamID
    );
  }

  function _toDSEWahlvorschlag(wahlvorschlag: Wahlvorschlag): DseWahlvorschlag {
    const dseWahlvorschlag: DseWahlvorschlag = {
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
    wahlvorschlagOfKandiat: DseWahlvorschlag
  ): DseKandidat[] {
    const result: DseKandidat[] = [];
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

  return {
    createStimmzettelWithWahlvorschlaege,
    getEmptyStimmzettelWithStimmzettelkennung,
    isVorgemerktFuerBeschluss,
    getVormerkungsgrund,
    normalizePersistedStimmzettel,
    resetDseStimmzettel,
    isSamePersistedStimmzettel,
  };
}
