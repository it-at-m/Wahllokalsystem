import type { BeschlussAbstimmungsergebnis } from "@/types/dse/beschlussfassung/BeschlussAbstimmungsergebnis.ts";
import type { BeschlussfassungDialogDetails } from "@/types/dse/beschlussfassung/BeschlussfassungDialogDetails.ts";
import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";
import type { Ref } from "vue";

import { storeToRefs } from "pinia";
import { ref, watch, watchEffect } from "vue";

import { useBeschlussgrundTools } from "@/composables/dse/beschlussfassung/beschlussgrundTools.ts";
import { useTheBeschlussFassenTabUtils } from "@/composables/dse/beschlussfassung/theBeschlussFassenTabUtils.ts";
import { useWahlvorstandStore } from "@/stores/wahlvorstandStore.ts";

interface BeschlussgrundOption {
  grund: string;
  selected: boolean;
}

export function useTheBeschlussfassungBearbeitenDialogUtils(
  stimmzettel: Ref<PersistedStimmzettel | undefined>
) {
  const { anwesendeWahlvorstandsmitgliederAnzahl } = storeToRefs(
    useWahlvorstandStore()
  );
  const {
    createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit,
    isStimmzettelGueltigBasedOnVormerkungsgruenden,
  } = useTheBeschlussFassenTabUtils();
  const { getBeschlussgrundEnumValueAsString } = useBeschlussgrundTools();

  const abstimmungsergebnis = ref<BeschlussAbstimmungsergebnis>({
    stimmenDafuer: null,
    stimmenDagegen: null,
    hasWahlvorsteherVotedDafuer: false,
    abstimmungIsUnentschieden: false,
    abstimmungIsUngueltig: false,
  });

  const beschlussDetails = ref<BeschlussfassungDialogDetails>({
    isGueltig: null,
    beschlussgruende: [],
    andererGrund: "",
    andererGrundChecked: false,
    beschlussText: "",
  });

  watch(
    stimmzettel,
    () => {
      if (!stimmzettel.value) return;

      const beschlussfassung = stimmzettel.value.beschlussfassung;
      if (beschlussfassung) {
        const unentschieden = beschlussfassung.pro === beschlussfassung.contra;

        abstimmungsergebnis.value = {
          stimmenDafuer: beschlussfassung.pro,
          stimmenDagegen: beschlussfassung.contra,
          hasWahlvorsteherVotedDafuer: unentschieden,
          abstimmungIsUnentschieden: unentschieden,
          abstimmungIsUngueltig: false,
        };
      } else {
        abstimmungsergebnis.value = {
          stimmenDafuer: null,
          stimmenDagegen: null,
          hasWahlvorsteherVotedDafuer: false,
          abstimmungIsUnentschieden: false,
          abstimmungIsUngueltig: false,
        };
      }

      beschlussDetails.value.isGueltig =
        isStimmzettelGueltigBasedOnVormerkungsgruenden(stimmzettel.value);
      _rebuildBeschlussgruende();
    },
    { immediate: true }
  );

  watch(
    () => beschlussDetails.value.isGueltig,
    () => _rebuildBeschlussgruende()
  );

  watchEffect(() => {
    const dafuer = abstimmungsergebnis.value.stimmenDafuer;
    const dagegen = abstimmungsergebnis.value.stimmenDagegen;

    const total = (dafuer ?? 0) + (dagegen ?? 0);
    const stimmenNotNull = dafuer != null && dagegen != null;

    const ungueltig =
      stimmenNotNull &&
      (total < 3 ||
        total > anwesendeWahlvorstandsmitgliederAnzahl.value ||
        dafuer < dagegen);

    const unentschieden = stimmenNotNull && !ungueltig && dafuer === dagegen;

    abstimmungsergebnis.value.abstimmungIsUngueltig = ungueltig;
    abstimmungsergebnis.value.abstimmungIsUnentschieden = unentschieden;

    beschlussDetails.value.andererGrundChecked =
      !!beschlussDetails.value.andererGrund;
  });

  function _rebuildBeschlussgruende() {
    const gruende =
      createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit(
        beschlussDetails.value.isGueltig,
        stimmzettel.value
      );
    beschlussDetails.value.andererGrund = gruende.andererGrund;
    beschlussDetails.value.beschlussgruende = gruende.beschlussgruende;
    beschlussDetails.value.beschlussText =
      _mergeAndReturnBeschlussText(gruende);
  }

  function _mergeAndReturnBeschlussText(gruende: {
    andererGrund: string;
    beschlussgruende: BeschlussgrundOption[];
  }) {
    const selectedGruende = gruende.beschlussgruende
      .filter((option) => option.selected)
      .map((option) => getBeschlussgrundEnumValueAsString(option.grund))
      .join(",");
    const andereGruende = beschlussDetails.value.andererGrundChecked
      ? gruende.andererGrund
      : "";

    return [selectedGruende, andereGruende].filter(Boolean).join(",");
  }

  return { abstimmungsergebnis, beschlussDetails };
}
