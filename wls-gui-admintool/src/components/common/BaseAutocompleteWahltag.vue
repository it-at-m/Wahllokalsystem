<template>
  <v-autocomplete
    clearable
    label="Wahltag auswählen"
    :items="props.items"
    item-title="wahltag"
    v-model="modelValue"
    return-object
    data-testr="autocompleteWahltage"
  ></v-autocomplete>
</template>

<script setup lang="ts">
import type { Wahltag } from "@/types/wahltag/Wahltag.ts";
import type { PropType } from "vue";

import { VAutocomplete } from "vuetify/components";

const props = defineProps({
  items: {
    type: Array<Wahltag>,
    required: true,
  },
});

const modelValue = defineModel({
  type: Object as PropType<Wahltag>,
  required: false,
});
</script>
