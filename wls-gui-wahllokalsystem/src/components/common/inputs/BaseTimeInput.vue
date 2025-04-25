<template>
  <v-text-field
    :model-value="toHhMm(schliessungsuhrzeit)"
    :rules="[REQUIRED]"
    label="Uhrzeit"
    type="time"
    clearable
    max-width="175"
    class="centered-input"
    density="comfortable"
    @update:model-value="(value) => onSchliessungsuhrzeitChanged(value)"
  ></v-text-field>
</template>

<script setup lang="ts">
import type { PropType } from "vue";

import { VTextField } from "vuetify/components";

import { useFormatter } from "@/composables/common/formatter.ts";
import { REQUIRED } from "@/util/rules.ts";

const { toHhMm } = useFormatter();

const schliessungsuhrzeit = defineModel({
  type: Object as PropType<Date>,
});

function onSchliessungsuhrzeitChanged(time: string | undefined) {
  if (time && schliessungsuhrzeit.value) {
    const [hours, minutes] = time.split(":").map(Number);
    schliessungsuhrzeit.value.setHours(hours, minutes);
  } else {
    schliessungsuhrzeit.value = undefined;
  }
}
</script>

<style scoped>
.centered-input :deep(input) {
  text-align: center;
}
</style>
