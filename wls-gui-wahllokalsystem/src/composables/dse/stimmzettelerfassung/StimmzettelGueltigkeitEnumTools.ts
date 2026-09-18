import { PersistedStimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettelGueltigkeitEnum.ts";

export function useStimmzettelGueltigkeitEnumTools() {
  const gueltigkeitTextMap = {
    [PersistedStimmzettelGueltigkeitEnum.Valid]: "gültig",
    [PersistedStimmzettelGueltigkeitEnum.Invalid]: "ungültig",
    [PersistedStimmzettelGueltigkeitEnum.BeschlussAusstehend]:
      "Beschluss notwendig",
    [PersistedStimmzettelGueltigkeitEnum.BwbPseudoStimmzettelLeererUmschlag]:
      "ungültig",
    [PersistedStimmzettelGueltigkeitEnum.Leer]: "ungültig",
  };

  function toText(value: PersistedStimmzettelGueltigkeitEnum): string {
    return gueltigkeitTextMap[value];
  }

  return {
    toText,
  };
}
