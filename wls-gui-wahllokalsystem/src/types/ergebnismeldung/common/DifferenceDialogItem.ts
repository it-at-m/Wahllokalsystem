import type { DifferenceBegruendung } from "@/types/ergebnismeldung/common/DifferenceBegruendung.ts";

export interface DifferenceDialogItem {
  isVisible: boolean;
  differenceBegruendung: DifferenceBegruendung;
}
