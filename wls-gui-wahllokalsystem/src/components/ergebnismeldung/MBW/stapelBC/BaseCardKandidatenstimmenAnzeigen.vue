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
          <base-button-folding v-model="showDetails" />
        </v-col>
      </v-row>
      <div v-if="showDetails">
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
                class="px-0 align-top"
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
        :class="[showDetails ? 'top-border' : '']"
      >
        <v-row class="overflow-hidden">
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

import { onActivated, ref } from "vue";

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

.align-top {
  vertical-align: top;
}
</style>
