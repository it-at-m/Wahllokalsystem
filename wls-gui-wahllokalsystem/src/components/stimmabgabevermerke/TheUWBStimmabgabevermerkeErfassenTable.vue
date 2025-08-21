<template>
  <v-container v-if="stimmabgabevermerke">
    <div class="d-flex">
      <v-number-input
        v-model="rowSize"
        :rules="[required, minNumber(1), maxNumber(250)]"
        max-width="15rem"
        label="Anzahl der Blätter"
      />
      <v-btn
        class="ml-4 mt-3"
        primary
        :disabled="disableChangeRowSizeButton"
        @click="changeRowCountOrOpenDialog"
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
        <tr>
          <td><b>Gesamt</b></td>
          <td
            v-for="totalCount in stimmvermerkeTableTotalEachWahldaten"
            :key="totalCount"
          >
            <b>{{ totalCount }}</b>
          </td>
        </tr>
      </tbody>
    </v-table>
    <base-dialog
      :visible="deleteDialog"
      dialogtitle="Reduzierung der Blätteranzahl des Wählerverzeichnisses"
      confirmtext="Trotzdem Löschen"
      canceltext="Abbrechen"
      icon="$information"
      @cancel="deleteDialog = false"
      @confirm="onDialogConfirmDeletingRows"
      ><div>
        Sie wollen Blätter löschen, für die Sie Stimmabgabevermerke eingetragen
        haben. Wenn Sie diese löschen, werden dadurch auch die Werte für die
        Stimmabgabevermerke gelöscht.
      </div></base-dialog
    >
  </v-container>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed, onMounted, ref } from "vue";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import { useRules } from "@/composables/common/rules.ts";
import { useStimmabgabevermerkeStore } from "@/stores/stimmabgabevermerkeStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const { minNumber, maxNumber, required } = useRules();

const {
  stimmabgabevermerke,
  stimmvermerkeTableTotalEachWahldaten,
  lowestNumberOfRowsOverAllWahldaten,
} = storeToRefs(useStimmabgabevermerkeStore());
const { isAnyRowThatShouldBeDeletedFilled, changeRowCount } =
  useStimmabgabevermerkeStore();
const { getWahlNameOrBlankStringById } = useWahlenStore();

onMounted(() => {
  rowSize.value = lowestNumberOfRowsOverAllWahldaten.value + 1;
});

const deleteDialog = ref(false);
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

function changeRowCountOrOpenDialog() {
  if (
    lowestNumberOfRowsOverAllWahldaten.value != null &&
    rowSize.value != null
  ) {
    if (isAnyRowThatShouldBeDeletedFilled(rowSize.value)) {
      deleteDialog.value = true;
    } else {
      changeRowCount(rowSize.value);
    }
  }
}

function onDialogConfirmDeletingRows() {
  if (rowSize.value != null) {
    changeRowCount(rowSize.value);
    deleteDialog.value = false;
  }
}
</script>

<style scoped></style>
