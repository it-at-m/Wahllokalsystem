<template>
  <div>
    <v-number-input
      v-model="modelVotes"
      control-variant="split"
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
