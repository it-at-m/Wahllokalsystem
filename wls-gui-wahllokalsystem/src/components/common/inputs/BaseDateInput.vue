<template>
  <v-text-field
    :model-value="getFormattedValue(modelValue)"
    :rules="[
      REQUIRED,
      (value) => DATE_NOT_BEFORE_WAHLTAG(value, currentUserWahltag),
    ]"
    label="Datum"
    type="date"
    clearable
    data-test="base-date-input"
    @update:model-value="onDateChanged"
  />
</template>

<script setup lang="ts">
import type { PropType } from "vue";

import { VTextField } from "vuetify/components";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { DATE_NOT_BEFORE_WAHLTAG, REQUIRED } from "@/util/rules.ts";

const { updateDateOfDateObject } = useDateTimeFormatter();
const { currentUserWahltag } = useUserStore();

const modelValue = defineModel({
  type: Object as PropType<Date>,
});

const onDateChanged = (newDate: string) => {
  const currentTime = modelValue?.value || new Date();

  const updatedDate = updateDateOfDateObject(newDate, currentTime);
  if (updatedDate) {
    modelValue.value = updatedDate;
  }
};

const getFormattedValue = (date: Date | undefined) => {
  if (!date) return "";

  const correctedDate = new Date(
    date.getTime() - date.getTimezoneOffset() * 60000
  );
  return correctedDate.toISOString().split("T")[0];
};
</script>
