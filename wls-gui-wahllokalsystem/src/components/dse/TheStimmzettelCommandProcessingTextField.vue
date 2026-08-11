<template>
  <v-form>
    <v-text-field
      :model-value="commandString"
      :error-messages="errorMessage"
      label="Kurzbefehl"
      @update:model-value="onModelValueChanged"
      @keydown.enter.prevent="onEnterPressed"
    />
  </v-form>
</template>

<script setup lang="ts">
import type { StimmzettelManager } from "@/composables/dse/stimmzettelManager.ts";
import type { PropType } from "vue";

import { ref } from "vue";

import { CommandExecutionError } from "@/types/dse/error/CommandExecutionError.ts";
import { UnsupportedCommandError } from "@/types/dse/error/UnsupportedCommandError.ts";

const props = defineProps({
  stimmzettelManger: {
    type: Object as PropType<StimmzettelManager>,
    required: true,
  },
});

const commandString = ref("");
const errorMessage = ref<string | null>(null);

function onEnterPressed() {
  try {
    props.stimmzettelManger.parseCommandOrThrowError(commandString.value);
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
