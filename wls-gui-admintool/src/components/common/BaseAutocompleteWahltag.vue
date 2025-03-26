<template>
  <v-autocomplete
    clearable
    label="Wahltag auswählen"
    :items="props.items"
    item-title="wahltag"
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
});

const selected = defineModel();
</script>
