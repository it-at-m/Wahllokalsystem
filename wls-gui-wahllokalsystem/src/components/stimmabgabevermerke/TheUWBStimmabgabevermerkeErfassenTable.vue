<template>
  <v-container v-if="stimmabgabevermerke">
    <v-number-input
      v-model="rowSize"
      :rules="[REQUIRED, MIN_NUMBER(1), MAX_NUMBER(250)]"
      max-width="15rem"
      label="Anzahl der Blätter"
    />
    <v-btn @click="changeRowSize">change</v-btn>
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
          v-for="(data, index) in stimmabgabevermerkeTableData"
          :key="index"
        >
          <td
            v-for="columnNumber in tableColumns"
            :key="columnNumber"
          >
            <v-number-input v-model="data[columnNumber - 1]" />
          </td>
        </tr>
      </tbody>
    </v-table>
  </v-container>
</template>

<script setup lang="ts">
import type { Vermerke } from "@/types/stimmabgabevermerke/Vermerke.ts";

import { storeToRefs } from "pinia";
import { computed, onMounted, ref } from "vue";

import { StimmzettelDTOStimmzettelartEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useStimmabgabevermerkeStore } from "@/stores/stimmabgabevermerkeStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { MAX_NUMBER, MIN_NUMBER, REQUIRED } from "@/util/rules.ts";

const { stimmabgabevermerke } = storeToRefs(useStimmabgabevermerkeStore());
const { getWahlNameOrBlankStringById } = useWahlenStore();

onMounted(() => {
  mapperToTableData();
});

function mapperToTableData() {
  if (stimmabgabevermerke.value) {
    for (let rowNumber = 0; rowNumber < numberOfRows.value; rowNumber++) {
      const row: number[] = [];
      stimmabgabevermerke.value.wahldaten.forEach((wahldaten) => {
        row.push(wahldaten.vermerke[rowNumber].stimmzettel[0].anzahl);
      });
      stimmabgabevermerkeTableData.value.push(row);
    }
  }
}

function mapTableDataToEntity() {
  if (tableColumns.value && stimmabgabevermerke.value) {
    const numberOfRowsAtEnd = stimmabgabevermerkeTableData.value.length;
    for (let columNumber = 0; columNumber < tableColumns.value; columNumber++) {
      const stimmabgabevermerkeToAdd: Vermerke[] = [];
      for (let rowNumber = 0; rowNumber < numberOfRowsAtEnd; rowNumber++) {
        stimmabgabevermerkeToAdd.push({
          blattnummer: rowNumber,
          stimmzettel: [
            {
              anzahl:
                stimmabgabevermerkeTableData.value[columNumber][rowNumber] ?? 0,
              stimmzettelart: StimmzettelDTOStimmzettelartEnum.Klein,
            },
          ],
        });
      }
      stimmabgabevermerke.value.wahldaten[columNumber].vermerke = new Set(
        stimmabgabevermerkeToAdd
      );
    }
  }
}

const tableColumns = ref<number | undefined>(
  stimmabgabevermerke.value?.wahldaten.size
);

const rowSize = ref<number | null>(null);

const stimmabgabevermerkeTableData = ref<(number | null)[][]>([]);

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

function removeRowsFromEnd() {
  if (rowSize.value && tableColumns.value) {
    const numberOfRowsToRemove =
      stimmabgabevermerkeTableData.value.length - rowSize.value;
    stimmabgabevermerkeTableData.value.splice(-numberOfRowsToRemove);
  }
}

function addRows() {
  if (rowSize.value && tableColumns.value) {
    for (let rowNumber = 0; rowNumber < rowSize.value; rowNumber++) {
      const arr = [];
      for (
        let columnNumber = 0;
        columnNumber < tableColumns.value;
        columnNumber++
      ) {
        arr.push(null);
      }
      stimmabgabevermerkeTableData.value.push(arr);
    }
  }
}

function changeRowSize() {
  if (
    rowSize.value &&
    stimmabgabevermerkeTableData.value.length > rowSize.value
  ) {
    removeRowsFromEnd();
  } else {
    addRows();
  }
}
</script>

<style scoped></style>
