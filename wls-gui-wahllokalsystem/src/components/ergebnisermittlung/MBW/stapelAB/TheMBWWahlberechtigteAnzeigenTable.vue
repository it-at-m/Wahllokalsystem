<template>
  <v-container>
    <v-table>
      <tbody>
        <tr>
          <td>A1</td>
          <td>Wahlberechtigte ohne Sperrvermerk "W" (Wahlschein)</td>
          <td class="text-right">
            {{ wahlberechtigte.a1 }}
          </td>
        </tr>
        <tr>
          <td>A2</td>
          <td>Wahlberechtigte mit Sperrvermerk "W" (Wahlschein)</td>
          <td class="text-right">
            {{ wahlberechtigte.a2 }}
          </td>
        </tr>
      </tbody>
      <tfoot>
        <tr>
          <td class="font-weight-bold">A1 + A2</td>
          <td class="font-weight-bold">Wahlberechtigte insgesamt</td>
          <td class="font-weight-bold text-right">
            {{ wahlberechtigte.a1 + (wahlberechtigte.a2 ?? 0) }}
          </td>
        </tr>
      </tfoot>
    </v-table>
  </v-container>
</template>
<script setup lang="ts">
import type { AWerte } from "@/types/ergebnisermittlung/AWerte.ts";
import type { PropType } from "vue";

defineProps({
  wahlberechtigte: {
    type: Object as PropType<AWerte>,
    required: true,
  },
});
</script>
