<template>
  <v-dialog
    :model-value="visible"
    data-test="dialog-wahltag-override-wahltermindaten-confirmation"
  >
    <v-card>
      <v-card-title>Wahltermindaten erneut anlegen</v-card-title>
      <v-card-text>
        <div data-test="div-confirm-information">
          Für diesen Wahltag existieren bereits Wahltermindaten. Bitte geben sie
          "{{ requiredConfirmText }}" in das Eingabefeld ein und bestätigen sie
          um die alten Wahltermindaten zu löschen und neue zu erstellen.
        </div>
        <v-text-field
          v-model="confirmText"
          ref="confirmTextField"
          :label="confirmTextFieldLabel"
          :rules="[requiredText(requiredConfirmText)]"
          class="mt-2"
          data-test="textfield-confirm-text"
        />
      </v-card-text>
      <v-card-actions>
        <base-button-cancel @click="onCancelClicked" />
        <base-button-confirm
          :disabled="!hasUserEnteredConfirmText"
          @click="onConfirmClicked"
        />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script setup lang="ts">
import { computed, ref, useTemplateRef } from "vue";
import {
  VCard,
  VCardActions,
  VCardText,
  VCardTitle,
  VDialog,
  VTextField,
} from "vuetify/components";

import BaseButtonCancel from "@/components/common/BaseButtonCancel.vue";
import BaseButtonConfirm from "@/components/common/BaseButtonConfirm.vue";
import useRules from "@/composables/common/rules.ts";

const props = defineProps({
  requiredConfirmText: {
    type: String,
    required: false,
    default: "Löschen",
  },
});

const emit = defineEmits<{
  confirmDelete: [];
  cancelDelete: [];
}>();

defineExpose({
  show,
  hide,
});

const visible = ref(false);

const confirmText = ref("");
const confirmTextField = useTemplateRef("confirmTextField");

const confirmTextFieldLabel = computed(
  () => `Bitte geben sie "${props.requiredConfirmText}" ein`
);
const hasUserEnteredConfirmText = computed(
  () => props.requiredConfirmText === confirmText.value
);

const { requiredText } = useRules();

function onCancelClicked() {
  emit("cancelDelete");
}

function onConfirmClicked() {
  emit("confirmDelete");
}

function show() {
  confirmText.value = "";
  confirmTextField.value?.reset();
  visible.value = true;
}

function hide() {
  visible.value = false;
}
</script>
