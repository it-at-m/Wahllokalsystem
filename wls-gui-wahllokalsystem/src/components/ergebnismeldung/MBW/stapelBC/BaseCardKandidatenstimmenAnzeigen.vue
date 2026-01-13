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
      <div v-if="expandedRows[index]">
        <v-table
          striped="odd"
          density="compact"
          class="ma-0"
        >
          <tbody class="ma-10">
            <tr class="justify-start">
              <td
                v-for="(group, groupIndex) in groupedKandidatenByTabellenSpalte"
                :key="groupIndex"
                class="px-0"
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
                  </tbody>
                </v-table>
              </td>
            </tr>
          </tbody>
        </v-table>
      </div>
      <div
        class="bg-grey-lighten-3 font-weight-bold pa-4"
        :class="[expandedRows[index] ? 'top-border' : '']"
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
import type { Ref } from "vue";

import { onActivated, ref } from "vue";

import BaseButtonFolding from "@/components/common/buttons/BaseButtonFolding.vue";
import { useErgebnisAndKandidatUtils } from "@/composables/ergebnismeldung/common/ergebnisAndKandidatUtils.ts";
import { useWahlvorschlagUtils } from "@/composables/wahlvorschlaege/wahlvorschlagUtils.ts";

const { getKandidatLaufendeNummer } = useWahlvorschlagUtils();
const { summeKandidatenStimmen } = useErgebnisAndKandidatUtils();

const expandedRows: Ref<(boolean | undefined)[]> = ref([]);

const props = defineProps<{
  kandidatenergebnisse: ErgebnisAndKandidat[];
  wahlvorschlagNr: number;
  wahlvorschlagName: string;
  laufendeNummer: string;
  stimmen: string;
  index: number;
}>();

const groupedKandidatenByTabellenSpalte =
  ref<Record<number, ErgebnisAndKandidat[]>>();

onActivated(() => {
  groupedKandidatenByTabellenSpalte.value =
    groupKandidatenAndErgebnisseByTabellenSpalteInNiederschrift(
      props.kandidatenergebnisse
    );
});

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
</script>

<style scoped>
.top-border {
  border-top: 1px solid;
}
</style>
