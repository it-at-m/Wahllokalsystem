import type { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/teamStatus/StimmzettelerfassungTeamStatusEnum.ts";

export interface StimmzettelerfassungTeamStatusEntry {
  teamID: string;
  status: StimmzettelerfassungTeamStatusEnum;
}
