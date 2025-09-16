<template>
  <v-card>
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
              {{
                `${wahlvorschlagAndSum.wahlvorschlag.kurzname}, ${getFirstKandidatNameOrEmptyString(wahlvorschlagAndSum.wahlvorschlag)}`
              }}
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

const { getFirstKandidatNameOrEmptyString } = useWahlvorschlagUtils();

const props = defineProps({
  wahlID: {
    type: String,
    required: true,
  },
  wahlbezirkID: {
    type: String,
    required: true,
  },
});

const {
  stapelCUngueltigErgebnisseSum,
  totalSum,
  wahlvorschlaegeAndSumAboveZero,
} = useOBWStapelCUtils(
  computed(() => props.wahlID),
  computed(() => props.wahlbezirkID)
);
</script>
