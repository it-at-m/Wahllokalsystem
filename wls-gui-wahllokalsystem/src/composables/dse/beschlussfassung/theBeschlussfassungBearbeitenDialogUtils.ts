import type { BeschlussAbstimmungsergebnis } from "@/types/dse/beschlussfassung/BeschlussAbstimmungsergebnis.ts";
import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";
import type { Ref } from "vue";

import { storeToRefs } from "pinia";
import { ref, watch, watchEffect } from "vue";

import { useWahlvorstandStore } from "@/stores/wahlvorstandStore.ts";

export function useTheBeschlussfassungBearbeitenDialogUtils(
  stimmzettel: Ref<PersistedStimmzettel | undefined>
) {
  const { anwesendeWahlvorstandsmitgliederAnzahl } = storeToRefs(
    useWahlvorstandStore()
  );

  const abstimmungsergebnis = ref<BeschlussAbstimmungsergebnis>({
    stimmenDafuer: null,
    stimmenDagegen: null,
    hasWahlvorsteherVotedDafuer: false,
    abstimmungIsUnentschieden: false,
    abstimmungIsUngueltig: false,
  });

  watch(
    stimmzettel,
    () => {
      const beschlussfassung = stimmzettel.value?.beschlussfassung;
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
    },
    { immediate: true }
  );

  watchEffect(() => {
    const dafuer = abstimmungsergebnis.value.stimmenDafuer;
    const dagegen = abstimmungsergebnis.value.stimmenDagegen;

    const total = (dafuer ?? 0) + (dagegen ?? 0);

    const ungueltig =
      dafuer == null || dagegen == null
        ? false
        : total < 3 ||
          total > anwesendeWahlvorstandsmitgliederAnzahl.value ||
          dafuer < dagegen;

    const unentschieden =
      dafuer != null && dagegen != null && !ungueltig && dafuer === dagegen;

    abstimmungsergebnis.value.abstimmungIsUngueltig = ungueltig;
    abstimmungsergebnis.value.abstimmungIsUnentschieden = unentschieden;
  });

  return { abstimmungsergebnis };
}
