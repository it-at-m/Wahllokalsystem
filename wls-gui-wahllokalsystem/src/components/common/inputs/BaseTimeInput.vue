<template>
  <v-text-field
    :model-value="toHhMm(modelValue)"
    :rules="[REQUIRED]"
    label="Uhrzeit"
    type="time"
    clearable
    max-width="150"
    class="mt-5"
    @update:model-value="(value) => onSchliessungsuhrzeitChanged(value)"
  ></v-text-field>
</template>

<script setup lang="ts">
import type { PropType } from "vue";

import { VTextField } from "vuetify/components";

import { useFormatter } from "@/composables/common/formatter.ts";
import { REQUIRED } from "@/util/rules.ts";

const { toHhMm } = useFormatter();

const modelValue = defineModel({
  type: Object as PropType<Date>,
});

function onSchliessungsuhrzeitChanged(time: string | undefined) {
  if (time) {
    const [hours, minutes] = time.split(":").map(Number);
    const date = new Date();
    date.setHours(hours, minutes);
    modelValue.value = date;
  } else {
    modelValue.value = undefined;
  }
}
</script>
