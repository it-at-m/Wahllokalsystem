import { StimmzettelGueltigkeitEnum } from "@/types/dse/persistedStimmzettel/StimmzettelGueltigkeitEnum.ts";

export function useStimmzettelGueltigkeitEnumTools() {
  const gueltigkeitTextMap = {
    [StimmzettelGueltigkeitEnum.Valid]: "gültig",
    [StimmzettelGueltigkeitEnum.Invalid]: "ungültig",
    [StimmzettelGueltigkeitEnum.BeschlussAusstehend]: "Beschluss notwendig",
    [StimmzettelGueltigkeitEnum.BwbPseudoStimmzettelLeererUmschlag]: "ungültig",
    [StimmzettelGueltigkeitEnum.Leer]: "ungültig",
  };

  function toText(value: StimmzettelGueltigkeitEnum): string {
    return gueltigkeitTextMap[value];
  }

  return {
    toText,
  };
}
