<template>
  <v-text-field
    :model-value="toHhMm(modelValue)"
    :rules="[REQUIRED]"
    label="Uhrzeit"
    type="time"
    clearable
    max-width="150"
    class="mt-5"
    @update:model-value="onTimeChanged"
  ></v-text-field>
</template>

<script setup lang="ts">
import type { PropType } from "vue";

import { VTextField } from "vuetify/components";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { REQUIRED } from "@/util/rules.ts";

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
