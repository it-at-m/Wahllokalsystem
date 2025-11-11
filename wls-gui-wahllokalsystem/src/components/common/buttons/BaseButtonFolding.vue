<template>
  <v-btn
    :icon="icon"
    variant="flat"
    @click="onFoldingStateClicked()"
  />
</template>

<script setup lang="ts">
import { computed } from "vue";

const isFoldedModel = defineModel("modelValue", {
  type: Boolean,
  required: false,
});

const icon = computed(() => (isFoldedModel.value ? "$expand" : "$collapse"));

function onFoldingStateClicked() {
  isFoldedModel.value = !isFoldedModel.value;
}
</script>
