<template>
  <v-form @submit="onSubmit">
    <v-text-field
      v-model="commandString"
      :error-messages="errorMessages"
      @keydown.enter.prevent="onSubmit"
    />
  </v-form>
</template>

<script setup lang="ts">
import type { AbstractCommandEvent } from "@/types/experimental/AbstractCommandEvent.ts";

import { computed, ref, watch } from "vue";

import { useLogging } from "@/composables/common/logging.ts";
import { useStimmzettelQuickInputHandler } from "@/composables/experimental/stimmzettelQuickInputHandler.ts";

const logger = useLogging("baseFormStimmzettelQuickInput");

const emit = defineEmits<{
  command: [event: AbstractCommandEvent];
}>();

const { handleQuickInput } = useStimmzettelQuickInputHandler();

const commandString = ref("");
const errorMessage = ref<string | null>(null);

const errorMessages = computed(() =>
  errorMessage.value ? [errorMessage.value] : []
);

watch(commandString, (newValue) => {
  if (!newValue) {
    errorMessage.value = null;
  }
});

function onSubmit() {
  logger.log(`on submit - commandString > ${commandString.value}`);
  const commandEvent = handleQuickInput(commandString.value);
  if (commandEvent) {
    emit("command", commandEvent);
    logger.log(`emitting command event > ${JSON.stringify(commandEvent)}`);
    resetInput();
  } else {
    errorMessage.value = "Eingabe ungültig";
  }
}

function resetInput(): void {
  commandString.value = "";
  errorMessage.value = null;
}
</script>

<style scoped></style>
