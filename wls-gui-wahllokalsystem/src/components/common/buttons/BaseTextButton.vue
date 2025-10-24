<template>
  <v-btn
    class="base-text-button"
    active-color="primary"
    variant="flat"
  >
    <slot />
  </v-btn>
</template>

<style scoped>
.base-text-button {
  border: 1px solid rgb(var(--v-theme-primary));
}
</style>
