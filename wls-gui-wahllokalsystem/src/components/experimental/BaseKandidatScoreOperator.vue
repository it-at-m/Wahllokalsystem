<template>
  <div>
    <v-number-input
      v-model="modelVotes"
      control-variant="stacked"
      density="compact"
      hide-details
      :min="0"
    />
  </div>
</template>

<script setup lang="ts">
const modelVotes = defineModel({
  type: Number,
});
</script>
