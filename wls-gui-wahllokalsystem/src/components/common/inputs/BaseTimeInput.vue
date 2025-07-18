<template>
  <v-text-field
    :model-value="toHhMm(modelValue)"
    :rules="[DATE_REQUIRED]"
    label="Uhrzeit"
    type="time"
    clearable
    data-test="base-time-input"
    @update:model-value="onTimeChanged"
  />
</template>

<script setup lang="ts">
import type { PropType } from "vue";

import { VTextField } from "vuetify/components";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { DATE_REQUIRED } from "@/util/rules.ts";

const { toHhMm, updateTimeOfDateObject } = useDateTimeFormatter();

const modelValue = defineModel({
  type: Object as PropType<Date>,
});

const emit = defineEmits(["update:model-value"]);

const onTimeChanged = (newTime: string) => {
  const currentTime = modelValue?.value || new Date();

  const updatedTime = updateTimeOfDateObject(newTime, currentTime);
  emit("update:model-value", updatedTime);
};
</script>
