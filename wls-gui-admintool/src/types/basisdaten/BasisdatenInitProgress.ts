import type { Progress } from "@/types/common/Progress.ts";

export interface BasisdatenInitProgress {
  forWahltag?: string;
  wahlNummer?: string;
  lastStartTime?: string;
  lastFinishTime?: string;
  wahlvorschlaege: Progress;
  referendumvorlagen: Progress;
}
