import type {
  BeschlussfassungDTO,
  BeschlussgrundDTO,
  KandidatDTO,
  StimmzettelOfTeamDTO,
  WahlvorschlagDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Beschlussfassung } from "@/types/dse/Beschlussfassung.ts";
import type { Beschlussgrund } from "@/types/dse/Beschlussgrund.ts";
import type { Kandidat } from "@/types/dse/Kandidat.ts";
import type { Stimmzettel } from "@/types/dse/Stimmzettel.ts";
import type { Wahlvorschlag } from "@/types/dse/Wahlvorschlag.ts";

export function useStimmzettelMapper() {
  function toModel(dto: StimmzettelOfTeamDTO): Stimmzettel {
    return {
      stimmzettelkennung: dto.stimmzettelkennung,
      wahlvorschlaege: (dto.wahlvorschlaege ?? []).map((wahlvorschlagDTO) =>
        _wahlvorschlagDtoToModel(wahlvorschlagDTO as WahlvorschlagDTO)
      ),
      invalideVotes: dto.invalideVotes,
      gueltigkeit: dto.gueltigkeit,
      beschlussvorschlag: (dto.beschlussvorschlag ?? []).map(
        (beschlussgrundDTO: BeschlussgrundDTO) =>
          _beschlussgrundDtoToModel(beschlussgrundDTO)
      ),
      beschlussfassung: dto.beschlussfassung
        ? _beschlussfassungDtoToModel(dto.beschlussfassung)
        : null,
    };
  }

  function toDTO(model: Stimmzettel): StimmzettelOfTeamDTO {
    return {
      gueltigkeit: model.gueltigkeit,
      invalideVotes: model.invalideVotes,
      stimmzettelkennung: model.stimmzettelkennung,
      wahlvorschlaege:
        model.wahlvorschlaege.length > 0
          ? model.wahlvorschlaege.map((wahlvorschlag) =>
              _wahlvorschlagModelToDto(wahlvorschlag)
            )
          : undefined,
      beschlussfassung: model.beschlussfassung
        ? _beschlussfassungModelToDto(model.beschlussfassung)
        : undefined,
      beschlussvorschlag:
        model.beschlussvorschlag.length > 0
          ? model.beschlussvorschlag.map((beschlussgrund) =>
              _beschlussgrundModelToDto(beschlussgrund)
            )
          : undefined,
    };
  }

  function _kandidatDtoToModel(dto: KandidatDTO): Kandidat {
    return {
      kandidatId: dto.id.kandidatID,
      nennung: dto.id.nennungsNummer,
      isDiscarded: dto.discarded,
      votesByVoter: _getNumberOrNullWhenUndefined(dto.votesByVoter),
      invalidVotes: _getNumberOrNullWhenUndefined(dto.invalidVotes),
      votesByWahlvorschlag: _getNumberOrNullWhenUndefined(
        dto.votesByWahlvorschlag
      ),
    };
  }

  function _kandidatModelToDto(model: Kandidat): KandidatDTO {
    return {
      id: {
        kandidatID: model.kandidatId,
        nennungsNummer: model.nennung,
      },
      discarded: model.isDiscarded,
      invalidVotes: _getNumberOrUndefinedWhenNull(model.invalidVotes),
      votesByVoter: _getNumberOrUndefinedWhenNull(model.votesByVoter),
      votesByWahlvorschlag: _getNumberOrUndefinedWhenNull(
        model.votesByWahlvorschlag
      ),
    };
  }

  function _wahlvorschlagDtoToModel(dto: WahlvorschlagDTO): Wahlvorschlag {
    return {
      wahlvorschlagID: dto.wahlvorschlagID,
      selected: dto.selected,
      kandidaten: (dto.kandidaten ?? []).map((k) => _kandidatDtoToModel(k)),
    };
  }

  function _wahlvorschlagModelToDto(model: Wahlvorschlag): WahlvorschlagDTO {
    return {
      wahlvorschlagID: model.wahlvorschlagID,
      selected: model.selected,
      kandidaten: model.kandidaten?.map((k) => _kandidatModelToDto(k)),
    };
  }

  function _beschlussgrundDtoToModel(dto: BeschlussgrundDTO): Beschlussgrund {
    return {
      text: dto.text,
    };
  }

  function _beschlussfassungDtoToModel(
    dto: BeschlussfassungDTO
  ): Beschlussfassung {
    return {
      contra: dto.contra,
      pro: dto.pro,
      text: dto.text,
    };
  }

  function _beschlussgrundModelToDto(model: Beschlussgrund): BeschlussgrundDTO {
    return {
      text: model.text,
    };
  }

  function _beschlussfassungModelToDto(
    model: Beschlussfassung
  ): BeschlussfassungDTO {
    return {
      pro: model.pro,
      contra: model.contra,
      text: model.text,
    };
  }

  function _getNumberOrNullWhenUndefined(
    number: number | undefined
  ): number | null {
    return number === undefined ? null : number;
  }

  function _getNumberOrUndefinedWhenNull(
    number: number | null
  ): number | undefined {
    return number === null ? undefined : number;
  }

  return {
    toModel,
    toDTO,
  };
}
