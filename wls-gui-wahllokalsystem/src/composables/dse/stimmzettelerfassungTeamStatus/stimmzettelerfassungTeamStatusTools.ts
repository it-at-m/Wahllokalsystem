import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEnum.ts";

export function isTeamStatusBeingWritten(
  statusBeingWritten: StimmzettelerfassungTeamStatusEnum | null,
  status: StimmzettelerfassungTeamStatusEnum,
  exactMatch = true
) {
  return exactMatch
    ? statusBeingWritten === status
    : statusBeingWritten !== null && statusBeingWritten !== status;
}
