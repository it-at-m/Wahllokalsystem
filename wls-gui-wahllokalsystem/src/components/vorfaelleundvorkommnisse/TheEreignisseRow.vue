<template>
  <div>
    <base-ereignis-row
      v-for="(ereignis, index) in wahlbezirkEreignisse.ereigniseintraege"
      :key="index"
      :model-value="ereignis"
      :line-number="index + 1"
      @delete="() => onDeleteIconClicked(index)"
    />
    <yes-no-dialog
      v-model="deleteDialog"
      dialogtitle="Ereignis löschen"
      dialogtext="Möchten Sie dieses Ereignis wirklich löschen?"
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
import { useEreignisStore } from "@/stores/ereignisStore.ts";

const ereignisStore = useEreignisStore();
const { wahlbezirkEreignisse } = storeToRefs(ereignisStore);
const deleteDialog = ref(false);
const deleteIndex = ref<number | null>(null);

function closeYesNoDialog() {
  deleteDialog.value = false;
}

function showYesNoDialogForItem(index: number) {
  deleteIndex.value = index;
  deleteDialog.value = true;
}

function onDeleteIconClicked(index: number) {
  showYesNoDialogForItem(index);
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
