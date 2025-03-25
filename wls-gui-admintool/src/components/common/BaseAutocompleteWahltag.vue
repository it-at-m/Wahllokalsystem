<template>
  <v-autocomplete
    clearable
    chips
    label="Wahltage auswählen"
    :items="props.items"
    multiple
    variant="outlined"
  ></v-autocomplete>
</template>

<script setup lang="ts">
import { VAutocomplete } from "vuetify/components";

const props = defineProps({
  /**
   * Wahltage to select
   */
  items: {
    type: Array<String>,
    required: true,
  },
});
</script>
