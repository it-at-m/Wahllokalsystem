<template>
  <v-checkbox
    v-model="modelValue"
    hide-details
    :true-value="false"
    true-icon="$kandidatNonRemoved"
    :false-value="true"
    false-icon="$kandidatRemoved"
  />
</template>

<script setup lang="ts">
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
</script>
