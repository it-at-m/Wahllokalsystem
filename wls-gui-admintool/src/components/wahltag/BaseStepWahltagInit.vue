<template>
  <div>
    <div v-if="wahlterminDatenExists === true">
      <v-btn
        :loading="isLoading || istDeleting"
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

    <base-dialog-wahltag-override-wahltermin-confirmation
      ref="wahltageOverrideConfirmationDialog"
      @confirmDelete="onOverrideDialogConfirmDelete"
      @cancelDelete="onOverrideDialogCancelDelete"
    />
  </div>
</template>
<script setup lang="ts">
import type { WahltagEvent } from "@/types/wahltag/WahltagEvent.ts";
import type { PropType } from "vue";

import { useTemplateRef } from "vue";
import { VBtn } from "vuetify/components";

import BaseDialogWahltagOverrideWahlterminConfirmation from "@/components/wahltag/BaseDialogWahltagOverrideWahlterminConfirmation.vue";
import { useWahltermindatenService } from "@/composables/wahltermindaten/wahltermindatenService.ts";

const {
  importWahlterminDaten,
  isLoading,
  deleteAndImportWahlterminDaten,
  istDeleting,
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

const templateRefWahltagDeleteConfirmationDialog = useTemplateRef<
  InstanceType<typeof BaseDialogWahltagOverrideWahlterminConfirmation>
>("wahltageOverrideConfirmationDialog");

function onInitWahltagClicked() {
  importWahlterminDaten(props.wahltagEvent.wahltagID);
}

async function onOverrideDialogConfirmDelete() {
  templateRefWahltagDeleteConfirmationDialog.value?.hide();

  deleteAndImportWahlterminDaten(props.wahltagEvent.wahltagID);
}

function onOverrideDialogCancelDelete() {
  templateRefWahltagDeleteConfirmationDialog.value?.hide();
}

function onOverrideWahltagClicked() {
  templateRefWahltagDeleteConfirmationDialog.value?.show();
}
</script>
