import type { Progress } from "@/types/common/Progress.ts";

export interface AWerteInitProgress extends Progress {
  lastStartTime?: string;
  lastFinishTime?: string;
}
