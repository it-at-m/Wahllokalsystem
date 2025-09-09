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
import { storeToRefs } from "pinia";

import { useWahlenStore } from "@/stores/wahlenStore.ts";

const { wahlenState } = storeToRefs(useWahlenStore());

const props = defineProps<{
  index: number;
}>();

function getIcon() {
  return areRowInputsValid() ? "$valid" : "$edit";
}

function getColor() {
  return areRowInputsValid() ? "success" : "error";
}

function areRowInputsValid() {
  return wahlenState.value.wahlen
    ? wahlenState.value.wahlen.every(
        (wahl) =>
          wahl.beanstandeteWahlbriefe &&
          wahl.beanstandeteWahlbriefe[props.index]
      )
    : false;
}
</script>
