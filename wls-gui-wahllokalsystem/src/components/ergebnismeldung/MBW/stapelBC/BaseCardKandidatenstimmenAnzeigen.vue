<template>
  <v-card class="ma-3">
    <v-card-text class="px-0 pb-0 pt-2">
      <v-row align="center">
        <v-col cols="9">
          <div class="font-weight-bold mx-4">
            Wahlvorschlag Nr. {{ wahlvorschlagNr }}
          </div>
          <div class="font-weight-bold smallText text-grey-darken-1 mx-4">
            {{ wahlvorschlagName }}
          </div>
        </v-col>
        <v-col
          cols="3"
          class="d-flex align-end flex-column"
        >
          <base-button-folding v-model="showDetails" />
        </v-col>
      </v-row>
      <v-row
        v-if="showDetails"
        class="flex-nowrap overflow-x-auto mt-0"
      >
        <v-col
          v-for="(group, groupIndex) in groupedKandidatenByTabellenSpalte"
          :key="groupIndex"
          class="px-0 pt-0"
        >
          <v-table
            striped="odd"
            density="compact"
          >
            <tbody>
              <tr
                v-for="kandidatWithErgebnis in group"
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
                <td class="text-right border-e-md">
                  {{ kandidatWithErgebnis.ergebnis.ergebnis ?? 0 }}
                </td>
              </tr>
              <tr
                v-for="row in getPlaceholderRows(
                  getMaxRows(groupedKandidatenByTabellenSpalte),
                  group.length
                )"
                :key="row"
              >
                <td />
                <td />
              </tr>
            </tbody>
            <tfoot
              v-if="Object.keys(groupedKandidatenByTabellenSpalte).length > 1"
            >
              <tr>
                <td class="border-b-sm top-border">
                  <!-- KandidatLaufendeNummer -->
                </td>
                <td
                  class="font-weight-bold text-right border-e-md border-b-sm top-border"
                >
                  {{
                    group.reduce((sum, kandidat) => {
                      return sum + (kandidat.ergebnis.ergebnis ?? 0);
                    }, 0)
                  }}
                </td>
              </tr>
            </tfoot>
          </v-table>
        </v-col>
      </v-row>
      <div
        class="bg-grey-lighten-3 font-weight-bold pa-4"
        :class="[showDetails ? 'top-border' : '']"
      >
        <v-row>
          <v-col> Gesamtstimmenzahl </v-col>
          <v-col class="text-right">
            {{ summeKandidatenStimmen(kandidatenergebnisse) }}
          </v-col>
        </v-row>
      </div>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { ErgebnisAndKandidat } from "@/types/ergebnismeldung/common/ErgebnisAndKandidat.ts";

import { computed, ref } from "vue";

import BaseButtonFolding from "@/components/common/buttons/BaseButtonFolding.vue";
import { useErgebnisAndKandidatUtils } from "@/composables/ergebnismeldung/common/ergebnisAndKandidatUtils.ts";
import { useWahlvorschlagUtils } from "@/composables/wahlvorschlaege/wahlvorschlagUtils.ts";

const { getKandidatLaufendeNummer } = useWahlvorschlagUtils();
const { summeKandidatenStimmen } = useErgebnisAndKandidatUtils();

const showDetails = ref<boolean>(false);

const props = defineProps<{
  kandidatenergebnisse: ErgebnisAndKandidat[];
  wahlvorschlagNr: number;
  wahlvorschlagName: string;
}>();

const groupedKandidatenByTabellenSpalte = computed(() =>
  groupKandidatenAndErgebnisseByTabellenSpalteInNiederschrift(
    props.kandidatenergebnisse
  )
);

function groupKandidatenAndErgebnisseByTabellenSpalteInNiederschrift(
  kandidatenergebnisse: ErgebnisAndKandidat[]
) {
  return kandidatenergebnisse.reduce(
    (grouped, current) => {
      const tabellenSpalte = current.kandidat.tabellenSpalteInNiederschrift;

      if (!grouped[tabellenSpalte]) {
        grouped[tabellenSpalte] = [];
      }
      grouped[tabellenSpalte].push(current);

      return grouped;
    },
    {} as Record<number, ErgebnisAndKandidat[]>
  );
}

function getMaxRows(
  groupedKandidatenByTabellenSpalte: Record<number, ErgebnisAndKandidat[]>
) {
  return Object.values(groupedKandidatenByTabellenSpalte).reduce(
    (max, kandidatenArray) => {
      return Math.max(max, kandidatenArray.length);
    },
    0
  );
}

function getPlaceholderRows(maxRows: number, filledRows: number) {
  return maxRows - filledRows;
}
</script>

<style scoped>
.top-border {
  border-top: 1px solid;
}

.smallText {
  font-size: 12px;
}
</style>
