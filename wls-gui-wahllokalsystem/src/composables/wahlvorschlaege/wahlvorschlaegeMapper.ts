import type {
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
    dtoWahlvorschlaegeSet: Set<WahlvorschlagDTO>
  ) {
    const modelWahlvorschlaege = new Set<Wahlvorschlag>();

    for (const wahlvorschlagDto of dtoWahlvorschlaegeSet) {
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

      modelWahlvorschlaege.add(wahlvorschlag);
    }
    return modelWahlvorschlaege;
  }

  function _dtoKandidatenToModel(dtoKandidatenSet: Set<Kandidat>) {
    const modelKandidaten = new Set<Kandidat>();

    for (const kandidatDto of dtoKandidatenSet) {
      modelKandidaten.add({
        identifikator: kandidatDto.identifikator,
        name: kandidatDto.name,
        listenposition: kandidatDto.listenposition,
        direktkandidat: kandidatDto.direktkandidat,
        tabellenSpalteInNiederschrift:
          kandidatDto.tabellenSpalteInNiederschrift,
        einzelbewerber: kandidatDto.einzelbewerber,
      });
    }

    return modelKandidaten;
  }

  return { toModel };
}
