<template>
  <v-text-field
    ref="inputRef"
    v-model="modelValue"
    :label="props.label"
    clearable
    variant="solo"
    :rules="props.rules"
  >
  </v-text-field>
</template>

<script setup lang="ts">
import { computed, watch } from "vue";
import { CurrencyDisplay, useCurrencyInput } from "vue-currency-input";
import { VTextField } from "vuetify/components";

const props = defineProps({
  label: {
    type: String,
    required: false,
  },
  rules: {
    type: Array,
    required: false,
  },
});

const currencyInputOptions = computed(() => {
  return {
    currency: "EUR", // currency has to be set, even if not displayed
    currencyDisplay: CurrencyDisplay.hidden,
    useGrouping: false,
  };
});

// inputRef needs to be present for vue-currency-input to work
// formattedValue avoids, that characters can be typed
const { inputRef, modelValue, setValue } = useCurrencyInput(
  currencyInputOptions.value
);

// see https://dm4t2.github.io/vue-currency-input/guide.html#external-props-changes.
watch(
  () => props.modelValue,
  (value) => {
    setValue(value === undefined ? null : value);
  }
);
</script>
