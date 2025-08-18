<template>
  <v-container v-if="stimmabgabevermerke">
    <v-number-input
      v-model="rowSize"
      :rules="[REQUIRED, MIN_NUMBER(1), MAX_NUMBER(250)]"
      max-width="15rem"
      label="Anzahl der Blätter"
    />
    <v-btn @click="changeRowCount">change</v-btn>
    <v-table>
      <thead>
        <tr>
          <td
            v-for="wahldaten in stimmabgabevermerke.wahldaten"
            :key="wahldaten.wahlID"
          >
            {{ getWahlNameOrBlankStringById(wahldaten.wahlID) }}
          </td>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="number in numberOfRows"
          :key="number"
        >
          <td
            v-for="wahldaten in stimmabgabevermerke.wahldaten"
            :key="wahldaten.waehlerverzeichnisNummer"
          >
            <v-number-input
              v-model="wahldaten.vermerke[number - 1].stimmzettel[0].anzahl"
            />
          </td>
        </tr>
      </tbody>
    </v-table>
  </v-container>
</template>

<script setup lang="ts">
import type { Wahldaten } from "@/types/stimmabgabevermerke/Wahldaten.ts";

import { storeToRefs } from "pinia";
import { computed, ref } from "vue";

import { useStimmabgabevermerkeStore } from "@/stores/stimmabgabevermerkeStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { StimmzettelStimmzettelartEnum } from "@/types/stimmabgabevermerke/StimmzettelStimmzettelartEnum.ts";
import { MAX_NUMBER, MIN_NUMBER, REQUIRED } from "@/util/rules.ts";

const { stimmabgabevermerke } = storeToRefs(useStimmabgabevermerkeStore());
const { getWahlNameOrBlankStringById } = useWahlenStore();

const numberOfRows = computed(() => {
  const wahldatenIterator = stimmabgabevermerke.value?.wahldaten.values();
  if (!wahldatenIterator) {
    return 0;
  }
  let lowestVermerkeLength = Infinity;
  for (const wahldaten of wahldatenIterator) {
    const vermerkeLength = wahldaten.vermerke.length;
    if (vermerkeLength < lowestVermerkeLength) {
      lowestVermerkeLength = vermerkeLength;
    }
  }
  return lowestVermerkeLength === Infinity ? 0 : lowestVermerkeLength;
});

const rowSize = ref<number | null>(null);

function changeRowCount() {
  if (
    numberOfRows.value &&
    rowSize.value &&
    rowSize.value > numberOfRows.value
  ) {
    increaseRows();
  } else {
    decreaseRows();
  }
}

function increaseRows() {
  stimmabgabevermerke.value?.wahldaten.forEach((wahldaten: Wahldaten) => {
    if (rowSize.value && numberOfRows.value) {
      for (
        let rowNumber = numberOfRows.value;
        rowNumber < rowSize.value;
        rowNumber++
      ) {
        wahldaten.vermerke.push({
          blattnummer: rowNumber,
          stimmzettel: [
            {
              anzahl: 1,
              stimmzettelart: StimmzettelStimmzettelartEnum.Klein,
            },
          ],
        });
      }
    }
  });
}

function decreaseRows() {
  stimmabgabevermerke.value?.wahldaten.forEach((wahldaten: Wahldaten) => {
    if (rowSize.value && numberOfRows.value) {
      const removeRows = rowSize.value - numberOfRows.value;
      wahldaten.vermerke.splice(removeRows, removeRows * -1);
    }
  });
}
</script>

<style scoped></style>
