<template>
  <v-dialog
    :model-value="visible"
    data-test="dialog-wahltag-override-wahltermindaten-confirmation"
  >
    <v-card>
      <v-card-title>Wahltermindaten erneut anlegen</v-card-title>
      <v-card-text>
        <slot />
        <v-text-field
          v-model="confirmText"
          ref="confirmTextField"
          :label="confirmTextFieldLabel"
          :rules="[requiredText(requiredConfirmText)]"
          class="mt-2"
          autofocus
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
import type { ShallowRef } from "vue";

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
import { useRules } from "@/composables/common/rules.ts";
import { DEFAULT_TEXT_CONFIRMATION_TEXT } from "@/constants.ts";

const props = defineProps({
  requiredConfirmText: {
    type: String,
    required: false,
    default: DEFAULT_TEXT_CONFIRMATION_TEXT,
  },
});

const emit = defineEmits<{
  confirmDelete: [];
  cancelDelete: [];
}>();

defineExpose({
  showDialog,
  hideDialog,
});

const visible = ref(false);

const confirmText = ref("");
const confirmTextField = useTemplateRef("confirmTextField") as Readonly<
  ShallowRef<InstanceType<typeof VTextField>>
>;

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

function showDialog() {
  confirmText.value = "";
  confirmTextField.value?.reset();
  visible.value = true;
}

function hideDialog() {
  visible.value = false;
}
</script>
