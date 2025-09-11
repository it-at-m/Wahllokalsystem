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
            v-for="a in wahlvorschlaegeToShow"
            :key="a.wahlvorschlag.identifikator"
          >
            <td>
              {{
                `${a.wahlvorschlag.kurzname}, ${getFirstKandidatNameOrEmptyString(a.wahlvorschlag)}`
              }}
            </td>
            <td>{{ a.sum }}</td>
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

const stapelCUngueltigErgebnisseSum = computed(() =>
  props.ergebnisseStapelCUngueltig.reduce(
    (sum, ergebnis) => sum + (ergebnis.ergebnis ?? 0),
    0
  )
);

const stapelCGueltigSums = computed(() =>
  props.ergebnisseStapelCGueltig.reduce(
    (sumOfWahlvorschlag: Map<string, number>, ergebnis) => {
      if (ergebnis.wahlvorschlagID !== null && ergebnis.ergebnis !== null) {
        const currentSum =
          sumOfWahlvorschlag.get(ergebnis.wahlvorschlagID) || 0;
        sumOfWahlvorschlag.set(
          ergebnis.wahlvorschlagID,
          currentSum + ergebnis.ergebnis
        );
      }
      return sumOfWahlvorschlag;
    },
    new Map<string, number>()
  )
);

const wahlvorschlaegeToShow = computed(() =>
  props.wahlvorschlaege
    .filter((wahlvorschlag) =>
      stapelCGueltigSums.value.has(wahlvorschlag.identifikator)
    )
    .map((wahlvorschlag) => ({
      wahlvorschlag,
      sum: stapelCGueltigSums.value.get(wahlvorschlag.identifikator) || 0,
    }))
);

const totalSum = computed(
  () =>
    stapelCUngueltigErgebnisseSum.value +
    [...stapelCGueltigSums.value.values()].reduce(
      (sum, currentValue) => sum + currentValue,
      0
    )
);
</script>
