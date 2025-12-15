<template>
  <v-table>
    <thead>
      <tr>
        <th class="index-column" />
        <th class="font-weight-bold text-left">Wahlvorschlag</th>
        <th class="font-weight-bold text-right">Insgesamt</th>
      </tr>
    </thead>
    <tbody>
      <tr
        v-for="vorschlag in sortedModelValue"
        :key="vorschlag.wahlvorschlag.identifikator"
      >
        <td class="index-column">
          D {{ vorschlag.wahlvorschlag.ordnungszahl }}
        </td>
        <td>{{ vorschlag.wahlvorschlag.kurzname }}</td>
        <td class="text-right">
          {{
            (vorschlag.ergebnisStapelA.ergebnis ?? 0) +
            (vorschlag.ergebnisStapelB.ergebnis ?? 0)
          }}
        </td>
      </tr>
    </tbody>
    <tfoot>
      <tr>
        <td class="font-weight-bold index-column">D</td>
        <td class="font-weight-bold">Gültige Stimmen</td>
        <td class="font-weight-bold text-right">{{ totalSum }}</td>
      </tr>
    </tfoot>
  </v-table>
</template>
<script setup lang="ts">
import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnisermittlung/MbwErgebnisseAndWahlvorschlag.ts";
import type { PropType } from "vue";

import { computed } from "vue";

import { useWahlvorschlagUtils } from "@/composables/wahlvorschlaege/wahlvorschlagUtils.ts";

const { sortMbwErgebnisseAndWahlvorschlagByOrdnungszahl } =
  useWahlvorschlagUtils();

const props = defineProps({
  ergebnisseAndWahlvorschlaege: {
    type: Array as PropType<MbwErgebnisseAndWahlvorschlag[]>,
    required: true,
  },
});

const sortedModelValue = computed(() => {
  return sortMbwErgebnisseAndWahlvorschlagByOrdnungszahl(
    props.ergebnisseAndWahlvorschlaege
  );
});

const totalSum = computed(() => {
  let total = 0;
  for (const vorschlag of props.ergebnisseAndWahlvorschlaege) {
    total =
      total +
      (vorschlag.ergebnisStapelA.ergebnis ?? 0) +
      (vorschlag.ergebnisStapelB.ergebnis ?? 0);
  }
  return total;
});
</script>
