<template>
  <v-text-field
    :v-model="toHhMm(schliessungsuhrzeit)"
    :rules="[REQUIRED]"
    label="Uhrzeit"
    type="time"
    clearable
    max-width="200"
    class="centered-input"
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

// todo: add rule: schliessungsuhrzeit darf nicht in der zukunft liegen (anderes issue??)
</script>

<style scoped>
.centered-input :deep(input) {
  text-align: center;
}
</style>
