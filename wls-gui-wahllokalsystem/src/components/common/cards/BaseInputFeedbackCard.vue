<template>
  <v-card
    class="border-lg"
    :class="borderColor"
  >
    <v-card-title style="font-size: 1rem">{{ title }}</v-card-title>
    <v-card-text>
      <div class="d-flex align-center">
        <v-icon
          :color="iconColor"
          class="mr-2"
          :icon="icon"
        />
        <div :class="textColor">
          <slot />
        </div>
      </div>
      <div
        v-if="hasAdditionalFeedback"
        class="mt-4"
      >
        <slot name="additionalFeedback" />
      </div>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import type { InputFeedbackTypeEnum } from "@/types/common/InputFeedbackTypeEnum.ts";

import { computed, useSlots } from "vue";
import { VCard, VCardText, VCardTitle, VIcon } from "vuetify/components";

import { useInputFeedbackUtils } from "@/composables/common/inputFeedbackUtils.ts";

const {
  getBorderColorForInputFeedbackType,
  getIconColorForInputFeedbackType,
  getIconForInputFeedbackType,
  getTextColorForInputFeedbackType,
} = useInputFeedbackUtils();

const slots = useSlots();

const props = defineProps<{
  title: string;
  type: InputFeedbackTypeEnum;
}>();

const icon = computed(() => getIconForInputFeedbackType(props.type));
const iconColor = computed(() => getIconColorForInputFeedbackType(props.type));
const textColor = computed(() => getTextColorForInputFeedbackType(props.type));
const borderColor = computed(() =>
  getBorderColorForInputFeedbackType(props.type)
);

const hasAdditionalFeedback = computed(() => !!slots?.additionalFeedback);
</script>
