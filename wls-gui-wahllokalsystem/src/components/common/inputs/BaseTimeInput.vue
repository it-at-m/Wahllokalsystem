<template>
  <v-text-field
    :model-value="toHhMm(modelValue)"
    :rules="[required]"
    label="Uhrzeit"
    type="time"
    data-test="baseTimeInput"
    @update:model-value="onTimeChanged"
  />
</template>

<script setup lang="ts">
import type { PropType } from "vue";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useDateTimeUtils } from "@/composables/common/dateTimeUtils.ts";
import { useRules } from "@/composables/common/rules.ts";

const { required } = useRules();

const { toHhMm } = useDateTimeFormatter();
const { createTodayWithTime } = useDateTimeUtils();

const modelValue = defineModel({
  type: Object as PropType<Date>,
});

function onTimeChanged(time: string | undefined) {
  if (time) {
    modelValue.value = createTodayWithTime(time);
  } else {
    modelValue.value = undefined;
  }
}
</script>
