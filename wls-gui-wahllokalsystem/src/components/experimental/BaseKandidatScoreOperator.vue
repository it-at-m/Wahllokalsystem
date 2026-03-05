<template>
  <div>
    <v-number-input
      v-model="modelVotes"
      control-variant="stacked"
      density="compact"
      hide-details
      :min="0"
    ></v-number-input>
  </div>
</template>

<script setup lang="ts">
const modelVotes = defineModel({
  type: Number,
});

const emit = defineEmits<{
  add: [count: number];
  subtract: [count: number];
  discard: [];
  revokeDiscard: [];
}>();
</script>
