<template>
  <div>
    <v-data-table
      :items="items"
      :headers="headers"
      fixed-header
      :sort-by="sortBy"
    >
      <template #item.index="{ item }"> B {{ item.index }} </template>
      <template #item.actions>
        <div class="d-flex justify-end">
          <v-btn
            icon="$edit"
            size="small"
          />
        </div>
      </template>
      <template #item.isValid="{ item }">
        <v-icon
          :icon="valideStateToIcon(item.isValid)"
          :color="valideStateToColor(item.isValid)"
        />
      </template>
    </v-data-table>
  </div>
</template>

<script setup lang="ts">
import type { Ref } from "vue";
import type { SortItem } from "vuetify/lib/components/VDataTable/composables/sort";

import { storeToRefs } from "pinia";
import { ref } from "vue";

import { useExperimentalFeaturesStore } from "@/stores/experimentalFeaturesStore.ts";

const headers = [
  {
    title: "#",
    value: "index",
    sortable: true,
  },
  {
    title: "Gültigkeit",
    value: "isValid",
  },
  {
    title: "Beschlussbegründung",
    value: "beschlussgrund",
  },
  {
    title: "",
    value: "actions",
  },
];

const { stimmzettelSummaryItems } = storeToRefs(useExperimentalFeaturesStore());

const items = stimmzettelSummaryItems;
const sortBy: Ref<SortItem[]> = ref([
  {
    key: "index",
    order: "desc",
  },
]);

function valideStateToIcon(state: number) {
  if (state === 0) {
    return "$stimmzettelValid ";
  } else if (state === 1) {
    return "$stimmzettelPartialValid  ";
  } else if (state === 2) {
    return "$stimmzettelInvalid";
  } else {
    return "$beschluss";
  }
}

function valideStateToColor(state: number) {
  if (state === 0) {
    return "success";
  } else if (state === 1) {
    return "warning";
  } else if (state === 2) {
    return "error";
  } else {
    return "info";
  }
}
</script>
