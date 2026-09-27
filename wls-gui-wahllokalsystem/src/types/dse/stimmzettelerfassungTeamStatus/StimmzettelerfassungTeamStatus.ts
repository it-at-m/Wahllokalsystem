import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEnum.ts";

export interface StimmzettelerfassungTeamStatus {
  status: StimmzettelerfassungTeamStatusEnum;
}

export function useStimmzettelerfassungTeamStatusTools() {
  function createAbgeschlossen() {
    return _createWithStatus(StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN);
  }

  function createRegistriert() {
    return _createWithStatus(StimmzettelerfassungTeamStatusEnum.REGISTRIERT);
  }

  function createInBearbeitung() {
    return _createWithStatus(StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG);
  }

  function createUnterbrochen() {
    return _createWithStatus(StimmzettelerfassungTeamStatusEnum.UNTERBROCHEN);
  }

  function _createWithStatus(
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
  };
}
