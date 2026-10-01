import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";

import { ref } from "vue";

import { useBeschlussgrundOptionTools } from "@/composables/dse/beschlussfassung/beschlussgrundOptionTools.ts";
import { useBeschlussgrundTools } from "@/composables/dse/beschlussfassung/beschlussgrundTools.ts";

const {
  mapGruendeToBeschlussgrundOptions,
  setSystemBeschlussgruendeTrueWhenFoundInStimmzettel,
  setWahlvorstandBeschlussgruendeTrueWhenFoundInStimmzettel,
  setBeschlussgruendeToAndererGrundWhenNotFoundInBeschlussGruendeList,
} = useBeschlussgrundOptionTools();

export function useTheBeschlussFassenTabUtils() {
  const { getBeschlussGruendeBasedOnGueltigkeit } = useBeschlussgrundTools();

  function createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit(
    isStimmzettelGueltig: boolean | null,
    stimmzettel: PersistedStimmzettel | undefined
  ) {
    if (isStimmzettelGueltig === null)
      return { andererGrund: "", beschlussgruende: [] };

    const gruendeList =
      getBeschlussGruendeBasedOnGueltigkeit(isStimmzettelGueltig);
    const beschlussgrundOptions = ref(
      mapGruendeToBeschlussgrundOptions(gruendeList)
    );

    setSystemBeschlussgruendeTrueWhenFoundInStimmzettel(
      stimmzettel?.systemBeschlussvorschlag ?? [],
      beschlussgrundOptions.value
    );
    setWahlvorstandBeschlussgruendeTrueWhenFoundInStimmzettel(
      stimmzettel?.wahlvorstandBeschlussvorschlag ?? [],
      beschlussgrundOptions.value
    );

    const andererGrund =
      setBeschlussgruendeToAndererGrundWhenNotFoundInBeschlussGruendeList(
        stimmzettel?.wahlvorstandBeschlussvorschlag ?? [],
        stimmzettel?.systemBeschlussvorschlag ?? [],
        beschlussgrundOptions.value
      );
    const beschlussgruende = beschlussgrundOptions.value;

    return {
      andererGrund,
      beschlussgruende,
    };
  }

  function isStimmzettelGueltigBasedOnVormerkungsgruenden(
    stimmzettel: PersistedStimmzettel
  ) {
    const ungueltigOptions =
      createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit(
        false,
        undefined
      ).beschlussgruende;
    const ungueltigSet = new Set(ungueltigOptions.map((o) => o.grund));

    const hasUngueltigerSystemGrund = (
      stimmzettel.systemBeschlussvorschlag ?? []
    ).some((beschlussvorschlag) => {
      return ungueltigSet.has(beschlussvorschlag.reason);
    });

    const hasUngueltigerWahlvorstandGrund = (
      stimmzettel.wahlvorstandBeschlussvorschlag ?? []
    ).some((beschlussvorschlag) => {
      return ungueltigSet.has(beschlussvorschlag.text);
    });

    return !(hasUngueltigerSystemGrund || hasUngueltigerWahlvorstandGrund);
  }

  return {
    createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit,
    isStimmzettelGueltigBasedOnVormerkungsgruenden,
  };
}
