export interface KandidatAnzeige {
  identifikator: string;
  name: string;
  listenposition: number;
  nennungsposition: number;
  durchgestrichen: boolean;
  gesamtStimmen: number;
  gueltigeStimmen: number;
  ungueltigeStimmen: number;
  restStimmen: number;
}
