<template>
  <v-text-field
    :model-value="toHhMm(modelValue)"
    :rules="[DATE_REQUIRED]"
    label="Uhrzeit"
    type="time"
    clearable
    data-test="baseTimeInput"
    @update:model-value="onTimeChanged"
  />
</template>

<script setup lang="ts">
import type { PropType } from "vue";

import { VTextField } from "vuetify/components";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { DATE_REQUIRED } from "@/util/rules.ts";

const { toHhMm, extractTimeFromString } = useDateTimeFormatter();

const modelValue = defineModel({
  type: Object as PropType<Date>,
});

const emit = defineEmits(["update:model-value"]);

const onTimeChanged = (newTime: string) => {
  const currentTime = modelValue?.value || new Date();

  const updatedTime = extractTimeFromString(newTime, currentTime);
  emit("update:model-value", updatedTime);
};
</script>
