<template>
  <v-text-field
    :model-value="formatedDate"
    type="date"
    :rules="[required]"
    label="Datum"
    @update:model-value="onModelValueChanged"
  />
</template>

<script setup lang="ts">
import { computed } from "vue";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useRules } from "@/composables/common/rules.ts";

const { required } = useRules();

const { toYyyyMmDd } = useDateTimeFormatter();

const model = defineModel<Date>();

const formatedDate = computed(() =>
  model.value ? toYyyyMmDd(model.value) : null
);

function onModelValueChanged(newValue: string | null) {
  model.value = newValue ? new Date(newValue) : undefined;
}
</script>
