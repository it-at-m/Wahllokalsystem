<template>
  <v-container>
    <v-table>
      <thead>
        <tr>
          <th />
          <th class="font-weight-bold text-center">Wahlvorschlag</th>
          <th class="font-weight-bold text-center">
            Stapel a: Wahlvorschlag unverändert gekennzeichnet
          </th>
          <th class="font-weight-bold text-center">
            Stapel b: Innerhalb eines Wahlvorschlags verändert
          </th>
          <th class="font-weight-bold text-center">Insgesamt</th>
        </tr>
      </thead>
      <!-- todo: taborder -->
      <tbody>
        <tr
          v-for="(vorschlag, index) in modelValue"
          :key="index"
        >
          <td>
            {{ vorschlag.wahlvorschlag.identifikator }}
          </td>
          <td>{{ vorschlag.wahlvorschlag.kurzname }}</td>
          <td>
            <base-number-input v-model="vorschlag.ergebnisStapelA.ergebnis" />
          </td>
          <td>
            <base-number-input v-model="vorschlag.ergebnisStapelB.ergebnis" />
          </td>
          <td>
            {{
              (vorschlag.ergebnisStapelA.ergebnis ?? 0) +
              (vorschlag.ergebnisStapelB.ergebnis ?? 0)
            }}
          </td>
        </tr>
      </tbody>
      <tfoot>
        <tr>
          <td />
          <td class="font-weight-bold">Gültige Stimmen insgesamt</td>
          <td />
          <td />
          <td class="font-weight-bold">{{ totalSum }}</td>
        </tr>
      </tfoot>
    </v-table>
  </v-container>
</template>
<script setup lang="ts">
import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnisermittlung/MbwErgebnisseAndWahlvorschlag.ts";
import type { PropType } from "vue";

import { computed } from "vue";

import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";

const modelValue = defineModel({
  type: Object as PropType<MbwErgebnisseAndWahlvorschlag[]>,
  required: true,
});

defineProps({
  wahlID: {
    type: String,
    required: true,
  },
  wahlbezirkID: {
    type: String,
    required: true,
  },
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
