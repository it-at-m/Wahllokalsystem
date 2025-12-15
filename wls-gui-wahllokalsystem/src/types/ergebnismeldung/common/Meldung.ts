import type { MeldungValidierungsstatusEnum } from "@/types/ergebnismeldung/common/MeldungValidierungsstatusEnum.ts";

export interface Meldung {
  validierungsstatus: MeldungValidierungsstatusEnum;
  gedruckt: boolean;
  uebermittelt?: boolean;
  sendeuhrzeit?: string;
}
