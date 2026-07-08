<template>
  <v-chip
    color="success"
    size="small"
    data-test="activeChip"
  >
    <slot />
  </v-chip>
</template>

<script setup lang="ts">
import { VChip } from "vuetify/components";
</script>

<style scoped></style>
