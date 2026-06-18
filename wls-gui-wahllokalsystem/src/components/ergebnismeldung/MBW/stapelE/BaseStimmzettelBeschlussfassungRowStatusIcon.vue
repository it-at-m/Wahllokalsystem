<template>
  <v-icon
    :icon="getIcon()"
    variant="text"
    :color="getColor()"
  />
</template>

<script setup lang="ts">
import type { BedenklicherStimmzettel } from "@/types/ergebnismeldung/MBW/bedenklicheStimmzettel/BedenklicherStimmzettel.ts";
import type { PropType } from "vue";

import { computed } from "vue";

import { ValidityEnum } from "@/types/ergebnismeldung/MBW/bedenklicheStimmzettel/ValidityEnum.ts";

const ereignisModel = defineModel({
  type: Object as PropType<BedenklicherStimmzettel>,
  required: true,
});

const areRowInputsValid = computed(() => {
  return (
    ereignisModel.value &&
    ereignisModel.value.validity &&
    (ereignisModel.value.supplements.length == 0 ||
      ereignisModel.value.validity == ValidityEnum.PARTIAL_VALID)
  );
});

function getIcon() {
  return areRowInputsValid.value ? "$valid" : "$edit";
}

function getColor() {
  return areRowInputsValid.value ? "success" : "error";
}
</script>
