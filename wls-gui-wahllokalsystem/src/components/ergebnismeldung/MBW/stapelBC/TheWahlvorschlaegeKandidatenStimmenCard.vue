<template>
  <v-card>
    <v-card-title>Übersicht des Auszählungsergebnisses</v-card-title>
    <v-card-text>
      <v-skeleton-loader
        v-if="isLoading"
        type="table"
      />
      <v-table v-else>
        <thead>
          <tr>
            <th><!-- fold/expand action --></th>
            <th><!-- Ordnungszahl --></th>
            <th class="font-weight-bold">Wahlvorschlag</th>
            <th
              colspan="3"
              style="text-align: center"
              class="font-weight-bold"
            >
              Gültige Stimmzettel
            </th>
            <th class="font-weight-bold text-right">Gültige Stimmen</th>
          </tr>
          <tr>
            <th><!-- fold/expand action --></th>
            <th><!-- Ordnungszahl --></th>
            <th><!-- Wahlvorschlag --></th>
            <th class="font-weight-bold smallText">
              Wahlvorschlag unverändert gekennzeichnet
            </th>
            <th class="font-weight-bold smallText">
              Innerhalb eines Wahlvorschlag verändert
            </th>
            <th class="font-weight-bold smallText">
              Gültige Stimmzettel für genau einen Wahlvorschlag
            </th>
            <th class="font-weight-bold smallText">
              Gültig kumulierte und panaschierte insgesamt
            </th>
          </tr>
        </thead>
        <tbody>
          <template
            v-for="(
              wahlvorschlag, index
            ) in wahlvorschlaegeWithKandidatenErgebnissen"
            :key="index"
          >
            <tr>
              <td class="foldingButtonColumn">
                <base-button-folding v-model="expandedRows[index]" />
              </td>
              <td class="ordnungszahlColumn">
                D{{ wahlvorschlag.ordnungszahl }}
              </td>
              <td>{{ wahlvorschlag.kurzname }}</td>
              <td class="text-right">
                {{ getStapelAErgebnisForWahlvorschlagIndex(index) }}
              </td>
              <td class="text-right">
                {{ getStapelBErgebnisForWahlvorschlagIndex(index) }}
              </td>
              <td class="text-right">
                {{
                  getStapelAErgebnisForWahlvorschlagIndex(index) +
                  getStapelBErgebnisForWahlvorschlagIndex(index)
                }}
              </td>
              <td class="text-right">
                {{ summeKandidatenStimmen(wahlvorschlag.kandidatenErgebnisse) }}
              </td>
            </tr>
            <tr v-if="expandedRows[index]">
              <td :colspan="COLUMN_COUNT_FULL_COL_SPAN">
                <base-card-wahlvorschlag-kandidaten-stimmen-erfassen
                  :model-value="wahlvorschlag"
                  :is-saving="isSaving"
                  @do-save="onSaveWahlvorschlag"
                />
              </td>
            </tr>
          </template>
        </tbody>
        <tfoot>
          <tr>
            <td
              :colspan="COUNT_COLUMNS_BEFORE_SUM"
              class="font-weight-bold"
            >
              Gültige Stimmen insgesamt
            </td>
            <td class="font-weight-bold text-right">
              {{ totalSumUnveraendert }}
            </td>
            <td class="font-weight-bold text-right">
              {{ totalSumVeraendert }}
            </td>
            <td class="font-weight-bold text-right">
              {{ totalSumUnveraendert + totalSumVeraendert }}
            </td>
            <td class="font-weight-bold text-right">
              {{ totalSumErgebnisse }}
            </td>
          </tr>
        </tfoot>
      </v-table>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnisermittlung/MbwErgebnisseAndWahlvorschlag.ts";
import type { Ref } from "vue";

import { computed, onActivated, ref } from "vue";

import BaseButtonFolding from "@/components/common/buttons/BaseButtonFolding.vue";
import BaseCardWahlvorschlagKandidatenStimmenErfassen from "@/components/ergebnismeldung/MBW/stapelBC/BaseCardWahlvorschlagKandidatenStimmenErfassen.vue";
import { useErgebnisAndKandidatUtils } from "@/composables/ergebnismeldung/common/ergebnisAndKandidatUtils.ts";
import { useMbwUtils } from "@/composables/ergebnismeldung/MBW/mbwUtils.ts";
import { useMwbStapelBCUtils } from "@/composables/ergebnismeldung/MBW/mwbStapelBCUtils.ts";

const COLUMN_COUNT_FULL_COL_SPAN = 7;

const props = defineProps({
  wahlbezirkID: {
    type: String,
    required: true,
  },
  wahlID: {
    type: String,
    required: true,
  },
});

const {
  isLoading,
  isSaving,
  wahlvorschlaegeWithKandidatenErgebnissen,
  loadWahlvorschlaegeAndErgebnisse,
  saveErgebnisse,
} = useMwbStapelBCUtils(props.wahlbezirkID, props.wahlID);
const { summeKandidatenStimmen } = useErgebnisAndKandidatUtils();
const { loadAndCombineErgebnisseAndWahlvorschlaege } = useMbwUtils(
  props.wahlID,
  props.wahlbezirkID
);

const ergebnisseAndWahlvorschlaege = ref<MbwErgebnisseAndWahlvorschlag[]>([]);
const expandedRows: Ref<(boolean | undefined)[]> = ref([]);

const COUNT_COLUMNS_BEFORE_SUM = 3;

onActivated(async () => {
  await loadWahlvorschlaegeAndErgebnisse();
  ergebnisseAndWahlvorschlaege.value =
    await loadAndCombineErgebnisseAndWahlvorschlaege();
});

const totalSumErgebnisse = computed(() => {
  return wahlvorschlaegeWithKandidatenErgebnissen.value.reduce(
    (sum, wahlvorschlag) =>
      sum + summeKandidatenStimmen(wahlvorschlag.kandidatenErgebnisse),
    0
  );
});

const totalSumUnveraendert = computed(() => {
  return ergebnisseAndWahlvorschlaege.value.reduce(
    (sum, ergebnis) => sum + (ergebnis.ergebnisStapelA.ergebnis ?? 0),
    0
  );
});

const totalSumVeraendert = computed(() => {
  return ergebnisseAndWahlvorschlaege.value.reduce(
    (sum, ergebnis) => sum + (ergebnis.ergebnisStapelB.ergebnis ?? 0),
    0
  );
});

function getStapelAErgebnisForWahlvorschlagIndex(index: number) {
  return (
    ergebnisseAndWahlvorschlaege.value[index]?.ergebnisStapelA.ergebnis ?? 0
  );
}

function getStapelBErgebnisForWahlvorschlagIndex(index: number) {
  return (
    ergebnisseAndWahlvorschlaege.value[index]?.ergebnisStapelB.ergebnis ?? 0
  );
}

function onSaveWahlvorschlag() {
  saveErgebnisse();
}
</script>

<style scoped>
.foldingButtonColumn {
  width: 48px;
}
.ordnungszahlColumn {
  width: 5em;
}
.smallText {
  font-size: 12px !important;
  text-align: right !important;
}
</style>
