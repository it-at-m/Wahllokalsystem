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
            v-for="(wahlvorschlag, index) in scorableWahlvorschlaege"
            :key="index"
          >
            <tr>
              <td>
                <base-button-folding v-model="expandedRows[index]" />
              </td>
              <td>D{{ wahlvorschlag.ordnungszahl }}</td>
              <td>{{ wahlvorschlag.kurzname }}</td>
              <td class="text-right">
                {{ summeKandidatenStimmen(wahlvorschlag.scorableKandidaten) }}
              </td>
            </tr>
            <tr v-if="expandedRows[index]">
              <td :colspan="COLUMN_COUNT_FULL_COL_SPAN">
                <base-card-wahlvorschlag-kandidaten-stimmen-update
                  :model-value="wahlvorschlag"
                  :is-saving="isSaving"
                  @do-save="onSaveWahlvorschlag"
                />
              </td>
            </tr>
          </template>
        </tbody>
      </v-table>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { Ref } from "vue";

import { onMounted, ref } from "vue";

import BaseButtonFolding from "@/components/common/buttons/BaseButtonFolding.vue";
import BaseCardWahlvorschlagKandidatenStimmenUpdate from "@/components/ergebnisermittlung/MBW/stapelBC/BaseCardWahlvorschlagKandidatenStimmenUpdate.vue";
import { useErgebnisAndKandidatUtils } from "@/composables/ergebnisermittlung/ergebnisAndKandidatUtils.ts";
import { useMwbStapelBCUtils } from "@/composables/ergebnisermittlung/mwbStapelBCUtils.ts";

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
  scorableWahlvorschlaege,
  loadWahlvorschlaegeAndErgebnisse,
  saveErgebnisse,
} = useMwbStapelBCUtils(props.wahlbezirkID, props.wahlID);
const { summeKandidatenStimmen } = useErgebnisAndKandidatUtils();

const expandedRows: Ref<(boolean | undefined)[]> = ref([]);

onMounted(() => {
  loadWahlvorschlaegeAndErgebnisse();
});

function onSaveWahlvorschlag() {
  saveErgebnisse();
}
</script>
