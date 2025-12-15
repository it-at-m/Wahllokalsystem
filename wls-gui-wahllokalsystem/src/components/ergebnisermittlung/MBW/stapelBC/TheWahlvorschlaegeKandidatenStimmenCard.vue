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
            <th class="font-weight-bold text-right">Gültige Stimmen</th>
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
              {{ totalSumErgebnisse }}
            </td>
          </tr>
        </tfoot>
      </v-table>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { Ref } from "vue";

import { computed, onMounted, ref } from "vue";

import BaseButtonFolding from "@/components/common/buttons/BaseButtonFolding.vue";
import BaseCardWahlvorschlagKandidatenStimmenErfassen from "@/components/ergebnisermittlung/MBW/stapelBC/BaseCardWahlvorschlagKandidatenStimmenErfassen.vue";
import { useErgebnisAndKandidatUtils } from "@/composables/ergebnismeldung/common/ergebnisAndKandidatUtils.ts";
import { useMwbStapelBCUtils } from "@/composables/ergebnismeldung/MBW/mwbStapelBCUtils.ts";

const COLUMN_COUNT_FULL_COL_SPAN = 4;

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

const expandedRows: Ref<(boolean | undefined)[]> = ref([]);

const COUNT_COLUMNS_BEFORE_SUM = 3;

onMounted(() => {
  loadWahlvorschlaegeAndErgebnisse();
});

const totalSumErgebnisse = computed(() => {
  return wahlvorschlaegeWithKandidatenErgebnissen.value.reduce(
    (sum, wahlvorschlag) =>
      sum + summeKandidatenStimmen(wahlvorschlag.kandidatenErgebnisse),
    0
  );
});

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
</style>
