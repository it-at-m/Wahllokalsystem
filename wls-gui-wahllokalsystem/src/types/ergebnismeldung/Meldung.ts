import type { MeldungValidierungsstatusEnum } from "@/types/ergebnismeldung/MeldungValidierungsstatusEnum.ts";

export interface Meldung {
  validierungsstatus: MeldungValidierungsstatusEnum;
  gedruckt: boolean;
  uebermittelt?: boolean;
  sendeuhrzeit?: string;
}
