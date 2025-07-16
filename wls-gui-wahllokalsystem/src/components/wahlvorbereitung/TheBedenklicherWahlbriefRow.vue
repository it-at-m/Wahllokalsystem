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
              :model-value="wahlscheinGrund[index - 1]"
              label="Beschlussergebnis"
              class="ml-5"
              :items="gruendeWahlscheine"
              hide-details
              :rules="[REQUIRED]"
              @update:model-value="
                (value) => onZulassungsgrundChanged(value, index - 1)
              "
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
            :rules="[REQUIRED]"
            @update:model-value="
              (value) => onZulassungsgrundChanged(value, index - 1, wahl)
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
            <v-icon
              :icon="rowIcon[index - 1]"
              variant="text"
              :color="rowColor[index - 1]"
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
import { computed, onMounted, ref, watch } from "vue";
import { VAutocomplete, VBtn, VIcon, VRow, VTable } from "vuetify/components";

import { useWahlenStore } from "@/stores/wahlenStore.ts";
import {
  gruendeStimmzettel,
  gruendeWahlscheine,
  stringToEnumValue,
} from "@/types/briefwahl/ZurueckweisungsgrundEnum.ts";
import { REQUIRED } from "@/util/rules.ts";

const { wahlen } = storeToRefs(useWahlenStore());
const { getWahlOrUndefinedById } = useWahlenStore();

const maxRows = computed(() => {
  return Math.max(
    ...wahlen.value!.map((wahl) => wahl.beanstandeteWahlbriefe!.length)
  );
});

const rowIcon = ref<string[]>([]);
const rowColor = ref<string[]>([]);

const wahlscheinGrund = ref(Array(maxRows.value).fill(""));

onMounted(() => {
  for (const row of Array.from({ length: maxRows.value }, (_, i) => i)) {
    // (_, i) => i wandelt jedes Element in seinen Index um
    rowIcon.value[row] = _isRowValidAtIndex(row) ? "$check" : "$edit";
    rowColor.value[row] = _isRowValidAtIndex(row) ? "success" : "error";
  }
});

watch(maxRows, (newValue, oldValue) => {
  if (oldValue < newValue) {
    // row added
    rowIcon.value.push("$edit");
    rowColor.value.push("error");
  }
});

function onZulassungsgrundChanged(
  newValue: string,
  rowIndex: number,
  column?: Wahl
) {
  if (column) {
    const wahl = getWahlOrUndefinedById(column.wahlID);
    wahl!.beanstandeteWahlbriefe![rowIndex] = stringToEnumValue(newValue);
  } else {
    wahlscheinGrund.value[rowIndex] = stringToEnumValue(newValue);
  }
  rowIcon.value[rowIndex] = _isRowValidAtIndex(rowIndex) ? "$check" : "$edit";
  rowColor.value[rowIndex] = _isRowValidAtIndex(rowIndex) ? "success" : "error";
}

// delete row + remove item from beanstandeteWahlbriefeList and wahlscheinGrundList
function deleteBeanstandeteWahlbriefeRow(rowIndex: number) {
  wahlen.value!.map((wahl) => wahl.beanstandeteWahlbriefe!.splice(rowIndex, 1));
  rowIcon.value.splice(rowIndex, 1);
  rowColor.value.splice(rowIndex, 1);
  wahlscheinGrund.value.splice(rowIndex, 1);
}

function _isRowValidAtIndex(rowIndex: number) {
  const stimmzettelValid = computed(() => {
    return wahlen.value!.every(
      (wahl) =>
        wahl.beanstandeteWahlbriefe &&
        wahl.beanstandeteWahlbriefe[rowIndex] &&
        !!wahl.beanstandeteWahlbriefe[rowIndex]
    );
  });
  const beschlussValid: boolean =
    wahlscheinGrund.value[rowIndex] && !!wahlscheinGrund.value[rowIndex];

  return stimmzettelValid.value && beschlussValid;
}
</script>

<style scoped>
td {
  text-align: center;
}
</style>
