<template>
  <div>
    <base-ereignis-row
      v-for="(ereignis, index) in wahlbezirkEreignisse.ereigniseintraege"
      :key="index"
      :model-value="ereignis"
      :line-number="index + 1"
      @uhrzeit-changed="onEreignisUhrzeitChanged(index, $event)"
      @delete="(ereignisPayload) => onDeleteIcon(index, ereignisPayload)"
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
      <div class="mb-3">Möchten Sie das Ereignis wirklich löschen?</div>
      <div>Datum: {{ dialogDate }}</div>
      <div>Uhrzeit: {{ dialogTime }}</div>
      <div>Beschreibung: {{ dialogBeschreibung }}</div>
    </base-dialog>
  </div>
</template>

<script setup lang="ts">
import type { EreignisPayload } from "@/types/vorfaelleundvorkommnisse/EreignisPayload.ts";

import { storeToRefs } from "pinia";
import { ref } from "vue";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import BaseEreignisRow from "@/components/vorfaelleundvorkommnisse/BaseEreignisRow.vue";
import { useEreignisStore } from "@/stores/ereignisStore.ts";

const ereignisStore = useEreignisStore();
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

function onDeleteIcon(index: number, ereignisPayload: EreignisPayload) {
  const { dateStr, timeStr, beschreibung } = ereignisPayload;
  dialogTime.value = timeStr ?? "";
  dialogDate.value = dateStr ?? "";
  dialogBeschreibung.value = beschreibung ?? "";

  if (!beschreibung) {
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

function onEreignisUhrzeitChanged(
  indexOfEreignis: number,
  newUhrzeit: Date | undefined
) {
  ereignisStore.updateUhrzeitByIndex(newUhrzeit, indexOfEreignis);
}
</script>
