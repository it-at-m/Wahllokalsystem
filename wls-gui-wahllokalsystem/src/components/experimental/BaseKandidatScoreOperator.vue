<template>
  <div>
    <!-- bearbeitung per mausklick kommt erst später -->
    <!--    <v-number-input
      v-model="modelVotes"
      :persistent-clear="false"
      :clearable="false"
      control-variant="stacked"
      density="compact"
      hide-details
      :min="0"
    />-->
  </div>
</template>

<script setup lang="ts">
const modelVotes = defineModel({
  type: Number,
});
</script>
