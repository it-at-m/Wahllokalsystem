<template>
  <div>
    <v-btn @click="onAddClicked">+</v-btn>
    <v-btn @click="onSubtractClicked">-</v-btn>
  </div>
</template>

<script setup lang="ts">
const emit = defineEmits<{
  add: [count: number];
  subtract: [count: number];
}>();

function onAddClicked() {
  emit("add", 1);
}

function onSubtractClicked() {
  emit("subtract", 1);
}
</script>
