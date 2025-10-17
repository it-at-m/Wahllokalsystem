<template>
  <v-btn
    class="base-text-button"
    variant="elevated"
  >
    <slot name="default">Aktualisieren</slot>
  </v-btn>
</template>

<style scoped>
.base-text-button {
  border: 1px solid rgb(var(--v-theme-primary));
}
</style>
