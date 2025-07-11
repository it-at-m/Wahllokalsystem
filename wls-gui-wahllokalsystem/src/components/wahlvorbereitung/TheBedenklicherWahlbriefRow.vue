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
        v-for="(rowIndex, index) in rows"
        :key="index"
      >
        <td>
          <v-row
            align="center"
            class="my-2"
            style="min-width: 250px"
          >
            {{ index + 1 }}
            <v-autocomplete
              model-value="tbd - wird berechnet"
              label="Beschlussergebnis"
              class="ml-5"
              hide-details
            />
          </v-row>
        </td>
        <td
          v-for="wahl in wahlen"
          :key="`${wahl.wahlID}-${index}`"
        >
          <v-autocomplete
            :model-value="_getCellContent(wahl, rowIndex)"
            label="Beschlussergebnis"
            :items="Object.keys(ZurueckweisungsgrundEnum)"
            hide-details
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
              @click="deleteBeanstandeteWahlbriefeRow(rowIndex)"
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

import { useBriefwahlStore } from "@/stores/briefwahlStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { ZurueckweisungsgrundEnum } from "@/types/briefwahl/ZurueckweisungsgrundEnum.ts";

const { wahlen } = storeToRefs(useWahlenStore());
const { rows } = storeToRefs(useBriefwahlStore());
const { deleteRow } = useBriefwahlStore();

const _getCellContent = (wahl: Wahl, rowIndex: number): string => {
  if (!wahl.beanstandeteWahlbriefe) return "";
  const content = wahl.beanstandeteWahlbriefe[rowIndex - 1];
  console.log(rowIndex + content);
  return content ? content : "";
};
const getIconForRowStatus = computed(() => {
  // todo: if form valid return check, else return pencil
});
const getIconColorForRowStatus = computed(() => {
  // todo: if icon is check return green, if is pencil return orange
});

// delete row + remove item from beanstandeteWahlbriefeList
function deleteBeanstandeteWahlbriefeRow(rowIndex: number) {
  wahlen.value!.map((wahl) => wahl.beanstandeteWahlbriefe!.splice(rowIndex, 1));
  deleteRow();
}
</script>

<style scoped>
td {
  text-align: center;
}
</style>
