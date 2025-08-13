<template>
  <v-text-field
    :model-value="toHhMm(modelValue)"
    :rules="[required]"
    label="Uhrzeit"
    type="time"
    clearable
    data-test="baseTimeInput"
    @update:model-value="onTimeChanged"
  />
</template>

<script setup lang="ts">
import type { PropType } from "vue";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useRules } from "@/composables/common/rules.ts";

const { required } = useRules();

const { toHhMm, getDateFromTimeString } = useDateTimeFormatter();

const modelValue = defineModel({
  type: Object as PropType<Date>,
});

function onTimeChanged(time: string | undefined) {
  if (time) {
    modelValue.value = getDateFromTimeString(time);
  } else {
    modelValue.value = undefined;
  }
}
</script>
