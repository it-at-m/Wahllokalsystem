import { ErfassungTeamStatusEnum } from "@/types/dse/ErfassungTeamStatusEnum";
import type { ErfassungTeamStatus } from "@/types/dse/ErfassungTeamStatus";

export function useErfassungTeamStatusMapper() {

  const toErfassungTeamStatus = (statusString: string | null): ErfassungTeamStatus | null => {
    const validValues = Object.values(ErfassungTeamStatusEnum);

    // Prüfen, ob der String im Enum existiert
    if (statusString && validValues.includes(statusString as any)) {
      return { status: statusString as typeof validValues[number] } as ErfassungTeamStatus;
    }
    return null;
  }

  return {
    toErfassungTeamStatus
  };
}
