import type { BeschlussAbstimmungsergebnis } from "@/types/dse/beschlussfassung/BeschlussAbstimmungsergebnis.ts";
import type { BeschlussfassungDialogDetails } from "@/types/dse/beschlussfassung/BeschlussfassungDialogDetails.ts";
import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";
import type { Ref } from "vue";

import { storeToRefs } from "pinia";
import { computed, ref, watch, watchEffect } from "vue";

import { useBeschlussAbstimmungsergebnisTools } from "@/composables/dse/beschlussfassung/beschlussAbstimmungsergebnisTools.ts";
import { useBeschlussfassungDialogDetailsTools } from "@/composables/dse/beschlussfassung/beschlussfassungDialogDetailsTools.ts";
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
  const { createEmptyAbstimmungsergebnis } =
    useBeschlussAbstimmungsergebnisTools();
  const {
    createEmptyBeschlussfassungDialogDetails,
    mergeGruendeAndReturnBeschlusstext,
  } = useBeschlussfassungDialogDetailsTools();

  const abstimmungsergebnis = ref<BeschlussAbstimmungsergebnis>(
    createEmptyAbstimmungsergebnis()
  );

  const beschlussDetails = ref<BeschlussfassungDialogDetails>(
    createEmptyBeschlussfassungDialogDetails()
  );

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
    const isAnyGrundSelected =
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
        !isAnyGrundSelected ||
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
        beschlussDetails.value.isStimmzettelGueltig =
          stimmzettel.value.gueltigkeit == StimmzettelGueltigkeitEnum.Valid;

        stimmzettelGueltigkeitAusBeschluss.value =
          stimmzettel.value.gueltigkeit;
      }

      _rebuildBeschlussDetailsGruende();
    },
    { immediate: true }
  );

  watch(
    () => beschlussDetails.value.isStimmzettelGueltig,
    () => _rebuildBeschlussDetailsGruende()
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
      beschlussDetails.value.beschlussText = mergeGruendeAndReturnBeschlusstext(
        beschlussDetails.value
      );
    }
  });

  function _rebuildBeschlussDetailsGruende() {
    const gruende =
      createAndSetSelectedBeschlussgrundOptionsBasedOnStimmzettelAndGueltigkeit(
        beschlussDetails.value.isStimmzettelGueltig,
        stimmzettel.value
      );
    beschlussDetails.value.andererGrund = gruende.andererGrund;
    beschlussDetails.value.beschlussgruende = gruende.beschlussgruende;
  }

  function _resetAbstimmungsergebnis() {
    abstimmungsergebnis.value = createEmptyAbstimmungsergebnis();
  }

  function _resetBeschlussDetails(stimmzettel: PersistedStimmzettel) {
    beschlussDetails.value = createEmptyBeschlussfassungDialogDetails();
    beschlussDetails.value.isStimmzettelGueltig =
      isStimmzettelGueltigBasedOnVormerkungsgruenden(stimmzettel);
  }

  return {
    abstimmungsergebnis,
    beschlussDetails,
    stimmzettelGueltigkeitAusBeschluss,
    isBeschlussSpeichernButtonDisabled,
    isBeschlussGefasst,
  };
}
