import type {
  EreignisDTO,
  EreignisseWriteDTO,
  WahlbezirkEreignisseDTO,
} from "@/api/wls-clients/generated-vorfaelleundvorkommnisse-api";
import type { Ereignis } from "@/types/vorfaelleundvorkommnisse/Ereignis.ts";
import type { EreignisseWrite } from "@/types/vorfaelleundvorkommnisse/EreignisseWrite.ts";
import type { WahlbezirkEreignisse } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse.ts";

import { EreignisDTOEreignisartEnum } from "@/api/wls-clients/generated-vorfaelleundvorkommnisse-api";
import { EreignisartEnum } from "@/types/vorfaelleundvorkommnisse/Ereignisart.ts";

export function useEreignisMapper() {
  function toModel(
    ereignisseDto: WahlbezirkEreignisseDTO
  ): WahlbezirkEreignisse {
    const ereignisseAsModel =
      ereignisseDto.ereigniseintraege?.map((ereignis) =>
        ereignisDtoToEreignisModel(ereignis)
      ) ?? [];
    return {
      wahlbezirkID: ereignisseDto.wahlbezirkID,
      keineVorfaelle: ereignisseDto.keineVorfaelle ?? false,
      keineVorkommnisse: ereignisseDto.keineVorkommnisse ?? false,
      ereigniseintraege: ereignisseAsModel,
    };
  }

  function toDto(ereignisseModel: EreignisseWrite): EreignisseWriteDTO {
    const ereignisseAsDto = ereignisseModel.ereigniseintraege?.map(
      (ereignisModel) => ereignisModelToEreignisDto(ereignisModel)
    );
    return {
      ereigniseintraege: ereignisseAsDto,
    };
  }

  function ereignisDtoToEreignisModel(ereignisDto: EreignisDTO): Ereignis {
    return {
      beschreibung: ereignisDto.beschreibung ?? "",
      uhrzeit: ereignisDto.uhrzeit ?? "",
      ereignisart: ereignisDto.ereignisart
        ? ereignisartDtoToEreignisartModel(ereignisDto.ereignisart)
        : undefined,
    };
  }

  function ereignisModelToEreignisDto(ereignisModel: Ereignis): EreignisDTO {
    return {
      beschreibung: ereignisModel.beschreibung ?? "",
      uhrzeit: ereignisModel.uhrzeit ?? "",
      ereignisart: ereignisModel.ereignisart
        ? ereignisartModelToEreignisartDto(ereignisModel.ereignisart)
        : undefined,
    };
  }

  function ereignisartDtoToEreignisartModel(
    ereignisartDto: EreignisDTOEreignisartEnum
  ): EreignisartEnum {
    switch (ereignisartDto) {
      case "VORFALL":
        return EreignisartEnum.Vorfall;
      case "VORKOMMNIS":
        return EreignisartEnum.Vorkommnis;
    }
  }

  function ereignisartModelToEreignisartDto(
    ereignisartModel: EreignisartEnum
  ): EreignisDTOEreignisartEnum {
    switch (ereignisartModel) {
      case "VORFALL":
        return EreignisDTOEreignisartEnum.Vorfall;
      case "VORKOMMNIS":
        return EreignisDTOEreignisartEnum.Vorkommnis;
    }
  }

  return {
    toModel,
    toDto,
  };
}
