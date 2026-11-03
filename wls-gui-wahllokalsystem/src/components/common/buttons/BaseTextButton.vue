<template>
  <v-btn
    class="primary-button-border"
    active-color="primary"
    variant="flat"
    :disabled="isDisabled || loading"
    :loading="loading"
  >
    <slot />
  </v-btn>
</template>

<script setup lang="ts">
defineProps<{
  isDisabled?: boolean;
  loading?: boolean;
}>();
</script>
