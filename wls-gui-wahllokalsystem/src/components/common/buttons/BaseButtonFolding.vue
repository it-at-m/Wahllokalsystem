<template>
  <v-btn
    :icon="icon"
    variant="flat"
    @click="onFoldingStateClicked()"
  />
</template>

<script setup lang="ts">
import { computed } from "vue";

const isExpandedModel = defineModel("modelValue", {
  type: Boolean,
  required: false,
});

const icon = computed(() => (isExpandedModel.value ? "$collapse" : "$expand"));

function onFoldingStateClicked() {
  isExpandedModel.value = !isExpandedModel.value;
}
</script>
