import type { WahltagDTO } from "@/api/wls-clients/generated-admin-api";

export function useWahltagDtoUtils() {
  function groupWahltagDtosByWahltag(
    dtos: WahltagDTO[]
  ): Map<string, WahltagDTO[]> {
    const groupedWahltage = new Map<string, WahltagDTO[]>();

    dtos.reduce((group, wahltagDTO) => {
      const { wahltag } = wahltagDTO;

      const currentGroupValue = group.get(wahltag) ?? [];
      currentGroupValue.push(wahltagDTO);
      group.set(wahltag, currentGroupValue);

      return group;
    }, groupedWahltage);

    return groupedWahltage;
  }

  return {
    groupWahltagDtosByWahltag,
  };
}
