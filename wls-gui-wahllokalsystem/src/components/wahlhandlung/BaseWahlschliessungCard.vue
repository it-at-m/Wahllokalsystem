<template>
  <div>
    <v-card-title>Uhrzeit Wahlende</v-card-title>
    <v-card-text class="pb-0">
      Bitte geben Sie hier die Uhrzeit ein, zu der die Wahl für geschlossen
      erklärt wurde.
      <v-form v-model="schliessungsuhrzeitValidForm">
        <base-time-input
          v-model="schliessungsuhrzeitState.schliessungsuhrzeit"
          class="mt-5"
          max-width="300"
          :rules="[
            required,
            timeNotInFuture,
            timeGreaterOrEqual(fruehesteSchliessungsuhrzeit),
          ]"
        />
      </v-form>
      <base-feedback-card
        v-if="!isVorfaelleMaintained"
        title="Vorfälle melden"
        :type="InputFeedbackTypeEnum.error"
      >
        Sie können den Wahlschluss erst eingeben, wenn sie über mögliche
        eingetretene Störungen berichtet und diese gespeichert haben.
        <template #additionalFeedback>
          <base-text-button @click="onEreignisseBearbeiten"
            >Zu den Ereignissen</base-text-button
          >
        </template>
      </base-feedback-card>
      <base-feedback-card
        v-else
        :title="erinnerungTitle"
        :type="InputFeedbackTypeEnum.information"
      >
        {{ erinnerungText }}
        <template #additionalFeedback>
          <base-text-button @click="onEreignisseBearbeiten"
            >Zu den Ereignissen</base-text-button
          >
        </template>
      </base-feedback-card>
    </v-card-text>
    <v-card-actions>
      <base-button-save
        :loading="schliessungsuhrzeitState.schliessungsuhrzeitIsSaving"
        :disabled="isSaveButtonDisabled"
        save-text="Speichern und Weiter"
        @click="onSaveSchliessungsuhrzeitClicked"
      />
    </v-card-actions>
  </div>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed, ref } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseFeedbackCard from "@/components/common/cards/BaseFeedbackCard.vue";
import BaseTimeInput from "@/components/common/inputs/BaseTimeInput.vue";
import { useRules } from "@/composables/common/rules.ts";
import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";
import { ROUTE_EREIGNISSE } from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useEreignisStore } from "@/stores/ereignisStore.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { InputFeedbackTypeEnum } from "@/types/common/InputFeedbackTypeEnum.ts";

const { required, timeGreaterOrEqual, timeNotInFuture } = useRules();
const { getNextRoute } = useNavigationUtils();

const { schliessungsuhrzeitActions } = useWahlbezirkStore();
const { schliessungsuhrzeitState } = storeToRefs(useWahlbezirkStore());
const { fruehesteSchliessungsuhrzeit } = storeToRefs(useInfomanagementStore());
const { isVorfaelleMaintained, ereigniseintraegeContainsVorfaelle } =
  storeToRefs(useEreignisStore());

const schliessungsuhrzeitValidForm = ref<null | boolean>(null);

const isSaveButtonDisabled = computed(
  () =>
    schliessungsuhrzeitValidForm.value !== true || !isVorfaelleMaintained.value
);

const erinnerungTitle = computed(() =>
  ereigniseintraegeContainsVorfaelle.value
    ? "Vorfälle aktualisieren"
    : "Vorfälle melden"
);

const erinnerungText = computed(() =>
  ereigniseintraegeContainsVorfaelle.value
    ? "Wenn sich während der Wahlhandlung weitere Vorfälle ereignet haben, können diese hier erfasst werden."
    : "Wenn sich während der Wahlhandlung Vorfälle ereignet haben, können diese hier erfasst werden."
);

async function onEreignisseBearbeiten() {
  await router.push(ROUTE_EREIGNISSE);
}

async function onSaveSchliessungsuhrzeitClicked() {
  await schliessungsuhrzeitActions.sendSchliessungsuhrzeit();
  await router.push(getNextRoute());
}
</script>
