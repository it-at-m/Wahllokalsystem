import type {
  EreignisDTO,
  EreignisseWriteDTO,
  WahlbezirkEreignisseDTO,
} from "@/api/wls-clients/generated-vorfaelleundvorkommnisse-api";
import type { Ereignis } from "@/types/vorfaelleundvorkommnisse/Ereignis.ts";
import type { WahlbezirkEreignisse } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse.ts";

import { EreignisDTOEreignisartEnum } from "@/api/wls-clients/generated-vorfaelleundvorkommnisse-api";
import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { EreignisartEnum } from "@/types/vorfaelleundvorkommnisse/Ereignisart.ts";

const { applyLocalTimezoneOffset } = useDateTimeFormatter();

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

  function toDto(ereignisseModel: WahlbezirkEreignisse): EreignisseWriteDTO {
    const ereignisseAsDto = ereignisseModel.ereigniseintraege?.map(
      (ereignisModel) => ereignisModelToEreignisDto(ereignisModel)
    );
    return {
      ereigniseintraege: ereignisseAsDto,
    };
  }

  return {
    toModel,
    toDto,
  };
}

function ereignisDtoToEreignisModel(ereignisDto: EreignisDTO): Ereignis {
  return {
    beschreibung: ereignisDto.beschreibung ?? "",
    uhrzeit: ereignisDto.uhrzeit ? new Date(ereignisDto.uhrzeit) : undefined,
    ereignisart: ereignisDto.ereignisart
      ? ereignisartDtoToEreignisartModel(ereignisDto.ereignisart)
      : undefined,
  };
}

function ereignisModelToEreignisDto(ereignisModel: Ereignis): EreignisDTO {
  let mappedUhrzeit;
  if (ereignisModel.uhrzeit) {
    mappedUhrzeit = applyLocalTimezoneOffset(ereignisModel.uhrzeit);
  }
  return {
    beschreibung: ereignisModel.beschreibung ?? "",
    uhrzeit: mappedUhrzeit?.toJSON(),
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
