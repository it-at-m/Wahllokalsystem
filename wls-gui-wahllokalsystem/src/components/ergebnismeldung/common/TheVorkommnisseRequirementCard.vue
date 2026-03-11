<template>
  <base-input-feedback-card
    :title="
      'Vorkommnisse ' +
      (type === InputFeedbackTypeEnum.error ? 'melden' : 'aktualisieren')
    "
    :type="type"
    submit-button-text="Zu den Ereignissen"
    @submit="onSubmitClicked"
  >
    <div v-if="type === InputFeedbackTypeEnum.error">
      Sie können die Niederschrift erst ausdrucken, wenn Sie über mögliche
      eingetretene Störungen berichtet haben.
    </div>
    <div v-else>
      Wenn sich während der Auszählung weitere Vorkommnisse ereignet haben,
      können diese hier erfasst werden.
    </div>
  </base-input-feedback-card>
</template>

<script setup lang="ts">
import BaseInputFeedbackCard from "@/components/common/cards/BaseInputFeedbackCard.vue";
import { ROUTE_EREIGNISSE } from "@/constants.ts";
import router from "@/plugins/router.ts";
import { InputFeedbackTypeEnum } from "@/types/common/InputFeedbackTypeEnum.ts";

defineProps<{
  type: InputFeedbackTypeEnum;
}>();

function onSubmitClicked() {
  router.push({ name: ROUTE_EREIGNISSE });
}
</script>
