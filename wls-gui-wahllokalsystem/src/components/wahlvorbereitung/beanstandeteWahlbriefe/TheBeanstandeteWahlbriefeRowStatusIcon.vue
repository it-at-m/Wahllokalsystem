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
import { VIcon } from "vuetify/components";

import { useWahlenStore } from "@/stores/wahlenStore.ts";

const { wahlen } = storeToRefs(useWahlenStore());

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
  return wahlen.value
    ? wahlen.value.every(
        (wahl) =>
          wahl.beanstandeteWahlbriefe &&
          wahl.beanstandeteWahlbriefe[props.index] &&
          !!wahl.beanstandeteWahlbriefe[props.index]
      )
    : false;
}
</script>
