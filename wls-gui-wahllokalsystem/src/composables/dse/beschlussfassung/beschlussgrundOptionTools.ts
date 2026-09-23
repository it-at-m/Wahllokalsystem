import type { BeschlussgrundOption } from "@/types/dse/beschlussfassung/BeschlussgrundOption.ts";
import type { SystemBeschlussgrund } from "@/types/dse/beschlussfassung/SystemBeschlussgrund.ts";
import type { WahlvorstandBeschlussgrund } from "@/types/dse/beschlussfassung/WahlvorstandBeschlussgrund.ts";

export function useBeschlussgrundOptionTools() {
  function mapGruendeToBeschlussgrundOptions(
    gruende: string[]
  ): BeschlussgrundOption[] {
    return gruende.map((element) => ({
      grund: element,
      selected: false,
    }));
  }

  function setSystemBeschlussgruendeTrueWhenFoundInStimmzettel(
    systemBeschlussvorschlag: SystemBeschlussgrund[],
    beschlussgrundOptions: BeschlussgrundOption[]
  ) {
    for (const beschlussvorschlag of systemBeschlussvorschlag) {
      const entry = beschlussgrundOptions.find(
        (beschlussgrundOption) =>
          beschlussgrundOption.grund === beschlussvorschlag.reason
      );
      if (entry) {
        entry.selected = true;
      }
    }
  }

  function setWahlvorstandBeschlussgruendeTrueWhenFoundInStimmzettel(
    wahlvorstandBeschlussvorschlag: WahlvorstandBeschlussgrund[],
    beschlussgrundOptions: BeschlussgrundOption[]
  ) {
    for (const beschlussvorschlag of wahlvorstandBeschlussvorschlag) {
      const entry = beschlussgrundOptions.find(
        (beschlussgrundOption) =>
          beschlussgrundOption.grund === beschlussvorschlag.text
      );
      if (entry) {
        entry.selected = true;
      }
    }
  }

  function setBeschlussgruendeToAndererGrundWhenNotFoundInBeschlussGruendeList(
    wahlvorstandBeschlussvorschlag: WahlvorstandBeschlussgrund[],
    systemBeschlussvorschlag: SystemBeschlussgrund[],
    beschlussgrundOptions: BeschlussgrundOption[]
  ) {
    const result: string[] = [];

    for (const beschlussvorschlag of wahlvorstandBeschlussvorschlag) {
      const existsInOptions = !!beschlussgrundOptions.find(
        (beschlussgrund) => beschlussgrund.grund === beschlussvorschlag.text
      );
      if (!existsInOptions) {
        result.push(beschlussvorschlag.text);
      }
    }

    for (const beschlussvorschlag of systemBeschlussvorschlag) {
      const existsInOptions = !!beschlussgrundOptions.find(
        (beschlussgrundOption) =>
          beschlussgrundOption.grund === beschlussvorschlag.reason
      );
      if (!existsInOptions) {
        result.push(beschlussvorschlag.reason);
      }
    }

    return result.join(", ");
  }

  return {
    mapGruendeToBeschlussgrundOptions,
    setSystemBeschlussgruendeTrueWhenFoundInStimmzettel,
    setWahlvorstandBeschlussgruendeTrueWhenFoundInStimmzettel,
    setBeschlussgruendeToAndererGrundWhenNotFoundInBeschlussGruendeList,
  };
}
