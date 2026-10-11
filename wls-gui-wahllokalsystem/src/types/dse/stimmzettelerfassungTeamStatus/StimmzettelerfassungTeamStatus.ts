import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEnum.ts";

export interface StimmzettelerfassungTeamStatus {
  status: StimmzettelerfassungTeamStatusEnum;
}

export function useStimmzettelerfassungTeamStatusTools() {
  function createAbgeschlossen() {
    return createWithStatus(StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN);
  }

  function createRegistriert() {
    return createWithStatus(StimmzettelerfassungTeamStatusEnum.REGISTRIERT);
  }

  function createInBearbeitung() {
    return createWithStatus(StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG);
  }

  function createUnterbrochen() {
    return createWithStatus(StimmzettelerfassungTeamStatusEnum.UNTERBROCHEN);
  }

  function createWithStatus(
    status: StimmzettelerfassungTeamStatusEnum
  ): StimmzettelerfassungTeamStatus {
    return {
      status,
    };
  }

  return {
    createAbgeschlossen,
    createInBearbeitung,
    createRegistriert,
    createUnterbrochen,
    createWithStatus,
  };
}
