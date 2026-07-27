import type {
  StimmzettelKandidatDTO,
  StimmzettelOfTeamDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Kandidat } from "@/types/dse/Kandidat.ts";
import type { Stimmzettel } from "@/types/dse/Stimmzettel.ts";

export function useStimmzettelMapper() {
  function toModel(dto: StimmzettelOfTeamDTO): Stimmzettel {
    const mappedKandidaten =
      dto.kandidaten?.map((kandidat) => _kandidatDtoToModel(kandidat)) ?? [];
    return {
      stimmzettelkennung: dto.stimmzettelkennung,
      kandidaten: mappedKandidaten,
      selectedWahlvorschlaegeOrdnungszahlen:
        dto.selectedWahlvorschlaegeOrdnungszahlen ?? [],
    };
  }

  function _kandidatDtoToModel(dto: StimmzettelKandidatDTO): Kandidat {
    return {
      kandidatId: dto.kandidatId,
      isDiscarded: dto.isDiscarded,
      votesByVoter: dto.votesByVoter,
    };
  }

  return {
    toModel,
  };
}
