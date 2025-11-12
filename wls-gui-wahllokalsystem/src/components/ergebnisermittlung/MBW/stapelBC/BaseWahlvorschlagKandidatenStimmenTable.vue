<template>
  <v-table>
    <thead>
      <tr>
        <th class="colLfdNr">Lfd. Nr.</th>
        <th>Kandidatin/Kandidat</th>
        <th class="colScore">Schlusszahl</th>
      </tr>
    </thead>
    <tbody>
      <base-kandidat-row
        v-for="(ergebnisAndKandidat, index) in ergebnisseAndKandidaten"
        :key="index"
        v-model="ergebnisAndKandidat.ergebnis"
        :kandidat="ergebnisAndKandidat.kandidat"
        :wahlvorschlag-nummer="wahlvorschlagNummer"
      />
    </tbody>
    <tfoot>
      <tr>
        <td>Gesamtstimmenzahl</td>
        <td />
        <td>{{ totalScore }}</td>
      </tr>
    </tfoot>
  </v-table>
</template>

<script setup lang="ts">
import type { ErgebnisAndKandidat } from "@/types/ergebnisermittlung/ErgebnisAndKandidat.ts";

import { computed } from "vue";

import BaseKandidatRow from "@/components/ergebnisermittlung/MBW/stapelBC/BaseKandidatRow.vue";
import { useErgebnisAndKandidatUtils } from "@/composables/ergebnisermittlung/ergebnisAndKandidatUtils.ts";

const { summeKandidatenStimmen } = useErgebnisAndKandidatUtils();

const ergebnisseAndKandidaten = defineModel<ErgebnisAndKandidat[]>(
  "modelValue",
  {
    required: true,
  }
);

defineProps({
  wahlvorschlagNummer: {
    type: Number,
    required: true,
  },
});

const totalScore = computed(() =>
  summeKandidatenStimmen(ergebnisseAndKandidaten.value)
);
</script>

<style scoped>
.colLfdNr {
  width: 4em;
}

.colScore {
  width: 200px;
}
</style>
