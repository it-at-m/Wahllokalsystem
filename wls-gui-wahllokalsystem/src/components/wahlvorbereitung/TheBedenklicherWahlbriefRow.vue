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
        v-for="(rowIndex, index) in _getMaxRows"
        :key="index"
      >
        <td>{{ index + 1 }}. Platzhalter</td>
        <td
          v-for="wahl in wahlen"
          :key="`${wahl.wahlID}-${index}`"
        >
          {{ _getCellContent(wahl, rowIndex) }}
        </td>
        <td>
          <v-row
            align="center"
            justify="space-between"
            class="px-2"
          >
            <v-btn
              icon="$delete"
              size="x-small"
            />
            <v-btn
              icon="$save"
              size="x-small"
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
import { VBtn, VRow, VTable } from "vuetify/components";

import { useWahlenStore } from "@/stores/wahlenStore.ts";

const { wahlen } = storeToRefs(useWahlenStore());

const _getMaxRows = computed(() => {
  // find wahl with most beanstandeteWahlbriefe entries
  const maxLength = Math.max(
    ...wahlen.value!.map((w) => w.beanstandeteWahlbriefe?.length ?? 0)
  );
  return Array.from({ length: maxLength }, (_, i) => i);
});

const _getCellContent = (wahl: Wahl, rowIndex: number): string => {
  if (!wahl.beanstandeteWahlbriefe) return "";
  const content = wahl.beanstandeteWahlbriefe[rowIndex];
  return content ? content : "";
};
</script>

<style scoped>
td {
  text-align: center;
}
</style>
