import type { ZurueckweisungsgrundEnum } from "@/types/briefwahl/ZurueckweisungsgrundEnum.ts";

export interface BeanstandeteWahlbriefe {
  wahlbezirkID: string;
  waehlerverzeichnisNummer: number;
  beanstandeteWahlbriefe: Map<string, ZurueckweisungsgrundEnum[]>;
}