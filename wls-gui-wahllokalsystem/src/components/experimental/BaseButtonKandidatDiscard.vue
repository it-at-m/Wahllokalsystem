<template>
  <v-checkbox
    v-model="modelValue"
    hide-details
    :color="iconColor"
    :base-color="iconColor"
    :true-value="false"
    true-icon="$kandidatNonRemoved"
    :false-value="true"
    false-icon="$kandidatRemoved"
  />
</template>

<script setup lang="ts">
import { computed } from "vue";

const modelValue = defineModel({
  type: Boolean,
});

defineProps({
  useSwitchComponent: {
    type: Boolean,
    required: false,
    default: false,
  },
});

const iconColor = computed(() => (!modelValue.value ? "success" : "error"));
</script>
