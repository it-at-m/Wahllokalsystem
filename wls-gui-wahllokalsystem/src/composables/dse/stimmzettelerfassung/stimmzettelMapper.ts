import type {
  BeschlussfassungDTO,
  KandidatDTO,
  StimmzettelOfTeamDTO,
  SystemBeschlussgrundDTO,
  WahlvorschlagDTO,
  WahlvorstandBeschlussgrundDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { SystemBeschlussgrund } from "@/types/dse/beschlussfassung/SystemBeschlussgrund.ts";
import type { WahlvorstandBeschlussgrund } from "@/types/dse/beschlussfassung/WahlvorstandBeschlussgrund.ts";
import type { Beschlussfassung } from "@/types/dse/persistedStimmzettel/Beschlussfassung.ts";
import type { Kandidat } from "@/types/dse/persistedStimmzettel/Kandidat.ts";
import type { Stimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";
import type { Wahlvorschlag } from "@/types/dse/persistedStimmzettel/Wahlvorschlag.ts";
import type { Stimmzettel as ManageableStimmzettel } from "@/types/dse/stimmzettelerfassung/Stimmzettel.ts";

import { useKandidatTools } from "@/composables/dse/stimmzettelerfassung/KandidatTools.ts";

const { hasAnyKennzeichen } = useKandidatTools();

export function useStimmzettelMapper() {
  function toModel(dto: StimmzettelOfTeamDTO, teamID: string): Stimmzettel {
    const wahlvorstandBeschlussgruende = (
      dto.wahlvorstandBeschlussvorschlag ?? []
    ).map((beschlussgrundDTO: WahlvorstandBeschlussgrundDTO) =>
      _wahlvorstandBeschlussgrundDtoToModel(beschlussgrundDTO)
    );
    const systemBeschlussgruende = (dto.systemBeschlussvorschlag ?? []).map(
      (systemBeschlussgrundDTO: SystemBeschlussgrundDTO) =>
        _systemBeschlussgrundDtoToModel(systemBeschlussgrundDTO)
    );
    return {
      stimmzettelkennung: dto.stimmzettelkennung,
      teamID: teamID,
      wahlvorschlaege: (dto.wahlvorschlaege ?? []).map((wahlvorschlagDTO) =>
        _wahlvorschlagDtoToModel(wahlvorschlagDTO as WahlvorschlagDTO)
      ),
      invalideVotes: dto.invalideVotes,
      gueltigkeit: dto.gueltigkeit,
      wahlvorstandBeschlussvorschlag: wahlvorstandBeschlussgruende,
      systemBeschlussvorschlag: systemBeschlussgruende,
      beschlussfassung: dto.beschlussfassung
        ? _beschlussfassungDtoToModel(dto.beschlussfassung)
        : null,
    };
  }

  function toPersistedStimmzettel(
    manageableStimmzettel: ManageableStimmzettel,
    stimmzettelkennung: number,
    teamID: string
  ): Stimmzettel {
    if (!manageableStimmzettel.gueltigkeit) {
      throw new Error("Stimmzettel muss eine Gültigkeit besitzen");
    }

    const mappedWahlvorschlaege: Wahlvorschlag[] =
      manageableStimmzettel.wahlvorschlaege
        .map((wahlvorschlag) => {
          const mappedKandidaten: Kandidat[] = wahlvorschlag.kandidaten
            .filter((kandidat) => hasAnyKennzeichen(kandidat))
            .map((kandidat) => ({
              kandidatId: kandidat.kandidatId,
              nennung: kandidat.nennung,
              votesByWahlvorschlag: kandidat.reststimmen ?? 0,
              invalidVotes: kandidat.ungueltigeStimmen ?? 0,
              votesByVoter: kandidat.einzelstimmen ?? 0,
              isDiscarded: kandidat.durchgestrichen ?? false,
            }));

          return {
            kandidaten: mappedKandidaten,
            wahlvorschlagID: wahlvorschlag.wahlvorschlagID,
            selected: wahlvorschlag.selected,
          };
        })
        .filter(
          (wahlvorschlag) =>
            wahlvorschlag.kandidaten.length > 0 || wahlvorschlag.selected
        );

    return {
      teamID: teamID,
      stimmzettelkennung: stimmzettelkennung,
      gueltigkeit: manageableStimmzettel.gueltigkeit,
      invalideVotes: manageableStimmzettel.invalideVotes ?? 0,
      wahlvorschlaege: mappedWahlvorschlaege,
      wahlvorstandBeschlussvorschlag:
        manageableStimmzettel.wahlvorstandBeschlussvorschlag.map(
          (vorschlag) => ({ text: vorschlag.text })
        ),
      systemBeschlussvorschlag:
        manageableStimmzettel.systemBeschlussvorschlag.map((vorschlag) => ({
          reason: vorschlag.reason,
        })),
      beschlussfassung: manageableStimmzettel.beschlussfassung,
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
      wahlvorstandBeschlussvorschlag:
        model.wahlvorstandBeschlussvorschlag.length > 0
          ? model.wahlvorstandBeschlussvorschlag.map((beschlussgrund) =>
              _wahlvorstandBeschlussgrundModelToDto(beschlussgrund)
            )
          : undefined,
      systemBeschlussvorschlag:
        model.systemBeschlussvorschlag.length > 0
          ? model.systemBeschlussvorschlag.map((beschlussgrund) =>
              _systemBeschlussgrundModelToDto(beschlussgrund)
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

  function _systemBeschlussgrundDtoToModel(
    dto: SystemBeschlussgrundDTO
  ): SystemBeschlussgrund {
    return {
      reason: dto.reason,
    };
  }

  function _wahlvorstandBeschlussgrundDtoToModel(
    dto: WahlvorstandBeschlussgrundDTO
  ): WahlvorstandBeschlussgrund {
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

  function _systemBeschlussgrundModelToDto(
    model: SystemBeschlussgrund
  ): SystemBeschlussgrundDTO {
    return {
      reason: model.reason,
    };
  }

  function _wahlvorstandBeschlussgrundModelToDto(
    model: WahlvorstandBeschlussgrund
  ): WahlvorstandBeschlussgrundDTO {
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
    toPersistedStimmzettel,
    toDTO,
  };
}
