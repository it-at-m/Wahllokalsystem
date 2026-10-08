import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";

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
    const beschlussgrundOptions =
      mapGruendeToBeschlussgrundOptions(gruendeList);

    setSystemBeschlussgruendeTrueWhenFoundInStimmzettel(
      stimmzettel?.systemBeschlussvorschlag ?? [],
      beschlussgrundOptions
    );
    setWahlvorstandBeschlussgruendeTrueWhenFoundInStimmzettel(
      stimmzettel?.wahlvorstandBeschlussvorschlag ?? [],
      beschlussgrundOptions
    );

    const andererGrund =
      setBeschlussgruendeToAndererGrundWhenNotFoundInBeschlussGruendeList(
        stimmzettel?.wahlvorstandBeschlussvorschlag ?? [],
        stimmzettel?.systemBeschlussvorschlag ?? [],
        beschlussgrundOptions
      );
    const beschlussgruende = beschlussgrundOptions;

    return {
      andererGrund,
      beschlussgruende,
    };
  }

  return {
    createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit,
  };
}
