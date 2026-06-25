<template>
  <v-number-input
    v-model="modelValue"
    :label="props.label"
    :rules="[
      ...props.rules,
      minNumber(props.minValid),
      maxNumber(props.maxValid),
    ]"
    control-variant="hidden"
    @keydown.capture="onKeyDownCapture"
    @keydown.enter.prevent
  />
</template>

<script setup lang="ts">
import { useRules } from "@/composables/common/rules.ts";
import {
  NUMBER_INPUT_DEFAULT_MAX,
  NUMBER_INPUT_DEFAULT_MIN,
} from "@/constants.ts";

const { minNumber, maxNumber } = useRules();

const modelValue = defineModel<number | null | undefined>({ required: true });

const props = defineProps({
  label: {
    type: String,
    required: false,
    default: "Anzahl",
  },
  rules: {
    type: Array<(value: number) => string | boolean>,
    required: false,
    default: [],
  },
  minValid: {
    type: Number,
    required: false,
    default: NUMBER_INPUT_DEFAULT_MIN,
  },
  maxValid: {
    type: Number,
    required: false,
    default: NUMBER_INPUT_DEFAULT_MAX,
  },
});

function onKeyDownCapture(e: KeyboardEvent) {
  if (e.key === "ArrowUp" || e.key === "ArrowDown") {
    e.preventDefault(); // verhindert das native Increment/Decrement (Standardaktion des Browsers)
    e.stopPropagation(); // stoppt weitere Verarbeitung von anderen Listeners
  }
}
</script>
