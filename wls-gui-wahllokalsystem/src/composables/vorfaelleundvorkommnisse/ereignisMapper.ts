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

const { toYyyyMmDdWithTimeWithoutTimezoneOffset } = useDateTimeFormatter();

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
    const ereignisseAsDto = ereignisseModel.ereigniseintraege.map(
      (ereignisModel) => ereignisModelToEreignisDto(ereignisModel)
    );
    return {
      keineVorfaelle: ereignisseModel.keineVorfaelle,
      keineVorkommnisse: ereignisseModel.keineVorkommnisse,
      ereigniseintraege: ereignisseAsDto?.filter(
        (ereignis) => ereignis.beschreibung
      ),
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
    ereignisart: ereignisartDtoToEreignisartModel(ereignisDto.ereignisart),
  };
}

function ereignisModelToEreignisDto(ereignisModel: Ereignis): EreignisDTO {
  let mappedUhrzeit;
  if (ereignisModel.uhrzeit) {
    mappedUhrzeit = toYyyyMmDdWithTimeWithoutTimezoneOffset(
      ereignisModel.uhrzeit
    );
  }
  return {
    beschreibung: ereignisModel.beschreibung ?? "",
    uhrzeit: mappedUhrzeit,
    ereignisart: ereignisartModelToEreignisartDto(ereignisModel.ereignisart),
  };
}

function ereignisartModelToEreignisartDto(
  ereignisartModel: EreignisartEnum
): EreignisDTOEreignisartEnum {
  switch (ereignisartModel) {
    case EreignisartEnum.Vorfall:
      return EreignisDTOEreignisartEnum.Vorfall;
    case EreignisartEnum.Vorkommnis:
      return EreignisDTOEreignisartEnum.Vorkommnis;
  }
}

function ereignisartDtoToEreignisartModel(
  ereignisartDto: EreignisDTOEreignisartEnum
): EreignisartEnum {
  switch (ereignisartDto) {
    case EreignisDTOEreignisartEnum.Vorfall:
      return EreignisartEnum.Vorfall;
    case EreignisDTOEreignisartEnum.Vorkommnis:
      return EreignisartEnum.Vorkommnis;
  }
}
