<template>
  <v-container>
    <v-table striped="odd">
      <thead>
        <tr>
          <th />
          <th class="font-weight-bold text-left">Wahlvorschlag</th>
          <th class="font-weight-bold text-right">Insgesamt</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="vorschlag in sortedModelValue"
          :key="vorschlag.wahlvorschlag.identifikator"
        >
          <td>D {{ vorschlag.wahlvorschlag.ordnungszahl }}</td>
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
          <td>D</td>
          <td class="font-weight-bold">Gültige Stimmen insgesamt</td>
          <td class="font-weight-bold text-right">{{ totalSum }}</td>
        </tr>
      </tfoot>
    </v-table>
  </v-container>
</template>
<script setup lang="ts">
import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnisermittlung/MbwErgebnisseAndWahlvorschlag.ts";
import type { PropType } from "vue";

import { computed } from "vue";

const sortedModelValue = computed(() => {
  return [...modelValue.value].sort((a, b) => {
    return a.wahlvorschlag.ordnungszahl - b.wahlvorschlag.ordnungszahl;
  });
});

const modelValue = defineModel({
  type: Object as PropType<MbwErgebnisseAndWahlvorschlag[]>,
  required: true,
});

const totalSum = computed(() => {
  let total = 0;
  for (const vorschlag of modelValue.value) {
    total =
      total +
      (vorschlag.ergebnisStapelA.ergebnis ?? 0) +
      (vorschlag.ergebnisStapelB.ergebnis ?? 0);
  }
  return total;
});
</script>
