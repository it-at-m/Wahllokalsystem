import type { BeschlussgrundOption } from "@/types/dse/beschlussfassung/BeschlussgrundOption.ts";
import type { SystemBeschlussgrund } from "@/types/dse/beschlussfassung/SystemBeschlussgrund.ts";
import type { WahlvorstandBeschlussgrund } from "@/types/dse/beschlussfassung/WahlvorstandBeschlussgrund.ts";
import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";

import { useBeschlussgrundTools } from "@/composables/dse/beschlussfassung/beschlussgrundTools.ts";
import { useStimmzettelTools } from "@/composables/dse/stimmzettelerfassung/stimmzettelTools.ts";
import { SystemBeschlussgrundReasonEnum } from "@/types/dse/beschlussfassung/SystemBeschlussgrundReasonEnum.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";

const { getBeschlussgrundEnumValueAsString } = useBeschlussgrundTools();
const { isStimmzettelEmpty } = useStimmzettelTools();

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
        result.push(
          getBeschlussgrundEnumValueAsString(beschlussvorschlag.text)
        );
      }
    }

    for (const beschlussvorschlag of systemBeschlussvorschlag) {
      const existsInOptions = !!beschlussgrundOptions.find(
        (beschlussgrundOption) =>
          beschlussgrundOption.grund === beschlussvorschlag.reason
      );
      if (!existsInOptions) {
        result.push(
          getBeschlussgrundEnumValueAsString(beschlussvorschlag.reason)
        );
      }
    }

    return result.join(", ");
  }

  function setSystemBeschlussgrundKeineGueltigenStimmen(
    stimmzettel: PersistedStimmzettel | undefined,
    beschlussgrundOptions: BeschlussgrundOption[]
  ) {
    if (stimmzettel) {
      if (
        stimmzettel.gueltigkeit === StimmzettelGueltigkeitEnum.Leer ||
        isStimmzettelEmpty(stimmzettel)
      ) {
        const beschlussgrundKeineGueltigenStimmen = beschlussgrundOptions.find(
          (beschlussgrundOption) =>
            beschlussgrundOption.grund ===
            SystemBeschlussgrundReasonEnum.KeineGueltigenStimmen
        );
        if (beschlussgrundKeineGueltigenStimmen) {
          beschlussgrundKeineGueltigenStimmen.selected = true;
        }
      }
    }
  }

  return {
    mapGruendeToBeschlussgrundOptions,
    setSystemBeschlussgruendeTrueWhenFoundInStimmzettel,
    setWahlvorstandBeschlussgruendeTrueWhenFoundInStimmzettel,
    setBeschlussgruendeToAndererGrundWhenNotFoundInBeschlussGruendeList,
    setSystemBeschlussgrundKeineGueltigenStimmen,
  };
}
