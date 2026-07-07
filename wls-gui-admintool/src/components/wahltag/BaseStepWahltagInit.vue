<template>
  <div>
    <div v-if="wahlterminDatenExists === true">
      <v-btn
        :loading="isLoading || isDeleting"
        active
        class="mb-2"
        data-test="override"
        @click="onOverrideWahltagClicked"
        >Überschreiben</v-btn
      >
    </div>
    <div v-if="wahlterminDatenExists === false">
      <v-btn
        :loading="isLoading"
        active
        class="mb-2"
        data-test="create"
        @click="onInitWahltagClicked"
        >Erstellen</v-btn
      >
    </div>
    <div v-if="wahlterminDatenExists === undefined">
      Zustand der konfigurierten Wahltage ist unbekannt
    </div>

    <v-divider class="my-4" />

    <div>
      <h3 class="text-h6 mb-2">Wahllokalbenutzer</h3>
      <div v-if="canManageWahllokalBenutzer">
        <v-btn
          :loading="isGeneratingBenutzer"
          active
          class="mb-2 mr-2"
          data-test="generate-users"
          @click="onGenerateBenutzerClicked"
          >Benutzer erstellen</v-btn
        >
        <v-btn
          :loading="isExportingBenutzer"
          active
          class="mb-2 mr-2"
          data-test="export-users"
          @click="onExportBenutzerClicked"
          >Exportieren</v-btn
        >
        <v-btn
          :loading="isDeletingBenutzer"
          active
          class="mb-2"
          color="error"
          data-test="delete-users"
          @click="onDeleteBenutzerClicked"
          >Löschen</v-btn
        >

        <v-text-field
          v-model="benutzerSearch"
          class="mt-2"
          clearable
          data-test="user-search"
          label="Benutzer suchen"
        />
        <v-data-table
          :headers="benutzerHeaders"
          :items="benutzer"
          :items-per-page="benutzerItemsPerPage"
          :items-per-page-options="benutzerItemsPerPageOptions"
          :loading="isLoadingBenutzer"
          :search="benutzerSearch"
          :sort-by="benutzerSortBy"
          data-test="user-table"
          no-data-text="Keine Wahllokalbenutzer vorhanden"
        />
      </div>
      <div
        v-else
        data-test="users-disabled-hint"
      >
        Wahltermindaten müssen zuerst erstellt werden.
      </div>
    </div>

    <base-dialog-wahltag-override-wahltermin-confirmation
      ref="wahltageOverrideConfirmationDialog"
      @confirmDelete="onOverrideDialogConfirmDelete"
      @cancelDelete="onOverrideDialogCancelDelete"
    />
    <base-dialog-wahltag-override-wahltermin-confirmation
      ref="wahllokalBenutzerDeleteConfirmationDialog"
      :confirmation-text="wahllokalBenutzerDeleteConfirmationText"
      required-confirm-text="Benutzer löschen"
      @confirmDelete="onDeleteBenutzerDialogConfirmDelete"
      @cancelDelete="onDeleteBenutzerDialogCancelDelete"
    />
  </div>
</template>
<script setup lang="ts">
import type { WahltagEvent } from "@/types/wahltag/WahltagEvent.ts";
import type { PropType } from "vue";

import { computed, ref, useTemplateRef, watch } from "vue";
import { VBtn, VDataTable, VDivider, VTextField } from "vuetify/components";

import BaseDialogWahltagOverrideWahlterminConfirmation from "@/components/wahltag/BaseDialogWahltagOverrideWahlterminConfirmation.vue";
import { useWahllokalBenutzerService } from "@/composables/wahllokalbenutzer/wahllokalbenutzerService.ts";
import { useWahltermindatenService } from "@/composables/wahltermindaten/wahltermindatenService.ts";

