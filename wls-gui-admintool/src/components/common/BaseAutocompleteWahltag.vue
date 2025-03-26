<template>
  <v-autocomplete
    clearable
    chips
    label="Wahltage auswählen"
    :items="props.items"
    :multiple="props.multiple"
    variant="outlined"
    v-model="selected"
  ></v-autocomplete>
</template>

<script setup lang="ts">
import { VAutocomplete } from "vuetify/components";

const props = defineProps({
  /**
   * Wahltage to select
   */
  items: {
    type: Array<string>,
    required: true,
  },
  multiple: {
    type: Boolean,
    default: true,
    required: false,
  },
});

const selected = defineModel({ type: String });
</script>
