<template>
  <v-text-field
    ref="inputRef"
    v-model="formattedValue"
    :label="props.label"
    clearable
    variant="solo"
    :rules="activeRules"
    type="number"
    hide-spin-buttons
    @click:control="activateRules()"
  />
</template>

<script setup lang="ts">
import { computed, watch } from "vue";
import { CurrencyDisplay, useCurrencyInput } from "vue-currency-input";
import { VTextField } from "vuetify/components";

const props = defineProps({
  label: {
    type: String,
    required: false,
    default: "Zahl eingeben",
  },
  rules: {
    type: Array<(value: number) => string | boolean>,
    required: false,
    default: [],
  },
  modelValue: {
    type: [Number, null],
    required: false,
    default: null,
  },
});

const activeRules: ((value: number) => string | boolean)[] = [];
const emit = defineEmits<{ "update:modelValue": [value: number] }>();

const currencyInputOptions = computed(() => {
  return {
    currency: "EUR", // currency has to be set, even if not displayed
    currencyDisplay: CurrencyDisplay.hidden,
    useGrouping: false,
    precision: 0,
  };
});

// inputRef needs to be present for vue-currency-input to work
// formattedValue avoids, that characters can be typed
const { inputRef, formattedValue, setValue } = useCurrencyInput(
  currencyInputOptions.value,
  // disable autoEmit: `true` would emit event "change" instead of "update:modelValue" + would be triggered every time the field is clicked into or left
  // see: https://dm4t2.github.io/vue-currency-input/guide.html#auto-emit
  false
);

// if the value of the input is changed externally (and not only by user input)
// see https://dm4t2.github.io/vue-currency-input/guide.html#external-props-changes.
watch(
  () => props.modelValue,
  (value) => {
    setValue(value === undefined ? null : value);
  }
);

watch(formattedValue, (newValue) => {
  if (newValue) {
    emit("update:modelValue", Number.parseInt(newValue));
  }
});

function activateRules() {
  return activeRules.push(...props.rules);
}
</script>
