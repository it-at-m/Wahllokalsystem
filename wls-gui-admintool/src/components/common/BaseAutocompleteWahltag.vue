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
  >
    <template #item="{ props: itemProps, item }">
      <v-list-item v-bind="itemProps">
        <template
          v-if="item.raw.active"
          #append
        >
          <base-chip-active>aktiv</base-chip-active>
        </template>
      </v-list-item>
    </template>
    <template #selection="{ item }">
      <span>{{ item.title }}</span>
      <base-chip-active
        v-if="item.raw.active"
        class="ml-2"
      >
        aktiv
      </base-chip-active>
    </template>
  </v-autocomplete>
</template>

<script setup lang="ts">
import type { Wahltag } from "@/types/wahltag/Wahltag.ts";
import type { PropType } from "vue";

import { useDate } from "vuetify";
import { VAutocomplete, VListItem } from "vuetify/components";

import BaseChipActive from "@/components/wahltag/BaseChipActive.vue";

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
