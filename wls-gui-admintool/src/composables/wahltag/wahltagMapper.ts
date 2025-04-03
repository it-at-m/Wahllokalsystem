import type { WahltagDTO } from "@/api/wls-clients/generated-admin-api";
import type { Wahltag } from "@/types/wahltag/Wahltag.ts";

import { type WahltagEvent } from "@/types/wahltag/WahltagEvent.ts";

export function useWahltagMapper() {
  function mapWahltagDtoToWahltagEvent(dto: WahltagDTO): WahltagEvent {
    return {
      nummer: dto.nummer,
      wahltagID: dto.wahltagID,
      beschreibung: dto.beschreibung,
    };
  }

  function mapWahltagModelToWahltagDto(model: Wahltag): WahltagDTO {
    return {
      nummer: "",
      wahltagID: "",
      wahltag: model.wahltag,
      beschreibung: "",
    };
  }

  function mapGroupedWahltagDtosToWahltage(
    groupedDtos: Map<string, WahltagDTO[]>
  ): Wahltag[] {
    const result: Wahltag[] = [];

    groupedDtos.forEach((wahltage, wahltagDatum) => {
      const wahltagEvents = wahltage.map((dto) =>
        mapWahltagDtoToWahltagEvent(dto)
      );

      result.push({
        wahltag: wahltagDatum,
        events: wahltagEvents,
      });
    });

    return result;
  }

  return {
    wahltagModelToWahltagDto: mapWahltagModelToWahltagDto,
    mapWahltagDtoToWahltagEvent,
    mapGroupedWahltagDtosToWahltage,
  };
}
