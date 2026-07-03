import type {
  KandidatDTO,
  WahlvorschlaegeDTO,
  WahlvorschlagDTO,
} from "@/api/wls-clients/generated-basisdaten-api";
import type { Kandidat } from "@/types/wahlvorschlaege/Kandidat.ts";
import type { Wahlvorschlaege } from "@/types/wahlvorschlaege/Wahlvorschlaege.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

export function useWahlvorschlaegeMapper() {
  function toModel(dto: WahlvorschlaegeDTO): Wahlvorschlaege {
    return {
      wahlID: dto.wahlID,
      wahlbezirkID: dto.wahlbezirkID,
      stimmzettelgebietID: dto.stimmzettelgebietID,
      wahlvorschlaege: _dtoWahlvorschlaegeToModel(dto.wahlvorschlaege),
    };
  }

  function _dtoWahlvorschlaegeToModel(
    dtoWahlvorschlaegeArray: WahlvorschlagDTO[]
  ) {
    const modelWahlvorschlaege = [];

    for (const wahlvorschlagDto of dtoWahlvorschlaegeArray) {
      const wahlvorschlag: Wahlvorschlag = {
        identifikator: wahlvorschlagDto.identifikator,
        ordnungszahl: wahlvorschlagDto.ordnungszahl,
        kurzname: wahlvorschlagDto.kurzname,
        erhaeltStimmen: wahlvorschlagDto.erhaeltStimmen,
      };

      if (wahlvorschlagDto.kandidaten) {
        wahlvorschlag.kandidaten = _dtoKandidatenToModel(
          wahlvorschlagDto.kandidaten
        );
      }

      modelWahlvorschlaege.push(wahlvorschlag);
    }
    return modelWahlvorschlaege;
  }

  function _dtoKandidatenToModel(dtoKandidatenArray: KandidatDTO[]) {
    const modelKandidaten: Kandidat[] = [];

    for (const kandidatDto of dtoKandidatenArray) {
      modelKandidaten.push({
        identifikator: kandidatDto.identifikator,
        name: kandidatDto.name,
        listenposition: kandidatDto.listenposition,
        direktkandidat: kandidatDto.direktkandidat,
        tabellenSpalteInNiederschrift:
          kandidatDto.tabellenSpalteInNiederschrift,
        einzelbewerber: kandidatDto.einzelbewerber,
        anzahlNennungen: kandidatDto.anzahlNennungen,
      });
    }

    return modelKandidaten;
  }

  return { toModel };
}
