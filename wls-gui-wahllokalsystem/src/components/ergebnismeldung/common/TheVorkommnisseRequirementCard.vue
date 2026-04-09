<template>
  <base-feedback-card
    :title="
      'Vorkommnisse ' +
      (type === InputFeedbackTypeEnum.error ? 'melden' : 'aktualisieren')
    "
    :type="type"
  >
    <div v-if="type === InputFeedbackTypeEnum.error">
      Sie können die Niederschrift erst ausdrucken, wenn Sie über mögliche
      eingetretene Störungen berichtet haben.
    </div>
    <div v-else>
      Wenn sich während der Auszählung weitere Vorkommnisse ereignet haben,
      können diese hier erfasst werden.
    </div>
    <template #actions>
      <base-text-button @click="onNavigationClicked"
        >Zu den Ereignissen</base-text-button
      >
    </template>
  </base-feedback-card>
</template>

<script setup lang="ts">
import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseFeedbackCard from "@/components/common/cards/BaseFeedbackCard.vue";
import { CONTINUE_QUERY_PARAM, ROUTE_EREIGNISSE } from "@/constants.ts";
import router from "@/plugins/router.ts";
import { InputFeedbackTypeEnum } from "@/types/common/InputFeedbackTypeEnum.ts";

defineProps<{
  type: InputFeedbackTypeEnum;
}>();

function onNavigationClicked() {
  router.push({
    name: ROUTE_EREIGNISSE,
    params: {
      continue: CONTINUE_QUERY_PARAM,
    },
  });
}
</script>
