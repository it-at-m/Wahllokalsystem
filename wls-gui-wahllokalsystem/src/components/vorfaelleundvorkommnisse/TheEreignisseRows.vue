<template>
  <div>
    <base-ereignis-row
      v-for="(ereignis, index) in wahlbezirkEreignisse.ereigniseintraege"
      :key="index"
      :model-value="ereignis"
      :line-number="index + 1"
      @delete="(payload) => onDeleteIconClicked(index, payload)"
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
import { storeToRefs } from "pinia";
import { ref } from "vue";

import YesNoDialog from "@/components/common/YesNoDialog.vue";
import BaseEreignisRow from "@/components/vorfaelleundvorkommnisse/BaseEreignisRow.vue";
import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useEreignisStore } from "@/stores/ereignisStore.ts";

const ereignisStore = useEreignisStore();
const { toHhMm, toGermanDate } = useDateTimeFormatter();
const { wahlbezirkEreignisse } = storeToRefs(ereignisStore);
const deleteDialog = ref(false);
const deleteDialogText = ref("");
const deleteIndex = ref<number | null>(null);

function closeYesNoDialog() {
  deleteDialog.value = false;
}

function showYesNoDialogForItem(
  index: number,
  payload: { dateOnly?: Date; timeOnly?: Date; beschreibung?: string }
) {
  deleteIndex.value = index;
  deleteDialog.value = true;
  deleteDialogText.value = _formatDeleteDialogText(payload);
}

function onDeleteIconClicked(
  index: number,
  payload: { dateOnly?: Date; timeOnly?: Date; beschreibung?: string }
) {
  const { dateOnly, timeOnly, beschreibung } = payload;
  if (!dateOnly && !timeOnly && !beschreibung) {
    deleteIndex.value = index;
    onYesNoDialogYesClicked();
  } else {
    showYesNoDialogForItem(index, payload);
  }
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

function _formatDeleteDialogText(payload: {
  dateOnly?: Date;
  timeOnly?: Date;
  beschreibung?: string;
}): string {
  const { dateOnly, timeOnly, beschreibung } = payload;
  return `Möchten Sie das Ereignis wirklich löschen? Datum: ${toGermanDate(dateOnly) ?? ""},
   Uhrzeit: ${toHhMm(timeOnly)}, Beschreibung: ${beschreibung}`;
}
</script>
