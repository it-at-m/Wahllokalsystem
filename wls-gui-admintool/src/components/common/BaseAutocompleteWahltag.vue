<template>
  <v-autocomplete
    clearable
    label="Wahltag auswählen"
    :items="props.items"
    :item-title="itemTitle"
    v-model="modelValue"
    return-object
    data-test="autocompleteWahltage"
    no-data-text="Keine Wahltage gefunden"
  />
</template>

<script setup lang="ts">
import type { Wahltag } from "@/types/wahltag/Wahltag.ts";
import type { PropType } from "vue";

import { useDate } from "vuetify";
import { VAutocomplete } from "vuetify/components";

const date = useDate();

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

function itemTitle(wahltag: Wahltag): string {
  return date.format(wahltag.wahltag, "fullDate");
}
</script>
