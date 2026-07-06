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
    <template #selection="{ item }">
      <span>{{ itemTitle(item.raw) }}</span>
      <v-chip
        v-if="item.raw.isActive"
        class="ml-2"
        color="primary"
        data-test="activeWahltag"
        size="small"
        variant="tonal"
      >
        Aktiv
      </v-chip>
    </template>
    <template #item="{ props: itemProps, item }">
      <v-list-item
        v-bind="itemProps"
        :title="itemTitle(item.raw)"
      >
        <template #append>
          <v-chip
            v-if="item.raw.isActive"
            color="primary"
            data-test="activeWahltag"
            size="small"
            variant="tonal"
          >
            Aktiv
          </v-chip>
        </template>
      </v-list-item>
    </template>
  </v-autocomplete>
</template>

<script setup lang="ts">
import type { Wahltag } from "@/types/wahltag/Wahltag.ts";
import type { PropType } from "vue";

import { useDate } from "vuetify";
import { VAutocomplete, VChip, VListItem } from "vuetify/components";

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
