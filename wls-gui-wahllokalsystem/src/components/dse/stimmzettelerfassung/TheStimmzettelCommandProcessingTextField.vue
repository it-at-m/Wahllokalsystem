<template>
  <v-form>
    <v-text-field
      :ref="COMMAND_TEXT_FIELD_TEMPLATE_REF_NAME"
      :model-value="commandString"
      :disabled="disabled"
      :error-messages="errorMessage"
      label="Kurzbefehl"
      @update:model-value="onModelValueChanged"
      @keydown.enter.prevent="onEnterPressed"
    />
  </v-form>
</template>

<script setup lang="ts">
import type { StimmzettelManager } from "@/composables/dse/stimmzettelerfassung/stimmzettelManager.ts";
import type { PropType } from "vue";
import type { VTextField } from "vuetify/components/VTextField";

import { ref, useTemplateRef } from "vue";

import { CommandExecutionError } from "@/types/dse/error/CommandExecutionError.ts";
import { UnsupportedCommandError } from "@/types/dse/error/UnsupportedCommandError.ts";

const COMMAND_TEXT_FIELD_TEMPLATE_REF_NAME = "commandTextField";

const props = defineProps({
  disabled: {
    type: Boolean,
    required: false,
    default: false,
  },
  stimmzettelManager: {
    type: Object as PropType<StimmzettelManager>,
    required: true,
  },
});

defineExpose({ focus });

const commandString = ref("");
const errorMessage = ref<string | null>(null);
const commandTextField = useTemplateRef<InstanceType<typeof VTextField>>(
  COMMAND_TEXT_FIELD_TEMPLATE_REF_NAME
);

function focus() {
  commandTextField.value?.focus();
}

function onEnterPressed() {
  try {
    props.stimmzettelManager.parseCommandOrThrowError(commandString.value);
    commandString.value = "";
    clearErrorMessage();
  } catch (parseError) {
    if (parseError instanceof UnsupportedCommandError) {
      errorMessage.value = "Der Befehl wird nicht unterstützt.";
    } else if (parseError instanceof CommandExecutionError) {
      errorMessage.value = parseError.message;
    } else {
      errorMessage.value = "Unbekannter Fehler aufgetreten.";
    }
  }
}

function onModelValueChanged(newValue: string) {
  commandString.value = newValue;
  if (errorMessage.value) {
    clearErrorMessage();
  }
}

function clearErrorMessage() {
  errorMessage.value = "";
}
</script>