const {
  importWahlterminDaten,
  isLoading,
  deleteAndImportWahlterminDaten,
  isDeleting,
} = useWahltermindatenService();
const {
  benutzer,
  clearBenutzer,
  deleteBenutzer,
  exportBenutzer,
  generateBenutzer,
  isDeleting: isDeletingBenutzer,
  isExporting: isExportingBenutzer,
  isGenerating: isGeneratingBenutzer,
  isLoading: isLoadingBenutzer,
  loadBenutzer,
} = useWahllokalBenutzerService();

const props = defineProps({
  wahltagEvent: {
    type: Object as PropType<WahltagEvent>,
    required: true,
  },
  wahlterminDatenExists: {
    type: Boolean,
    required: false,
    default: undefined,
  },
});
const emits = defineEmits<{
  importWahlterminDatenDone: [];
}>();

const templateRefWahltagDeleteConfirmationDialog = useTemplateRef<
  InstanceType<typeof BaseDialogWahltagOverrideWahlterminConfirmation>
>("wahltageOverrideConfirmationDialog");
const templateRefWahllokalBenutzerDeleteConfirmationDialog = useTemplateRef<
  InstanceType<typeof BaseDialogWahltagOverrideWahlterminConfirmation>
>("wahllokalBenutzerDeleteConfirmationDialog");

const wahlterminDatenWereImported = ref(false);
const benutzerSearch = ref("");
const benutzerItemsPerPage = 50;
const benutzerItemsPerPageOptions = [50];
const benutzerHeaders = [{ title: "Benutzername", key: "benutzername" }];
const benutzerSortBy = [{ key: "benutzername", order: "asc" as const }];
const wahllokalBenutzerDeleteConfirmationText =
  'Für diesen Wahltag existieren möglicherweise bereits Wahllokalbenutzer. Bitte geben Sie "Benutzer löschen" in das Eingabefeld ein und bestätigen Sie, um die Wahllokalbenutzer zu löschen.';
const canManageWahllokalBenutzer = computed(
  () =>
    props.wahlterminDatenExists === true || wahlterminDatenWereImported.value
);

async function onInitWahltagClicked() {
  await importWahlterminDaten(props.wahltagEvent.wahltagID);
  wahlterminDatenWereImported.value = true;
  emits("importWahlterminDatenDone");
}

watch(
  () =>
    [props.wahltagEvent.wahltagID, canManageWahllokalBenutzer.value] as const,
  async ([wahltagID, canManageBenutzer]) => {
    if (canManageBenutzer) {
      try {
        await loadBenutzer(wahltagID);
      } catch {
        // The service already shows a user notification for failed automatic loads.
      }
    } else {
      clearBenutzer();
    }
  },
  { immediate: true }
);

async function onOverrideDialogConfirmDelete() {
  templateRefWahltagDeleteConfirmationDialog.value?.hideDialog();

  await deleteAndImportWahlterminDaten(props.wahltagEvent.wahltagID);
  wahlterminDatenWereImported.value = true;
  emits("importWahlterminDatenDone");
}

function onOverrideDialogCancelDelete() {
  templateRefWahltagDeleteConfirmationDialog.value?.hideDialog();
}

function onOverrideWahltagClicked() {
  templateRefWahltagDeleteConfirmationDialog.value?.showDialog();
}

async function onGenerateBenutzerClicked() {
  await generateBenutzer(props.wahltagEvent.wahltagID);
}

async function onExportBenutzerClicked() {
  await exportBenutzer(props.wahltagEvent.wahltagID);
}

function onDeleteBenutzerClicked() {
  templateRefWahllokalBenutzerDeleteConfirmationDialog.value?.showDialog();
}

async function onDeleteBenutzerDialogConfirmDelete() {
  templateRefWahllokalBenutzerDeleteConfirmationDialog.value?.hideDialog();
  await deleteBenutzer(props.wahltagEvent.wahltagID);
}

function onDeleteBenutzerDialogCancelDelete() {
  templateRefWahllokalBenutzerDeleteConfirmationDialog.value?.hideDialog();
}
</script>
