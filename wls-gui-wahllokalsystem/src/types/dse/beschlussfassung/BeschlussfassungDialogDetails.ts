import type { BeschlussgrundOption } from "@/types/dse/beschlussfassung/BeschlussgrundOption.ts";

export interface BeschlussfassungDialogDetails {
  isGueltig: boolean | null;
  beschlussgruende: BeschlussgrundOption[];
  andererGrund: string;
  andererGrundChecked: boolean;
  beschlussText: string;
}
