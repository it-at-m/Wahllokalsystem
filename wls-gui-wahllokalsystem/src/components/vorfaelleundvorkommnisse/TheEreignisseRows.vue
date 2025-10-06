<template>
  <div>
    <base-ereignis-row
      v-for="(ereignis, index) in wahlbezirkEreignisse.ereigniseintraege"
      :key="index"
      :model-value="ereignis"
      :line-number="index + 1"
      @delete="() => onDeleteIconClicked(index, ereignis)"
    />
    <yes-no-dialog
      v-model="deleteDialog"
      dialogtitle="Ereignis löschen"
      :dialogtext="deleteDialogText"
      @no="onYesNoDialogNoClicked"
      @yes="onYesNoDialogYesClicked"
    />
  </div>
</template>

<script setup lang="ts">
import type { Ereignis } from "@/types/vorfaelleundvorkommnisse/Ereignis.ts";

import { storeToRefs } from "pinia";
import { ref } from "vue";

import YesNoDialog from "@/components/common/YesNoDialog.vue";
import BaseEreignisRow from "@/components/vorfaelleundvorkommnisse/BaseEreignisRow.vue";
import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useEreignisStore } from "@/stores/ereignisStore.ts";

const ereignisStore = useEreignisStore();
const { wahlbezirkEreignisse } = storeToRefs(ereignisStore);
const deleteDialog = ref(false);
const deleteDialogText = ref("");
const deleteIndex = ref<number | null>(null);

function closeYesNoDialog() {
  deleteDialog.value = false;
}

function showYesNoDialogForItem(index: number, ereignis: Ereignis) {
  deleteIndex.value = index;
  deleteDialog.value = true;
  const { toHhMm, toGermanDate } = useDateTimeFormatter();
  deleteDialogText.value =
    "Möchten Sie das Ereignis  wirklich löschen?" +
    " Datum: " +
    toGermanDate(ereignis.uhrzeit) +
    ", Uhrzeit: " +
    toHhMm(ereignis.uhrzeit) +
    ", Beschreibung: " +
    ereignis.beschreibung;
}

function onDeleteIconClicked(index: number, ereignis: Ereignis) {
  showYesNoDialogForItem(index, ereignis);
}

function onYesNoDialogNoClicked() {
  closeYesNoDialog();
}

function onYesNoDialogYesClicked() {
  if (deleteIndex.value !== null) {
    ereignisStore.deleteEreignisByIndex(deleteIndex.value);
    deleteIndex.value = null;
  }
  closeYesNoDialog();
}
</script>
