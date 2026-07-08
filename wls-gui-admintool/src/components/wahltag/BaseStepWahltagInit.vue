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
      <base-step-wahllokal-benutzer
        class="mt-4"
        :wahltag-id="wahltagEvent.wahltagID"
      />
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

    <base-dialog-confirmation
      ref="wahltageOverrideConfirmationDialog"
      @confirmDelete="onOverrideDialogConfirmDelete"
      @cancelDelete="onOverrideDialogCancelDelete"
      ><div data-test="div-confirm-information">
        Für diesen Wahltag existieren bereits Wahltermindaten. Bitte geben Sie
        "{{ DEFAULT_TEXT_CONFIRMATION_TEXT }}" in das Eingabefeld ein und
        bestätigen Sie, um die alten Wahltermindaten zu löschen und neue zu
        erstellen.
      </div>
    </base-dialog-confirmation>
  </div>
</template>
<script setup lang="ts">
import type { WahltagEvent } from "@/types/wahltag/WahltagEvent.ts";
import type { PropType } from "vue";

import { useTemplateRef } from "vue";
import { VBtn } from "vuetify/components";

import BaseDialogConfirmation from "@/components/wahltag/BaseDialogConfirmation.vue";
import BaseStepWahllokalBenutzer from "@/components/wahltag/BaseStepWahllokalBenutzer.vue";
import { useWahltermindatenService } from "@/composables/wahltermindaten/wahltermindatenService.ts";
import { DEFAULT_TEXT_CONFIRMATION_TEXT } from "@/constants.ts";

const {
  importWahlterminDaten,
  isLoading,
  deleteAndImportWahlterminDaten,
  isDeleting,
} = useWahltermindatenService();

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
  InstanceType<typeof BaseDialogConfirmation>
>("wahltageOverrideConfirmationDialog");

async function onInitWahltagClicked() {
  await importWahlterminDaten(props.wahltagEvent.wahltagID);
  emits("importWahlterminDatenDone");
}

async function onOverrideDialogConfirmDelete() {
  templateRefWahltagDeleteConfirmationDialog.value?.hideDialog();

  await deleteAndImportWahlterminDaten(props.wahltagEvent.wahltagID);
  emits("importWahlterminDatenDone");
}

function onOverrideDialogCancelDelete() {
  templateRefWahltagDeleteConfirmationDialog.value?.hideDialog();
}

function onOverrideWahltagClicked() {
  templateRefWahltagDeleteConfirmationDialog.value?.showDialog();
}
</script>
