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
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";
import type { PropType } from "vue";

import { computed } from "vue";

import { useOBWStapelCUtils } from "@/composables/ergebnisermittlung/obwStapelCUtils.ts";
import { useWahlvorschlagUtils } from "@/composables/wahlvorschlaege/wahlvorschlagUtils.ts";

const { getFirstKandidatNameOrEmptyString } = useWahlvorschlagUtils();

const props = defineProps({
  ergebnisseStapelCUngueltig: {
    type: Array as PropType<Ergebnis[]>,
    required: false,
    default: () => [],
  },
  ergebnisseStapelCGueltig: {
    type: Array as PropType<Ergebnis[]>,
    required: false,
    default: () => [],
  },
  wahlvorschlaege: {
    type: Array as PropType<Wahlvorschlag[]>,
    required: true,
  },
});

const {
  stapelCUngueltigErgebnisseSum,
  totalSum,
  wahlvorschlaegeAndSumAboveZero,
} = useOBWStapelCUtils(
  computed(() => props.wahlvorschlaege),
  computed(() => props.ergebnisseStapelCUngueltig),
  computed(() => props.ergebnisseStapelCGueltig)
);
</script>
