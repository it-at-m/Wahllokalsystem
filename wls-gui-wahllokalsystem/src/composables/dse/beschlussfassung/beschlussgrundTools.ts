import type { WahlvorstandBeschlussgrund } from "@/types/dse/beschlussfassung/WahlvorstandBeschlussgrund.ts";

export function useBeschlussgrundTools() {
  const commonWahlvorstandBeschlussvorschlaege = [
    "einzelne Stimmen ungültig",
    "Wählerwille ist nicht zweifelsfrei erkennbar",
    "Stimmzettel ist mit einem besonderen Merkmal, Zusatz oder Vorbehalt versehen",
    "Stimmzettel ist nicht amtlich hergestellt (zum Beispiel von einer anderen Gemeinde)",
  ];
  const bwbWahlvorstandBeschlussvorschlaege = [
    "Mehrere gleich gekennzeichnete Stimmzettel im Umschlag",
    "Mehrere Stimmzettel im Umschlag, einer gekennzeichnet, die anderen leer",
    "Mehrere unterschiedlich gekennzeichnete Stimmzettel im Umschlag",
  ];

  function getWahlvorstandBeschlussvorschlaege(isBWB: boolean) {
    if (isBWB) {
      return [
        ...commonWahlvorstandBeschlussvorschlaege,
        ...bwbWahlvorstandBeschlussvorschlaege,
      ];
    } else {
      return [...commonWahlvorstandBeschlussvorschlaege];
    }
  }

  function createBeschlussgrundWithText(
    text: string
  ): WahlvorstandBeschlussgrund {
    return {
      text,
    };
  }

  return {
    createBeschlussgrundWithText,
    getWahlvorstandBeschlussvorschlaege,
  };
}
