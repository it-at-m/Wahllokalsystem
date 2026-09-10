import { StimmzettelGueltigkeitEnum } from "@/types/dse/persistedStimmzettel/StimmzettelGueltigkeitEnum.ts";

export function useStimmzettelGueltigkeitEnumTools() {
  const gueltigkeitTextMap = {
    [StimmzettelGueltigkeitEnum.Valid]: "Stimmzettel ist gültig",
    [StimmzettelGueltigkeitEnum.Invalid]: "Stimmzettel ist ungültig",
    [StimmzettelGueltigkeitEnum.BeschlussAusstehend]:
      "Stimmzettel ist für Beschluss vorgemerkt",
    [StimmzettelGueltigkeitEnum.BwbPseudoStimmzettelLeererUmschlag]:
      "Stimmzettel ist ungültig",
    [StimmzettelGueltigkeitEnum.Leer]: "Stimmzettel ist ungültig",
  };

  function toText(value: StimmzettelGueltigkeitEnum): string {
    return gueltigkeitTextMap[value];
  }

  return {
    toText,
  };
}
