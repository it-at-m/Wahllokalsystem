import type { AsyncProgressDTO } from "@/api/wls-clients/generated-basisdaten-api";
import type { BasisdatenInitProgress } from "@/types/basisdaten/BasisdatenInitProgress.ts";

export function useBasisdatenMapper() {
  function mapAsyncProgressDtoToBasisdatenInitProgress(
    dto: AsyncProgressDTO
  ): BasisdatenInitProgress {
    return {
      wahlNummer: dto.wahlNummer,
      forWahltag: dto.forWahltag,
      lastStartTime: dto.lastStartTime,
      lastFinishTime: dto.lastFinishTime,
      wahlvorschlaege: {
        active: dto.wahlvorschlaegeLoadingActive,
        total: dto.wahlvorschlaegeTotal,
        next: dto.wahlvorschlaegeNext,
        finished: dto.wahlvorschlageFinished,
      },
      referendumvorlagen: {
        active: dto.referendumLoadingActive,
        next: dto.referendumVorlagenNext,
        finished: dto.referendumVorlagenFinished,
        total: dto.referendumVorlagenTotal,
      },
    };
  }

  return { mapAsyncProgressDtoToBasisdatenInitProgress };
}
