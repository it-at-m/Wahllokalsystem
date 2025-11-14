<template>
  <v-number-input
    v-model="modelValue"
    :label="props.label"
    :rules="props.rules"
    control-variant="hidden"
    @keydown.capture="onKeyDownCapture"
  />
</template>

<script setup lang="ts">
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
});

function onKeyDownCapture(e: KeyboardEvent) {
  if (e.key === "ArrowUp" || e.key === "ArrowDown") {
    e.preventDefault(); // verhindert das native Increment/Decrement (Standardaktion des Browsers)
    e.stopPropagation(); // stoppt weitere Verarbeitung von anderen Listeners
  }
}
</script>
