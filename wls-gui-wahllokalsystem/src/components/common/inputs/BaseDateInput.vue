<template>
  <v-text-field
    :model-value="modelValue?.toISOString().split('T')[0]"
    :rules="[DATE_REQUIRED]"
    label="Datum"
    type="date"
    clearable
    data-test="baseDateInput"
    @update:model-value="onDateChanged"
  />
</template>

<script setup lang="ts">
import type { PropType } from "vue";

import { VTextField } from "vuetify/components";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { DATE_REQUIRED } from "@/util/rules.ts";

const { extractDateFromString } = useDateTimeFormatter();

const modelValue = defineModel({
  type: Object as PropType<Date>,
});

const emit = defineEmits(["update:model-value"]);
const onDateChanged = (newDate: string) => {
  const currentTime = modelValue?.value || new Date();

  const updatedDate = extractDateFromString(newDate, currentTime);
  if (updatedDate) {
    emit("update:model-value", updatedDate);
  }
};
</script>
