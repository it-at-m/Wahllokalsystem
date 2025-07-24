import type { ZurueckweisungsgrundEnum } from "@/types/briefwahl/ZurueckweisungsgrundEnum.ts";

type WahlIDString = string;

export interface BeanstandeteWahlbriefe {
  wahlbezirkID: string;
  waehlerverzeichnisNummer: number;
  beanstandeteWahlbriefe: Map<WahlIDString, ZurueckweisungsgrundEnum[]>;
}
