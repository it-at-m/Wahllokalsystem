import type { KandidatAnzeige } from "@/types/dse/KandidatAnzeige.ts";
import type { WahlvorschlagAnzeige } from "@/types/dse/WahlvorschlagAnzeige.ts";
import type { Kandidat } from "@/types/wahlvorschlaege/Kandidat.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

export function useWahlvorschlagMapper() {
  function toWahlvorschlagAnzeigen(
    wahlvorschlag: Wahlvorschlag
  ): WahlvorschlagAnzeige {
    return {
      identifikator: wahlvorschlag.identifikator,
      ordnungszahl: wahlvorschlag.ordnungszahl,
      kurzname: wahlvorschlag.kurzname,
      erhaeltStimmen: wahlvorschlag.erhaeltStimmen,
      gueltigeStimmen: 0,
      ungueltigeStimmen: 0,
      kandidaten: wahlvorschlag.kandidaten
        ?.map((kandidat) => toKandidatAnzeigen(kandidat))
        .flat(),
    };
  }

  function toKandidatAnzeigen(kandidat: Kandidat): KandidatAnzeige[] {
    const result: KandidatAnzeige[] = [];
    for (let nennung = 1; nennung <= kandidat.anzahlNennungen; nennung++) {
      result.push({
        identifikator: kandidat.identifikator,
        name: kandidat.name,
        listenposition: kandidat.listenposition,
        nennungsposition: 0, //TODO
        durchgestrichen: false,
        gesamtStimmen: 0,
        gueltigeStimmen: 0,
        ungueltigeStimmen: 0,
        restStimmen: 0,
      });
    }
    return result;
  }

  return {
    toWahlvorschlagAnzeigen,
  };
}
