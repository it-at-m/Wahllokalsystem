<template>
  <v-text-field
    ref="inputRef"
    v-model="formattedValue"
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
    type: Array<(value: number) => string | boolean>,
    required: false,
  },
});

const modelValue = defineModel({ type: Number });

const currencyInputOptions = computed(() => {
  return {
    currency: "EUR", // currency has to be set, even if not displayed
    currencyDisplay: CurrencyDisplay.hidden,
    useGrouping: false,
  };
});

// inputRef needs to be present for vue-currency-input to work
// formattedValue avoids, that characters can be typed
const { inputRef, formattedValue, setValue } = useCurrencyInput(
  currencyInputOptions.value
);

// if the value of the input is changed externally (and not only by user input)
// see https://dm4t2.github.io/vue-currency-input/guide.html#external-props-changes.
watch(
  () => modelValue.value,
  (value) => {
    setValue(value === undefined ? null : value);
  }
);
</script>
