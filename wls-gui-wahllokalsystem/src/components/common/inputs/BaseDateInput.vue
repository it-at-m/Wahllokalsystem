<template>
  <v-text-field
    :model-value="formatedDate"
    type="date"
    :rules="[REQUIRED]"
    label="Datum"
    clearable
    @update:model-value="onModelValueChanged"
  />
</template>

<script setup lang="ts">
import { computed } from "vue";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { REQUIRED } from "@/util/rules.ts";

const { toYyyyMmDd } = useDateTimeFormatter();

const model = defineModel<Date>();

const formatedDate = computed(() =>
  model.value ? toYyyyMmDd(model.value) : null
);

function onModelValueChanged(newValue: string | null) {
  model.value = newValue ? new Date(newValue) : undefined;
}
</script>
