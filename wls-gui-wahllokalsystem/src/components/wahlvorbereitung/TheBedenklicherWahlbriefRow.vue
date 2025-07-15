<template>
  <v-table>
    <thead>
      <tr>
        <th class="font-weight-bold text-center">Wahlschein</th>
        <th
          v-for="wahl in wahlen"
          :key="wahl.wahlID"
          class="font-weight-bold text-center"
        >
          Stimmzettelumschlag für {{ wahl.name }}
        </th>
      </tr>
    </thead>

    <tbody>
      <tr
        v-for="index in maxRows"
        :key="index"
      >
        <td>
          <v-row
            align="center"
            class="my-2"
            style="min-width: 250px"
          >
            {{ index - 1 }}
            <v-autocomplete
              model-value="tbd - wird berechnet"
              label="Beschlussergebnis"
              class="ml-5"
              :items="gruendeWahlscheine"
              hide-details
            />
          </v-row>
        </td>
        <td
          v-for="wahl in wahlen"
          :key="`${wahl.wahlID}-${index - 1}`"
        >
          <!-- todo: man sieht nur schlecht dass man scrollen kann -->
          <v-autocomplete
            :model-value="wahl.beanstandeteWahlbriefe![index - 1]"
            label="Beschlussergebnis"
            :items="gruendeStimmzettel"
            hide-details
            @update:model-value="
              (value) => onZulassungsgrundChanged(wahl, value, index - 1)
            "
          />
        </td>
        <td>
          <v-row
            align="center"
            justify="space-between"
            class="px-2"
            style="min-width: 115px"
          >
            <v-btn
              icon="$delete"
              variant="text"
              @click="deleteBeanstandeteWahlbriefeRow(index - 1)"
            />
            <v-btn
              icon="$edit"
              variant="text"
            />
          </v-row>
        </td>
      </tr>
    </tbody>
  </v-table>
</template>

<script setup lang="ts">
import type { Wahl } from "@/types/wahl/Wahl.ts";

import { storeToRefs } from "pinia";
import { computed } from "vue";
import { VAutocomplete, VBtn, VRow, VTable } from "vuetify/components";

import { useWahlenStore } from "@/stores/wahlenStore.ts";
import {
  gruendeStimmzettel,
  gruendeWahlscheine,
  stringToEnumValue,
} from "@/types/briefwahl/ZurueckweisungsgrundEnum.ts";

const { wahlen } = storeToRefs(useWahlenStore());
const { getWahlOrUndefinedById } = useWahlenStore();

const maxRows = computed(() => {
  return Math.max(
    ...wahlen.value!.map((wahl) => wahl.beanstandeteWahlbriefe!.length)
  );
});

const getIconForRowStatus = computed(() => {
  // todo: if form valid return check, else return pencil
});
const getIconColorForRowStatus = computed(() => {
  // todo: if icon is check return green, if is pencil return orange
});

function onZulassungsgrundChanged(
  column: Wahl,
  newValue: string,
  rowIndex: number
) {
  const wahl = getWahlOrUndefinedById(column.wahlID);
  wahl!.beanstandeteWahlbriefe![rowIndex] = stringToEnumValue(newValue);
}

// delete row + remove item from beanstandeteWahlbriefeList
function deleteBeanstandeteWahlbriefeRow(rowIndex: number) {
  wahlen.value!.map((wahl) => wahl.beanstandeteWahlbriefe!.splice(rowIndex, 1));
}
</script>

<style scoped>
td {
  text-align: center;
}
</style>
