import type {
  StimmzettelKandidatDTO,
  StimmzettelOfTeamDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { StimmzettelKandidatSnapshot } from "@/types/experimental/StimmzettelKandidatSnapshot.ts";
import type { StimmzettelSnapshot } from "@/types/experimental/StimmzettelSnapshot.ts";

export function useStimmzettelMapper() {
  function toStimmzettelSnapshot(
    stimmzettel: StimmzettelOfTeamDTO
  ): StimmzettelSnapshot {
    return {
      selectedWahlvorschlaegeOrdnungszahlen:
        stimmzettel.selectedWahlvorschlaegeOrdnungszahlen ?? [],
      kandidatenSnapshot:
        stimmzettel?.kandidaten?.map(_toStimmzettelKandidatSnapshot) ?? [],
    };
  }

  function toWaehlerstimmzettelDTO(
    stimzettelNummer: number,
    stimmzettel: StimmzettelSnapshot
  ): StimmzettelOfTeamDTO {
    return {
      selectedWahlvorschlaegeOrdnungszahlen:
        stimmzettel.selectedWahlvorschlaegeOrdnungszahlen,
      kandidaten: stimmzettel.kandidatenSnapshot.map(_toStimmzettelKandidatDTO),
      stimmzettelkennung: stimzettelNummer,
    };
  }

  function _toStimmzettelKandidatSnapshot(
    stimmzettelKandidatDTO: StimmzettelKandidatDTO
  ): StimmzettelKandidatSnapshot {
    return {
      kandidatId: stimmzettelKandidatDTO.kandidatId,
      isDiscarded: stimmzettelKandidatDTO.isDiscarded,
      votesByVoter: stimmzettelKandidatDTO.votesByVoter,
    };
  }

  function _toStimmzettelKandidatDTO(
    stimmzettelKandidatSnapshot: StimmzettelKandidatSnapshot
  ): StimmzettelKandidatDTO {
    return {
      kandidatId: stimmzettelKandidatSnapshot.kandidatId,
      isDiscarded: stimmzettelKandidatSnapshot.isDiscarded,
      votesByVoter: stimmzettelKandidatSnapshot.votesByVoter,
    };
  }

  return {
    toStimmzettelSnapshot,
    toWaehlerstimmzettelDTO,
  };
}
