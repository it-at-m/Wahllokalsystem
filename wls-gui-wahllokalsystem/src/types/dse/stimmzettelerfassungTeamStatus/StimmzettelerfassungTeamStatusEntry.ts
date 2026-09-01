import type { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEnum.ts";

export interface StimmzettelerfassungTeamStatusEntry {
  teamID: string;
  status: StimmzettelerfassungTeamStatusEnum;
}
