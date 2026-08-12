import type { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/StimmzettelerfassungTeamStatusEnum.ts";

export interface StimmzettelerfassungTeamStatusEntry {
  teamID: string;
  status: StimmzettelerfassungTeamStatusEnum;
}
