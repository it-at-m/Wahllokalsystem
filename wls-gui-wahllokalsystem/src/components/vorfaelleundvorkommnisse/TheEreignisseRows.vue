<template>
  <div>
    <base-ereignis-row
      v-for="(ereignis, index) in wahlbezirkEreignisse.ereigniseintraege"
      :key="index"
      :model-value="ereignis"
      :line-number="index + 1"
      @delete="(payload) => onDeleteIcon(index, payload)"
    />
    <base-dialog
      :visible="deleteDialog"
      dialogtitle="Ereignis löschen"
      confirmtext="Ja"
      canceltext="Nein"
      icon="$information"
      @confirm="onConfirmDelete"
      @cancel="onCancelDelete"
    >
      <div>Möchten Sie das Ereignis wirklich löschen?<br ><br ></div>
      <div>Datum: {{ dialogDate }}</div>
      <div>Uhrzeit: {{ dialogTime }}</div>
      <div>Beschreibung: {{ dialogBeschreibung }}</div>
    </base-dialog>
  </div>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { ref } from "vue";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import BaseEreignisRow from "@/components/vorfaelleundvorkommnisse/BaseEreignisRow.vue";
import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useEreignisStore } from "@/stores/ereignisStore.ts";

const ereignisStore = useEreignisStore();
const { toHhMm, toGermanDate } = useDateTimeFormatter();
const { wahlbezirkEreignisse } = storeToRefs(ereignisStore);
const deleteDialog = ref(false);
const deleteIndex = ref<number | null>(null);
const dialogTime = ref("");
const dialogDate = ref("");
const dialogBeschreibung = ref("");

function closeDeleteDialog() {
  deleteDialog.value = false;
}

function showDeleteDialog(index: number) {
  deleteIndex.value = index;
  deleteDialog.value = true;
}

function onDeleteIcon(
  index: number,
  payload: { dateOnly?: Date; timeOnly?: Date; beschreibung?: string }
) {
  const { dateOnly, timeOnly, beschreibung } = payload;
  dialogTime.value = toHhMm(timeOnly);
  dialogDate.value = toGermanDate(dateOnly) ?? "";
  dialogBeschreibung.value = beschreibung ?? "";

  if (!dateOnly && !timeOnly && !beschreibung) {
    deleteIndex.value = index;
    onConfirmDelete();
  } else {
    showDeleteDialog(index);
  }
}

function onCancelDelete() {
  closeDeleteDialog();
}

function onConfirmDelete() {
  if (deleteIndex.value !== null) {
    ereignisStore.deleteEreignisByIndex(deleteIndex.value);
    deleteIndex.value = null;
  }
  closeDeleteDialog();
}
</script>
