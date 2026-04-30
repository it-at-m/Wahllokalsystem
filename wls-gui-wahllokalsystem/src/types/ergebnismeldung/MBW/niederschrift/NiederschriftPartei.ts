import type { NiederschriftParteiMaxCol } from "./NiederschriftParteiMaxCol";

export interface NiederschriftPartei {
  ordnungszahl: number | string;
  kurzname: string;
  direktKandMit00: { laufendeNr: number | string } | null;
  maxcols: NiederschriftParteiMaxCol[];
  // eslint-disable-next-line
  _tabledata: any[]; // table rows for candidate data
}
