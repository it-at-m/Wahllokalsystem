<template>
  <v-autocomplete
    clearable
    label="Wahltag auswählen"
    :items="props.items"
    item-title="wahltag"
    v-model="selected"
    @update:model-value="$emit('update:selected', $event)"
    return-object
  ></v-autocomplete>
</template>

<script setup lang="ts">
import type { WahltagDTO } from "@/api/wls-clients/generated-admin-api";

import { VAutocomplete } from "vuetify/components";

const props = defineProps({
  items: {
    type: Array<WahltagDTO>,
    required: true,
  },
});

const selected = defineModel<WahltagDTO>();

const emit = defineEmits(["update:selected"]);
</script>
