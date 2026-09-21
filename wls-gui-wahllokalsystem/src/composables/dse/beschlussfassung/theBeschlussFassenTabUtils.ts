import type { SystemBeschlussgrund } from "@/types/dse/beschlussfassung/SystemBeschlussgrund.ts";
import type { WahlvorstandBeschlussgrund } from "@/types/dse/beschlussfassung/WahlvorstandBeschlussgrund.ts";
import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";

import { storeToRefs } from "pinia";
import { ref } from "vue";

import { useSystemBeschlussgrundReasonEnumTools } from "@/composables/dse/beschlussfassung/systemBeschlussgrundReasonEnumTools.ts";
import { useUserStore } from "@/stores/userStore.ts";

const { mapSystemBeschlussgrundReasonEnumToBeschlussvorschlagText } =
  useSystemBeschlussgrundReasonEnumTools();

interface BeschlussgrundOption {
  grund: string;
  selected: boolean;
}

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
      _mapGruendeToBeschlussgrundOptions(gruendeList)
    );

    _setSystemBeschlussgruendeTrueWhenFoundInStimmzettel(
      stimmzettel?.systemBeschlussvorschlag ?? [],
      beschlussgrundOptions.value
    );
    _setWahlvorstandBeschlussgruendeTrueWhenFoundInStimmzettel(
      stimmzettel?.wahlvorstandBeschlussvorschlag ?? [],
      beschlussgrundOptions.value
    );

    const texts =
      _setWahlvorstandbeschlussgruendeToAndererGrundWhenNotFoundInBeschlussGruendeList(
        stimmzettel?.wahlvorstandBeschlussvorschlag ?? [],
        beschlussgrundOptions.value
      );

    const andererGrund = texts.join(", ");
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

  function _mapGruendeToBeschlussgrundOptions(
    gruende: string[]
  ): BeschlussgrundOption[] {
    return gruende.map((element) => ({
      grund: element,
      selected: false,
    }));
  }

  function _setSystemBeschlussgruendeTrueWhenFoundInStimmzettel(
    systemBeschlussvorschlag: SystemBeschlussgrund[],
    beschlussgrundOptions: BeschlussgrundOption[]
  ) {
    for (const beschlussvorschlag of systemBeschlussvorschlag) {
      const reasonAsGrund =
        mapSystemBeschlussgrundReasonEnumToBeschlussvorschlagText(
          beschlussvorschlag.reason
        );
      if (reasonAsGrund) {
        const entry = beschlussgrundOptions.find(
          (beschlussgrundOption) => beschlussgrundOption.grund === reasonAsGrund
        );
        if (entry) {
          entry.selected = true;
        }
      }
    }
  }

  function _setWahlvorstandBeschlussgruendeTrueWhenFoundInStimmzettel(
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

  function _setWahlvorstandbeschlussgruendeToAndererGrundWhenNotFoundInBeschlussGruendeList(
    wahlvorstandBeschlussvorschlag: WahlvorstandBeschlussgrund[],
    beschlussgrundOptions: BeschlussgrundOption[]
  ) {
    return wahlvorstandBeschlussvorschlag
      .filter(
        (beschlussvorschlag) =>
          !beschlussgrundOptions.find(
            (beschlussgrund) => beschlussgrund.grund === beschlussvorschlag.text
          )
      )
      .map((w) => w.text);
  }

  return {
    updateBeschlussgruendeBasedOnStimmzettelAndGueltigkeit,
  };
}
