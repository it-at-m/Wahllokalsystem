<template>
  <v-table>
    <thead>
      <tr>
        <th class="index-column" />
        <th />
        <th class="font-weight-bold text-right">Insgesamt</th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td class="index-column">A1</td>
        <td>Wahlberechtigte ohne Sperrvermerk "W" (Wahlschein)</td>
        <td class="text-right">
          {{ wahlberechtigte.a1 }}
        </td>
      </tr>
      <tr>
        <td class="index-column">A2</td>
        <td>Wahlberechtigte mit Sperrvermerk "W" (Wahlschein)</td>
        <td class="text-right">
          {{ wahlberechtigte.a2 ?? 0 }}
        </td>
      </tr>
    </tbody>
    <tfoot>
      <tr>
        <td class="font-weight-bold index-column">A1 + A2</td>
        <td class="font-weight-bold">Wahlberechtigte</td>
        <td class="font-weight-bold text-right">
          {{ wahlberechtigte.a1 + (wahlberechtigte.a2 ?? 0) }}
        </td>
      </tr>
    </tfoot>
  </v-table>
</template>
<script setup lang="ts">
import type { AWerte } from "@/types/ergebnismeldung/common/AWerte.ts";
import type { PropType } from "vue";

defineProps({
  wahlberechtigte: {
    type: Object as PropType<AWerte>,
    required: true,
  },
});
</script>
