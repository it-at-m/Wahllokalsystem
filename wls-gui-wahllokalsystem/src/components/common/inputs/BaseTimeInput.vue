<template>
  <v-text-field
    :model-value="toHhMm(modelValue)"
    :rules="[REQUIRED]"
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
import { REQUIRED } from "@/util/rules.ts";

const { toHhMm, updateTimeOfDateObject } = useDateTimeFormatter();

const modelValue = defineModel({
  type: Object as PropType<Date>,
});

const onTimeChanged = (newTime: string) => {
  const currentDate = modelValue?.value || new Date();

  modelValue.value = updateTimeOfDateObject(newTime, currentDate);
};
</script>
