import type { AsyncProgressDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { AWerteInitProgress } from "@/types/aWerte/AWerteInitProgress.ts";

export function useAWerteMapper() {
  function asyncProgressDtoToAWerteInitProgress(
    dto: AsyncProgressDTO
  ): AWerteInitProgress {
    return {
      finished: dto.aWerteFinished,
      next: dto.aWerteNext,
      total: dto.aWerteTotal,
      active: dto.aWerteLoadingActive,
      lastFinishTime: dto.lastFinishTime,
      lastStartTime: dto.lastStartTime,
    };
  }

  return {
    asyncProgressDtoToAWerteInitProgress,
  };
}
