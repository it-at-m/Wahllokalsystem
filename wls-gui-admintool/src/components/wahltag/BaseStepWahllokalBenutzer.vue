<template>
  <div data-test="step-wahllokalbenutzer">
    <div class="text-subtitle-1 mb-2">Wahllokalbenutzer</div>
    <v-btn
      :loading="isGenerating || isLoading"
      active
      class="mb-2 mr-2"
      data-test="generate-benutzer"
      @click="onGenerateBenutzerClicked"
      >{{
        benutzerExist ? "Benutzer überschreiben" : "Benutzer erstellen"
      }}</v-btn
    >
    <v-btn
      v-if="benutzerExist"
      :loading="isExporting"
      active
      class="mb-2 mr-2"
      data-test="export-benutzer"
      @click="onExportBenutzerClicked"
      >Benutzer exportieren</v-btn
    >
    <v-btn
      v-if="benutzerExist"
      :loading="isDeleting"
      active
      class="mb-2"
      color="error"
      data-test="delete-benutzer"
      @click="onDeleteBenutzerClicked"
      >Benutzer löschen</v-btn
    >

    <base-list-wahllokal-benutzer
      v-if="benutzerExist"
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
import { computed, onMounted, ref, useTemplateRef, watch } from "vue";
import { VBtn } from "vuetify/components";

import BaseDialogWahllokalBenutzerDeleteConfirmation from "@/components/wahltag/BaseDialogWahllokalBenutzerDeleteConfirmation.vue";
import BaseListWahllokalBenutzer from "@/components/wahltag/BaseListWahllokalBenutzer.vue";
import { useWahllokalBenutzerFormatter } from "@/composables/wahllokalbenutzer/wahllokalbenutzerFormatter.ts";
import { useWahllokalBenutzerService } from "@/composables/wahllokalbenutzer/wahllokalbenutzerService.ts";

const {
  generateBenutzer,
  exportBenutzer,
  deleteBenutzer,
  loadBenutzer,
  isGenerating,
  isExporting,
  isDeleting,
  isLoading,
} = useWahllokalBenutzerService();
const { parseBenutzer } = useWahllokalBenutzerFormatter();

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
const benutzerExist = computed(
  () => parseBenutzer(benutzerCsv.value).length > 0
);

onMounted(loadExistingBenutzer);
watch(() => props.wahltagId, loadExistingBenutzer);

async function loadExistingBenutzer() {
  try {
    benutzerCsv.value = await loadBenutzer(props.wahltagId);
  } catch {
    benutzerCsv.value = "";
  }
}

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
