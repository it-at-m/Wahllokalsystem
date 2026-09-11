import type { Stimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";
import type { WahlvorschlagWithKandidatenErgebnissen } from "@/types/ergebnismeldung/common/WahlvorschlagWithKandidatenErgebnissen.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";
import type { Ref } from "vue";

import { computed } from "vue";

export function useStimmzettelGueltigeKandidatenstimmenAnzeigenCardUtils(
  stimmzettelListe: Ref<Stimmzettel[]>,
  wahlvorschlaege: Ref<Wahlvorschlag[]>
) {
  const wahlvorschlaegeWithKandidatenErgebnissen = computed(() => {
    return [...wahlvorschlaege.value].map((wahlvorschlag) =>
      _stimmzettelListeToWahlvorschlagWithKandidatenErgebnissen(wahlvorschlag)
    );
  });

  function _stimmzettelListeToWahlvorschlagWithKandidatenErgebnissen(
    wahlvorschlag: Wahlvorschlag
  ) {
    const wahlvorschlagWithKandidatenErgebnissen = _initResult(wahlvorschlag);

    if (wahlvorschlag.kandidaten) {
      wahlvorschlagWithKandidatenErgebnissen.kandidatenErgebnisse = [
        ...wahlvorschlag.kandidaten,
      ].map((kandidat) => {
        const ergebnisForKandidat = {
          wahlvorschlagID: wahlvorschlag.identifikator,
          kandidatID: kandidat.identifikator,
          wahlvorschlagsOrdnungszahl: wahlvorschlag.ordnungszahl,
          ergebnis: _getVotesForKandidatOfWahlvorschlag(
            wahlvorschlag.identifikator,
            kandidat.identifikator
          ),
          numIndex: null,
        };
        return {
          ergebnis: ergebnisForKandidat,
          kandidat,
        };
      });
    }

    return wahlvorschlagWithKandidatenErgebnissen;
  }

  function _getVotesForKandidatOfWahlvorschlag(
    wahlvorschlagId: string,
    kandidatId: string
  ) {
    return stimmzettelListe.value
      .map((stimmzettel) => {
        const wahlvorschlag = stimmzettel.wahlvorschlaege.find(
          (ws) => ws.wahlvorschlagID === wahlvorschlagId
        );
        if (!wahlvorschlag) {
          return 0;
        }

        const kandidat = wahlvorschlag.kandidaten.find(
          (k) => k.kandidatId === kandidatId
        );
        if (!kandidat) {
          return 0;
        }
        return (
          (kandidat.votesByVoter ?? 0) + (kandidat.votesByWahlvorschlag ?? 0)
        );
      })
      .reduce((sum, value) => sum + value, 0);
  }

  function _initResult(
    wahlvorschlag: Wahlvorschlag
  ): WahlvorschlagWithKandidatenErgebnissen {
    return {
      identifikator: wahlvorschlag.identifikator,
      kandidatenErgebnisse: [],
      kurzname: wahlvorschlag.kurzname,
      ordnungszahl: wahlvorschlag.ordnungszahl,
    };
  }

  return {
    wahlvorschlaegeWithKandidatenErgebnissen,
  };
}
