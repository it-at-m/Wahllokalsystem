<template>
  <v-card
    class="border-lg"
    :class="borderColor"
  >
    <v-card-title
      style="font-size: 1rem"
      :class="bgColorAndBold"
    >
      {{ title }}
    </v-card-title>
    <v-card-text>
      <div class="d-flex align-center">
        <v-icon
          :color="iconColor"
          class="mr-5"
          :icon="icon"
          size="x-large"
        />
        <slot />
      </div>
      <div
        v-if="hasAdditionalFeedback"
        class="mt-4"
      >
        <slot name="additionalFeedback" />
      </div>
    </v-card-text>
    <v-card-actions v-if="submitButtonText">
      <base-text-button @click="$emit('submit')">{{
        submitButtonText
      }}</base-text-button>
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import type { InputFeedbackTypeEnum } from "@/types/common/InputFeedbackTypeEnum.ts";

import { computed, useSlots } from "vue";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import { useInputFeedbackUtils } from "@/composables/common/inputFeedbackUtils.ts";

const {
  getBorderColorForInputFeedbackType,
  getIconColorForInputFeedbackType,
  getIconForInputFeedbackType,
  getBackgroundColorAndBoldTextForInputFeedbackType,
} = useInputFeedbackUtils();

const slots = useSlots();

const props = defineProps<{
  title: string;
  type: InputFeedbackTypeEnum;
  submitButtonText?: string;
}>();

defineEmits<{
  submit: [];
}>();

const icon = computed(() => getIconForInputFeedbackType(props.type));
const iconColor = computed(() => getIconColorForInputFeedbackType(props.type));
const borderColor = computed(() =>
  getBorderColorForInputFeedbackType(props.type)
);
const bgColorAndBold = computed(() =>
  getBackgroundColorAndBoldTextForInputFeedbackType(props.type)
);

const hasAdditionalFeedback = computed(() => !!slots?.additionalFeedback);
</script>
