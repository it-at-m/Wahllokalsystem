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

  return {
    createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit,
  };
}
