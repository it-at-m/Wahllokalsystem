import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";

import { storeToRefs } from "pinia";
import { ref } from "vue";

import { useBeschlussgrundOptionTools } from "@/composables/dse/beschlussfassung/beschlussgrundOptionTools.ts";
import { useUserStore } from "@/stores/userStore.ts";

const {
  mapGruendeToBeschlussgrundOptions,
  setSystemBeschlussgruendeTrueWhenFoundInStimmzettel,
  setWahlvorstandBeschlussgruendeTrueWhenFoundInStimmzettel,
  setBeschlussgruendeToAndererGrundWhenNotFoundInBeschlussGruendeList,
} = useBeschlussgrundOptionTools();

export function useTheBeschlussFassenTabUtils() {
  const { isBWB } = storeToRefs(useUserStore());

  const beschlussGruende = {
    common: {
      gueltig: [
        "Wählerwille ist zweifelsfrei erkennbar (lila Notiz auf dem Stimmzettel)",
        "Mehr als 3 Stimmen bei mind. einer Person und 80 Stimmen gesamt nicht überschritten",
        "keine Reststimmenvergabe möglich, Einzelstimmen und mehrere Kopfleistenkreuze",
        "einzelne Stimmen ungültig",
      ],
      ungueltig: [
        "Wählerwille ist nicht zweifelsfrei erkennbar",
        "mehr als 80 Einzelstimmen oder mehrere Kopfleistenkreuze ohne Einzelstimmen",
        "Stimmzettel ist mit einem besonderen Merkmal, Zusatz oder Vorbehalt versehen",
        "Stimmzettel ist nicht amtlich hergestellt (zum Beispiel von einer anderen Gemeinde)",
      ],
    },
    bwb: {
      gueltig: [
        "Mehrere gleich gekennzeichnete Stimmzettel im Umschlag",
        "Mehrere Stimmzettel im Umschlag, einer gekennzeichnet, die anderen leer",
      ],
      ungueltig: [
        "Mehrere unterschiedlich gekennzeichnete Stimmzettel im Umschlag",
      ],
    },
  };

  function updateBeschlussgruendeBasedOnStimmzettelAndGueltigkeit(
    isStimmzettelGueltig: boolean | null,
    stimmzettel: PersistedStimmzettel | undefined
  ) {
    if (isStimmzettelGueltig === null)
      return { andererGrund: "", beschlussgruende: [] };

    const gruendeList =
      _getBeschlussGruendeBasedOnGueltigkeit(isStimmzettelGueltig);
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

  function _getBeschlussGruendeBasedOnGueltigkeit(isGueltig: boolean) {
    return isGueltig
      ? isBWB.value
        ? [...beschlussGruende.common.gueltig, ...beschlussGruende.bwb.gueltig]
        : beschlussGruende.common.gueltig
      : isBWB.value
        ? [
            ...beschlussGruende.common.ungueltig,
            ...beschlussGruende.bwb.ungueltig,
          ]
        : beschlussGruende.common.ungueltig;
  }

  return {
    updateBeschlussgruendeBasedOnStimmzettelAndGueltigkeit,
  };
}
