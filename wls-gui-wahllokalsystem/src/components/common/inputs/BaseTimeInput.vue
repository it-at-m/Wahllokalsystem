<template>
  <v-text-field
    :model-value="toHhMm(modelValue)"
    :rules="[REQUIRED]"
    label="Uhrzeit"
    type="time"
    clearable
    data-test="base-time-input"
    @update:model-value="onTimeChanged"
    @click:clear="resetTimeKeepDateAndTriggerUpdateOnce"
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
  const updatedTime = updateTimeOfDateObject(newTime, currentDate);
  if (updatedTime) {
    modelValue.value = updatedTime;
  }
};

const resetTimeKeepDateAndTriggerUpdateOnce = () => {
  if (modelValue.value != undefined) {
    const newDateForReset = new Date();
    modelValue.value.setHours(
      newDateForReset.getUTCHours() + 2,
      newDateForReset.getMinutes()
    );
    const timeString = modelValue.value.toLocaleTimeString([], {
      hour: "2-digit",
      minute: "2-digit",
    });
    onTimeChanged(timeString);
  }
};
</script>
