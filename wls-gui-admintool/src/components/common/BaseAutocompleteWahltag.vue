<template>
  <v-autocomplete
    clearable
    chips
    label="Wahltage auswählen"
    :items="props.items"
    item-title="wahltag"
    :multiple="props.multiple"
    variant="outlined"
    v-model="selected"
  ></v-autocomplete>
</template>

<script setup lang="ts">
import type { WahltagDTO } from "@/api/wls-clients/generated-admin-api";

import { VAutocomplete } from "vuetify/components";

const props = defineProps({
  /**
   * Wahltage to select
   */
  items: {
    type: Array<WahltagDTO>,
    required: true,
  },
  multiple: {
    type: Boolean,
    default: true,
    required: false,
  },
});

const selected = defineModel();
</script>
