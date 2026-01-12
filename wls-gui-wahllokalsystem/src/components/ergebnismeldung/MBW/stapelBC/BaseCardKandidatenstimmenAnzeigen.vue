<template>
  <v-card class="ma-3">
    <v-card-text class="px-0 pb-0 pt-2">
      <v-row align="center">
        <v-col cols="9">
          <h4 class="mx-4">Wahlvorschlag Nr. {{ wahlvorschlagNr }}</h4>
          <h5 class="text-grey-darken-1 mx-4">{{ wahlvorschlagName }}</h5>
        </v-col>
        <v-col
          cols="3"
          class="d-flex align-end flex-column"
        >
          <base-button-folding v-model="expandedRows[index]" />
        </v-col>
      </v-row>
      <v-table
        striped="odd"
        density="compact"
      >
        <tbody v-if="expandedRows[index]">
          <tr
            v-for="kandidatWithErgebnis in kandidatenergebnisse"
            :key="kandidatWithErgebnis.kandidat.identifikator"
          >
            <td>
              {{
                getKandidatLaufendeNummer(
                  wahlvorschlagNr,
                  kandidatWithErgebnis.kandidat.listenposition
                )
              }}
            </td>
            <td class="text-right">
              {{ kandidatWithErgebnis.ergebnis.ergebnis ?? 0 }}
            </td>
          </tr>
        </tbody>
        <tfoot>
          <tr>
            <td class="font-weight-bold">Gesamtstimmenzahl</td>
            <td class="font-weight-bold text-right">
              {{ summeKandidatenStimmen(kandidatenergebnisse) }}
            </td>
          </tr>
        </tfoot>
      </v-table>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { ErgebnisAndKandidat } from "@/types/ergebnismeldung/common/ErgebnisAndKandidat.ts";
import type { Ref } from "vue";

import { ref } from "vue";

import BaseButtonFolding from "@/components/common/buttons/BaseButtonFolding.vue";
import { useErgebnisAndKandidatUtils } from "@/composables/ergebnismeldung/common/ergebnisAndKandidatUtils.ts";
import { useWahlvorschlagUtils } from "@/composables/wahlvorschlaege/wahlvorschlagUtils.ts";

const { getKandidatLaufendeNummer } = useWahlvorschlagUtils();
const { summeKandidatenStimmen } = useErgebnisAndKandidatUtils();

const expandedRows: Ref<(boolean | undefined)[]> = ref([]);

defineProps<{
  kandidatenergebnisse: ErgebnisAndKandidat[];
  wahlvorschlagNr: number;
  wahlvorschlagName: string;
  laufendeNummer: string;
  stimmen: string;
  index: number;
}>();
</script>

<style scoped>
.v-table > .v-table__wrapper > table > tfoot > tr > td {
  background-color: #eeeeee;
  border-top: 0;
}

.v-table > .v-table__wrapper > table > tbody > tr:last-child td {
  border-bottom: 1px solid black;
}
</style>
