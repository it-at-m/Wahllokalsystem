import type { BeschlussAbstimmungsergebnis } from "@/types/dse/beschlussfassung/BeschlussAbstimmungsergebnis.ts";
import type { BeschlussfassungDialogDetails } from "@/types/dse/beschlussfassung/BeschlussfassungDialogDetails.ts";
import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";
import type { Ref } from "vue";

import { storeToRefs } from "pinia";
import { computed, ref, watch, watchEffect } from "vue";

import { useBeschlussgrundTools } from "@/composables/dse/beschlussfassung/beschlussgrundTools.ts";
import { useTheBeschlussFassenTabUtils } from "@/composables/dse/beschlussfassung/theBeschlussFassenTabUtils.ts";
import { useWahlvorstandStore } from "@/stores/wahlvorstandStore.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";

export function useTheBeschlussfassungBearbeitenDialogUtils(
  stimmzettel: Ref<PersistedStimmzettel | undefined>
) {
  const { lastSavedAnwesendeWahlvorstandsmitgliederAnzahl } = storeToRefs(
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

  const stimmzettelGueltigkeitAusBeschluss = ref<StimmzettelGueltigkeitEnum>(
    StimmzettelGueltigkeitEnum.BeschlussAusstehend
  );

  const isBeschlussGefasst = computed(
    () =>
      stimmzettelGueltigkeitAusBeschluss.value !==
      StimmzettelGueltigkeitEnum.BeschlussAusstehend
  );

  const isBeschlussSpeichernButtonDisabled = computed(() => {
    const ergebnis = abstimmungsergebnis.value;
    const valuesSelected =
      beschlussDetails.value.beschlussgruende.some((grund) => grund.selected) ||
      beschlussDetails.value.andererGrundChecked;

    if (isBeschlussGefasst.value) {
      return (
        ergebnis.abstimmungIsUngueltig ||
        (ergebnis.abstimmungIsUnentschieden &&
          !ergebnis.hasWahlvorsteherVotedDafuer) ||
        ergebnis.stimmenDafuer == null ||
        ergebnis.stimmenDagegen == null
      );
    } else {
      return (
        !valuesSelected ||
        ergebnis.abstimmungIsUngueltig ||
        (ergebnis.abstimmungIsUnentschieden &&
          !ergebnis.hasWahlvorsteherVotedDafuer) ||
        ergebnis.stimmenDafuer == null ||
        ergebnis.stimmenDagegen == null
      );
    }
  });

  watch(
    stimmzettel,
    () => {
      if (!stimmzettel.value) return;

      _resetAbstimmungsergebnis();
      _resetBeschlussDetails(stimmzettel.value);
      stimmzettelGueltigkeitAusBeschluss.value =
        StimmzettelGueltigkeitEnum.BeschlussAusstehend;

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
        beschlussDetails.value.beschlussText = beschlussfassung.text;
        beschlussDetails.value.isGueltig =
          stimmzettel.value.gueltigkeit == StimmzettelGueltigkeitEnum.Valid;

        stimmzettelGueltigkeitAusBeschluss.value =
          stimmzettel.value.gueltigkeit;
      }

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
        dafuer < 1 ||
        dagegen < 0 ||
        total > lastSavedAnwesendeWahlvorstandsmitgliederAnzahl.value ||
        dafuer < dagegen);

    const unentschieden = stimmenNotNull && !ungueltig && dafuer === dagegen;

    abstimmungsergebnis.value.abstimmungIsUngueltig = ungueltig;
    abstimmungsergebnis.value.abstimmungIsUnentschieden = unentschieden;

    beschlussDetails.value.andererGrundChecked =
      !!beschlussDetails.value.andererGrund;

    if (!isBeschlussGefasst.value) {
      beschlussDetails.value.beschlussText = _mergeAndReturnBeschlussText();
    }
  });

  function _rebuildBeschlussgruende() {
    const gruende =
      createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit(
        beschlussDetails.value.isGueltig,
        stimmzettel.value
      );
    beschlussDetails.value.andererGrund = gruende.andererGrund;
    beschlussDetails.value.beschlussgruende = gruende.beschlussgruende;
  }

  function _mergeAndReturnBeschlussText() {
    const selectedGruende = beschlussDetails.value.beschlussgruende
      .filter((option) => option.selected)
      .map((option) => getBeschlussgrundEnumValueAsString(option.grund))
      .join(", ");
    const andereGruende = beschlussDetails.value.andererGrundChecked
      ? beschlussDetails.value.andererGrund
      : "";

    return [selectedGruende, andereGruende].filter(Boolean).join(", ");
  }

  function _resetAbstimmungsergebnis() {
    abstimmungsergebnis.value = {
      stimmenDafuer: null,
      stimmenDagegen: null,
      hasWahlvorsteherVotedDafuer: false,
      abstimmungIsUnentschieden: false,
      abstimmungIsUngueltig: false,
    };
  }

  function _resetBeschlussDetails(stimmzettel: PersistedStimmzettel) {
    beschlussDetails.value = {
      isGueltig: isStimmzettelGueltigBasedOnVormerkungsgruenden(stimmzettel),
      beschlussgruende: [],
      andererGrund: "",
      andererGrundChecked: false,
      beschlussText: "",
    };
  }

  return {
    abstimmungsergebnis,
    beschlussDetails,
    stimmzettelGueltigkeitAusBeschluss,
    isBeschlussSpeichernButtonDisabled,
    isBeschlussGefasst,
  };
}
