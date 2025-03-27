<template>
  <v-autocomplete
    clearable
    label="Wahltag auswählen"
    :items="props.items"
    item-title="wahltag"
    v-model="modelValue"
    return-object
  ></v-autocomplete>
</template>

<script setup lang="ts">
import type { WahltagDTO } from "@/api/wls-clients/generated-admin-api";
import type { PropType } from "vue";

import { VAutocomplete } from "vuetify/components";

const props = defineProps({
  items: {
    type: Array<WahltagDTO>,
    required: true,
  },
});

const modelValue = defineModel({
  type: Object as PropType<WahltagDTO>,
  required: false,
});
</script>
