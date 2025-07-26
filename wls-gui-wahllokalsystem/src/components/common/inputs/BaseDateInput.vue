<template>
  <v-text-field
    :model-value="formatedDate"
    type="date"
    clearable
    @update:model-value="onModelValueChanged"
    @clear="onClearClicked"
  />
</template>

<script setup lang="ts">
import { computed } from "vue";
import { VTextField } from "vuetify/components";

const model = defineModel<Date>();

const formatedDate = computed(() => {
  return (
    model.value?.toLocaleDateString("en-CA", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    }) ?? null
  );
});

function onClearClicked() {
  model.value = undefined;
}

function onModelValueChanged(newValue?: string) {
  if (newValue) {
    const valueAsDate = new Date(newValue);
    const cloneOfModel = new Date(model.value);
    cloneOfModel.setFullYear(
      valueAsDate.getFullYear(),
      valueAsDate.getMonth(),
      valueAsDate.getDate()
    );
    model.value = cloneOfModel;
  } else {
    model.value = undefined;
  }
}
</script>
