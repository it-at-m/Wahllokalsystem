<template>
  <v-table>
    <colgroup>
      <col style="width: 50px" />
      <col />
      <col style="width: 200px" />
    </colgroup>
    <thead>
      <tr>
        <td>Lfd. Nr.</td>
        <td>Kandidatin/Kandidat</td>
        <td>Schlusszahl</td>
      </tr>
    </thead>
    <tbody>
      <base-kandidat-row
        v-for="(ergebnisAndKandidat, index) in ergebnisseAndKandidaten"
        :key="index"
        v-model="ergebnisAndKandidat.ergebnis"
        :kandidat="ergebnisAndKandidat.kandidat"
        :wahlvorschlagNummer="wahlvorschlagNummer"
      />
    </tbody>
    <tfoot>
      <tr>
        <td>Gesamtstimmenzahl</td>
        <td />
        <td>0</td>
      </tr>
    </tfoot>
  </v-table>
</template>

<script setup lang="ts">
import type { ErgebnisAndKandidat } from "@/types/ergebnisermittlung/ErgebnisAndKandidat.ts";

import BaseKandidatRow from "@/components/ergebnisermittlung/MBW/kandidatenStimmen/BaseKandidatRow.vue";

const ergebnisseAndKandidaten = defineModel<ErgebnisAndKandidat[]>(
  "modelValue",
  {
    required: true,
  }
);

const props = defineProps({
  wahlvorschlagNummer: {
    type: Number,
    required: true,
  },
});
</script>

<style scoped></style>
