<template>
  <v-btn
    class="border-primary"
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
