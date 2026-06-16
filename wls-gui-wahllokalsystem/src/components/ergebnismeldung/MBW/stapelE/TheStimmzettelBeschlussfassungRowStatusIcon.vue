<template>
  <v-icon
    :index="index"
    :icon="getIcon()"
    variant="text"
    :color="getColor()"
    :data-test="`rowstatus-icon-${index}`"
  />
</template>

<script setup lang="ts">
import type { BedenklicherStimmzettel } from "@/types/ergebnismeldung/MBW/bedenklicheStimmzettel/BedenklicherStimmzettel.ts";
import type { PropType } from "vue";

import { ValidityEnum } from "@/types/ergebnismeldung/MBW/bedenklicheStimmzettel/ValidityEnum.ts";

defineProps<{
  index: number;
}>();

const ereignisModel = defineModel({
  type: Object as PropType<BedenklicherStimmzettel>,
  required: true,
});

function getIcon() {
  return areRowInputsValid() ? "$valid" : "$edit";
}

function getColor() {
  return areRowInputsValid() ? "success" : "error";
}

function areRowInputsValid() {
  return (
    ereignisModel.value &&
    ereignisModel.value.validity &&
    (ereignisModel.value.supplements.length == 0 ||
      ereignisModel.value.validity == ValidityEnum.PARTIAL_VALID)
  );
}
</script>
