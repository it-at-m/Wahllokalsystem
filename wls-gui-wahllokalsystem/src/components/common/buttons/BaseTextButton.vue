<template>
  <v-btn
    class="base-text-button"
    active-color="primary"
    variant="flat"
    :disabled="isDisabled"
  >
    <slot />
  </v-btn>
</template>

<script setup lang="ts">
defineProps<{
  isDisabled?: boolean;
}>();
</script>

<style scoped>
.base-text-button {
  border: 1px solid rgb(var(--v-theme-primary));
}
</style>
