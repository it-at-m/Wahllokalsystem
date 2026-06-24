<template>
  <div data-test="step-wahllokalbenutzer">
    <div class="text-subtitle-1 mb-2">Wahllokalbenutzer</div>
    <v-btn
      :loading="isGenerating"
      active
      class="mb-2 mr-2"
      data-test="generate-benutzer"
      @click="onGenerateBenutzerClicked"
      >Benutzer erstellen</v-btn
    >
    <v-btn
      :loading="isExporting"
      active
      class="mb-2 mr-2"
      data-test="export-benutzer"
      @click="onExportBenutzerClicked"
      >Benutzer exportieren</v-btn
    >
    <v-btn
      :loading="isDeleting"
      active
      class="mb-2"
      color="error"
      data-test="delete-benutzer"
      @click="onDeleteBenutzerClicked"
      >Benutzer löschen</v-btn
    >

    <base-list-wahllokal-benutzer
      v-if="benutzerCsv"
      :csv="benutzerCsv"
      class="mt-4"
    />

    <base-dialog-wahllokal-benutzer-delete-confirmation
      ref="benutzerDeleteConfirmationDialog"
      @confirmDelete="onDeleteDialogConfirmDelete"
      @cancelDelete="onDeleteDialogCancelDelete"
    />
  </div>
</template>
<script setup lang="ts">
import { ref, useTemplateRef } from "vue";
import { VBtn } from "vuetify/components";

import BaseDialogWahllokalBenutzerDeleteConfirmation from "@/components/wahltag/BaseDialogWahllokalBenutzerDeleteConfirmation.vue";
import BaseListWahllokalBenutzer from "@/components/wahltag/BaseListWahllokalBenutzer.vue";
import { useWahllokalBenutzerService } from "@/composables/wahllokalbenutzer/wahllokalbenutzerService.ts";

const {
  generateBenutzer,
  exportBenutzer,
  deleteBenutzer,
  isGenerating,
  isExporting,
  isDeleting,
} = useWahllokalBenutzerService();

const props = defineProps({
  wahltagId: {
    type: String,
    required: true,
  },
});

const templateRefBenutzerDeleteConfirmationDialog = useTemplateRef<
  InstanceType<typeof BaseDialogWahllokalBenutzerDeleteConfirmation>
>("benutzerDeleteConfirmationDialog");

const benutzerCsv = ref("");

async function onGenerateBenutzerClicked() {
  benutzerCsv.value = await generateBenutzer(props.wahltagId);
}

async function onExportBenutzerClicked() {
  benutzerCsv.value = await exportBenutzer(props.wahltagId);
}

function onDeleteBenutzerClicked() {
  templateRefBenutzerDeleteConfirmationDialog.value?.showDialog();
}

async function onDeleteDialogConfirmDelete() {
  templateRefBenutzerDeleteConfirmationDialog.value?.hideDialog();
  await deleteBenutzer(props.wahltagId);
  benutzerCsv.value = "";
}

function onDeleteDialogCancelDelete() {
  templateRefBenutzerDeleteConfirmationDialog.value?.hideDialog();
}
</script>
