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
import { VTextField } from "vuetify/components";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { REQUIRED } from "@/util/rules.ts";

const { toIsoDate } = useDateTimeFormatter();

const model = defineModel<Date>();

const formatedDate = computed(() =>
  model.value ? toIsoDate(model.value) : null
);

function onModelValueChanged(newValue: string | null) {
  model.value = newValue ? new Date(newValue) : undefined;
}
</script>
