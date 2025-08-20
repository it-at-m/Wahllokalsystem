<template>
  <v-container v-if="stimmabgabevermerke">
    <div class="d-flex">
      <v-number-input
        v-model="rowSize"
        :rules="[REQUIRED, MIN_NUMBER(1), MAX_NUMBER(250)]"
        max-width="15rem"
        label="Anzahl der Blätter"
      />
      <v-btn
        class="ml-4 mt-3"
        primary
        @click="changeRowCount"
        :disabled="disableChangeRowSizeButton"
        >{{ changeRowSizeButtonText }}</v-btn
      >
    </div>
    <v-table>
      <thead>
        <tr>
          <td>Blatt</td>
          <td
            v-for="wahldaten in stimmabgabevermerke.wahldaten"
            :key="wahldaten.wahlID"
          >
            {{ getWahlNameOrBlankStringById(wahldaten.wahlID) }}
          </td>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>Nr. 1</td>
          <td
            v-for="wahldaten in stimmabgabevermerke.wahldaten"
            :key="wahldaten.wahlID"
          >
            <v-text-field
              disabled
              label="Beurkundung"
            />
          </td>
        </tr>
        <tr
          v-for="number in lowestNumberOfRowsOverAllWahldaten"
          :key="number"
        >
          <td>Nr. {{ number + 1 }}</td>
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
import { computed, onMounted, ref } from "vue";

import { useStimmabgabevermerkeStore } from "@/stores/stimmabgabevermerkeStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { StimmzettelStimmzettelartEnum } from "@/types/stimmabgabevermerke/StimmzettelStimmzettelartEnum.ts";
import { MAX_NUMBER, MIN_NUMBER, REQUIRED } from "@/util/rules.ts";

const { stimmabgabevermerke } = storeToRefs(useStimmabgabevermerkeStore());
const { getWahlNameOrBlankStringById } = useWahlenStore();

onMounted(() => {
  console.log(stimmabgabevermerke.value.wahldaten.values().next().value);
  rowSize.value =
    stimmabgabevermerke.value.wahldaten.values().next().value.vermerke.length +
    1;
});

const lowestNumberOfRowsOverAllWahldaten = computed(() => {
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

const changeRowSizeButtonText = computed(() => {
  if (
    rowSize.value != null &&
    rowSize.value - 1 > lowestNumberOfRowsOverAllWahldaten.value
  ) {
    return "Erhöhen";
  } else if (
    rowSize.value != null &&
    rowSize.value - 1 < lowestNumberOfRowsOverAllWahldaten.value
  ) {
    return "Reduzieren";
  } else {
    return "Übernehmen";
  }
});

const disableChangeRowSizeButton = computed(() => {
  return rowSize.value == null || rowSize.value <= 0;
});

function changeRowCount() {
  if (
    lowestNumberOfRowsOverAllWahldaten.value != null &&
    rowSize.value != null &&
    rowSize.value - 1 > lowestNumberOfRowsOverAllWahldaten.value
  ) {
    increaseRows();
  } else {
    decreaseRows();
  }
}

function increaseRows() {
  stimmabgabevermerke.value?.wahldaten.forEach((wahldaten: Wahldaten) => {
    if (
      rowSize.value != null &&
      lowestNumberOfRowsOverAllWahldaten.value != null
    ) {
      for (
        let rowNumber = lowestNumberOfRowsOverAllWahldaten.value;
        rowNumber < rowSize.value - 1;
        rowNumber++
      ) {
        wahldaten.vermerke.push({
          blattnummer: rowNumber + 1,
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
  const currentLowestNumberOfrowsOverAllWahldaten =
    lowestNumberOfRowsOverAllWahldaten.value;
  stimmabgabevermerke.value?.wahldaten.forEach((wahldaten: Wahldaten) => {
    if (
      rowSize.value != null &&
      rowSize.value > 0 &&
      currentLowestNumberOfrowsOverAllWahldaten
    ) {
      const removeRows =
        rowSize.value - currentLowestNumberOfrowsOverAllWahldaten - 1;
      wahldaten.vermerke.splice(removeRows, removeRows * -1);
    }
  });
}
</script>

<style scoped></style>
