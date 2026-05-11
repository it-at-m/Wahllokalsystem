<template>
  <v-form>
    <div class="d-flex align-center">
      <v-text-field
        v-model="commandString"
        :error-messages="errorMessages"
        placeholder="str + enter zum speichern"
        @keydown.enter.prevent="onSubmit"
      />
      <!--      <div class="ms-2">-->
      <!--        <v-menu>-->
      <!--          <template v-slot:activator="{ props }">-->
      <!--            <v-icon-->
      <!--              v-bind="props"-->
      <!--              size="large"-->
      <!--              icon="$information"-->
      <!--            />-->
      <!--          </template>-->
      <!--          <v-card>-->
      <!--            <v-card-title>Hinweise</v-card-title>-->
      <!--            <v-card-text>-->
      <!--              <div>Hier stehen Hinweise zur Bedienung</div>-->
      <!--              <div>-->
      <!--                &lt;Kandidatennummer&gt;+&lt;Stimmen&gt; - Hinzufügen von-->
      <!--                Stimmen zu einen Kandidaten - 103+3-->
      <!--              </div>-->
      <!--            </v-card-text>-->
      <!--          </v-card>-->
      <!--        </v-menu>-->
      <!--      </div>-->
    </div>
  </v-form>
</template>

<script setup lang="ts">
import type { AbstractCommandEvent } from "@/types/experimental/AbstractCommandEvent.ts";
import type { StimmzettelEvent } from "@/types/experimental/StimmzettelEvent.ts";

import { computed, ref, watch } from "vue";

import { useLogging } from "@/composables/common/logging.ts";
import { useStimmzettelQuickInputHandler } from "@/composables/experimental/stimmzettelQuickInputHandler.ts";
import { StimmzettelEventTypeEnum } from "@/types/experimental/StimmzettelEventTypeEnum.ts";

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

function onSubmit(event: KeyboardEvent) {
  logger.log(
    `event - event.ctrlKey > ${event.ctrlKey}, event.altKey > ${event.altKey}, event.shiftKey > ${event.shiftKey}, event.metaKey > ${event.metaKey}`
  );
  logger.log(`on submit - commandString > ${commandString.value}`);

  if (event.ctrlKey && !commandString.value) {
    const saveCommand: StimmzettelEvent = {
      stimmzettelEventType: StimmzettelEventTypeEnum.SAVE,
    };
    emit("command", saveCommand);
  } else {
    const commandEvent = handleQuickInput(commandString.value);
    if (commandEvent) {
      emit("command", commandEvent);
      logger.log(`emitting command event > ${JSON.stringify(commandEvent)}`);
      resetInput();
    } else {
      errorMessage.value = "Eingabe ungültig";
    }
  }
}

function resetInput(): void {
  commandString.value = "";
  errorMessage.value = null;
}
</script>

<style scoped></style>
