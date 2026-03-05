<template>
  <div>
    <v-number-input
      v-model="modelVotes"
      control-variant="stacked"
      density="compact"
      hide-details
      :min="0"
    ></v-number-input>
    <v-switch
      hide-details
      inset
    />
    <v-btn @click="onAddClicked">+</v-btn>
    <v-btn @click="onSubtractClicked">-</v-btn>
    <v-btn @click="onDiscardClicked">🚮</v-btn>
    <v-btn @click="onRevokeDiscardClicked">🚯</v-btn>
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

function onAddClicked() {
  emit("add", 1);
}

function onSubtractClicked() {
  emit("subtract", 1);
}

function onDiscardClicked() {
  emit("discard");
}

function onRevokeDiscardClicked() {
  emit("revokeDiscard");
}
</script>
