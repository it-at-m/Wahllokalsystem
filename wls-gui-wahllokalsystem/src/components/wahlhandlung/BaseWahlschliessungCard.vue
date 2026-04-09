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
        :title="
          'Vorfälle ' + (isVorfaelleMaintained ? 'aktualisieren' : 'melden')
        "
        :type="type"
      >
        <div v-if="isVorfaelleMaintained">
          Wenn sich während der Wahlhandlung weitere Vorfälle ereignet haben,
          können diese hier erfasst werden.
        </div>
        <div v-else>
          Sie können den Wahlschluss erst eingeben, wenn sie über mögliche
          eingetretene Störungen berichtet und diese gespeichert haben.
        </div>
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
        :save-text="SAVE_CONTINUE"
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
import {
  CONTINUE_QUERY_PARAM,
  ROUTE_EREIGNISSE,
  SAVE_CONTINUE,
} from "@/constants.ts";
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
const { isVorfaelleMaintained } = storeToRefs(useEreignisStore());

const schliessungsuhrzeitValidForm = ref<null | boolean>(null);

const isSaveButtonDisabled = computed(
  () =>
    schliessungsuhrzeitValidForm.value !== true || !isVorfaelleMaintained.value
);

defineProps<{
  type: InputFeedbackTypeEnum;
}>();

async function onEreignisseBearbeiten() {
  await router.push({
    name: ROUTE_EREIGNISSE,
    query: { [CONTINUE_QUERY_PARAM]: "1" },
  });
}

async function onSaveSchliessungsuhrzeitClicked() {
  await schliessungsuhrzeitActions.sendSchliessungsuhrzeit();
  await router.push(getNextRoute());
}
</script>
