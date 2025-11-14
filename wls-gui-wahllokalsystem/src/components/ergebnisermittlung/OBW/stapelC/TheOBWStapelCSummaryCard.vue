<template>
  <v-card>
    <v-card-title> Beschlussergebnis </v-card-title>
    <v-card-text>
      <v-table>
        <thead>
          <tr>
            <th class="font-weight-bold">Wahlvorschlag</th>
            <th class="font-weight-bold">Anzahl</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="stapelCUngueltigErgebnisseSum">
            <td>Ungültig</td>
            <td>{{ stapelCUngueltigErgebnisseSum }}</td>
          </tr>
          <tr
            v-for="wahlvorschlagAndSum in wahlvorschlaegeAndSumAboveZero"
            :key="wahlvorschlagAndSum.wahlvorschlag.identifikator"
          >
            <td>
              {{ getWahlvorschlagTitle(wahlvorschlagAndSum.wahlvorschlag) }}
            </td>
            <td>{{ wahlvorschlagAndSum.sum }}</td>
          </tr>
        </tbody>
        <tfoot>
          <tr>
            <td class="font-weight-bold">Gesamt</td>
            <td class="font-weight-bold">
              {{ totalSum }}
            </td>
          </tr>
        </tfoot>
      </v-table>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { computed } from "vue";

import { useOBWStapelCUtils } from "@/composables/ergebnisermittlung/obwStapelCUtils.ts";
import { useWahlvorschlagUtils } from "@/composables/wahlvorschlaege/wahlvorschlagUtils.ts";

const { getWahlvorschlagTitle } = useWahlvorschlagUtils();

const props = defineProps({
  wahlId: {
    type: String,
    required: true,
  },
  wahlbezirkId: {
    type: String,
    required: true,
  },
});

const {
  stapelCUngueltigErgebnisseSum,
  totalSum,
  wahlvorschlaegeAndSumAboveZero,
} = useOBWStapelCUtils(
  computed(() => props.wahlId),
  computed(() => props.wahlbezirkId)
);
</script>
