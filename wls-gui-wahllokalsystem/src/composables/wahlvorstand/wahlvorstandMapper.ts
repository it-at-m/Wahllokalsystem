import type {
  WahlvorstandDTO,
  WahlvorstandsmitgliedDTO,
  WahlvorstandWriteDTO,
} from "@/api/wls-clients/generated-wahlvorstand-api";
import type { Wahlvorstand } from "@/types/wahlvorstand/wahlvorstand";
import type { Wahlvorstandsmitglied } from "@/types/wahlvorstand/wahlvorstandsmitglied";

import { WahlvorstandsmitgliedDTOFunktionEnum } from "@/api/wls-clients/generated-wahlvorstand-api";
import { WahlvorstandsmitgliedFunktionEnum } from "@/types/wahlvorstand/wahlvorstandsmitgliedFunktion";

export function useWahlvorstandMapper() {
  function toModel(wahlvorstand: WahlvorstandDTO): Wahlvorstand {
    const mitgliederAsModel =
      wahlvorstand.wahlvorstandsmitglieder?.map((mitglied) =>
        wahlvorstandmitgliedDtoToWahlvorstandModel(mitglied)
      ) ?? [];
    return {
      wahlvorstandsmitglieder: mitgliederAsModel,
    };
  }

  function toDto(
    wahlvorstand: Wahlvorstand,
    datetime: Date
  ): WahlvorstandWriteDTO {
    const wahlvorstandsmitgliederAsDTO =
      wahlvorstand.wahlvorstandsmitglieder?.map((mitglied) =>
        wahlvorstandmitgliedToWahlvorstandmitgliedDto(mitglied)
      );

    return {
      anwesenheitBeginn: datetime.toISOString(),
      wahlvorstandsmitglieder: wahlvorstandsmitgliederAsDTO,
    };
  }

  function wahlvorstandmitgliedDtoToWahlvorstandModel(
    wahlvorstandmitglied: WahlvorstandsmitgliedDTO
  ): Wahlvorstandsmitglied {
    return {
      anwesend: wahlvorstandmitglied.anwesend ?? false,
      familienname: wahlvorstandmitglied.familienname,
      funktion: funktionDtoToFunktionModel(wahlvorstandmitglied.funktion),
      funktionsname: wahlvorstandmitglied.funktionsname,
      identifikator: wahlvorstandmitglied.identifikator ?? "",
      vorname: wahlvorstandmitglied.vorname,
    };
  }

  function wahlvorstandmitgliedToWahlvorstandmitgliedDto(
    wahlvorstandsmitglied: Wahlvorstandsmitglied
  ): WahlvorstandsmitgliedDTO {
    return {
      anwesend: wahlvorstandsmitglied.anwesend,
      familienname: wahlvorstandsmitglied.familienname,
      funktionsname: wahlvorstandsmitglied.funktionsname,
      identifikator: wahlvorstandsmitglied.identifikator,
      vorname: wahlvorstandsmitglied.vorname,
      funktion: funktionModelToFunktionDto(wahlvorstandsmitglied.funktion),
    };
  }

  function funktionModelToFunktionDto(
    funktionAsModel?: WahlvorstandsmitgliedFunktionEnum
  ): WahlvorstandsmitgliedDTOFunktionEnum | undefined {
    if (!funktionAsModel) {
      return undefined;
    }

    switch (funktionAsModel) {
      case "SB":
        return WahlvorstandsmitgliedDTOFunktionEnum.Sb;
      case "B":
        return WahlvorstandsmitgliedDTOFunktionEnum.B;
      case "W":
        return WahlvorstandsmitgliedDTOFunktionEnum.W;
      case "SWB":
        return WahlvorstandsmitgliedDTOFunktionEnum.Swb;
      case "SSB":
        return WahlvorstandsmitgliedDTOFunktionEnum.Ssb;
    }
  }

  function funktionDtoToFunktionModel(
    funktionAsModel?: WahlvorstandsmitgliedDTOFunktionEnum
  ): WahlvorstandsmitgliedFunktionEnum | undefined {
    if (!funktionAsModel) {
      return undefined;
    }

    switch (funktionAsModel) {
      case "SB":
        return WahlvorstandsmitgliedFunktionEnum.Sb;
      case "B":
        return WahlvorstandsmitgliedFunktionEnum.B;
      case "W":
        return WahlvorstandsmitgliedFunktionEnum.W;
      case "SWB":
        return WahlvorstandsmitgliedFunktionEnum.Swb;
      case "SSB":
        return WahlvorstandsmitgliedFunktionEnum.Ssb;
    }
  }

  return {
    toModel,
    toDto,
  };
}
