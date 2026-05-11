<template>
  <v-card class="ma-1">
    <v-card-title>Übersicht erfasster Stimmzettel</v-card-title>
    <v-card-text>
      <v-data-table
        :items="items"
        :headers="headers"
        fixed-header
        :sort-by="sortBy"
      >
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
    </v-card-text>
    <v-card-actions>
      <base-text-button active>Erfassung fortsetzen</base-text-button>
      <base-wls-button-save
        :active="false"
        :save-text="'Weiter zur Schnellmeldung'"
      />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import type { Ref } from "vue";
import type { SortItem } from "vuetify/lib/components/VDataTable/composables/sort";

import { ref } from "vue";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";

const headers = [
  {
    title: "#",
    value: "index",
    sortable: true,
  },
  {
    title: "Stimmen",
    value: "countUserVotes",
    sortable: true,
  },
  {
    title: "ungültige Stimmen",
    value: "countUserVotesInvalid",
    sortable: true,
  },
  {
    title: "Streichungen",
    value: "countUserDiscards",
    sortable: true,
  },
  {
    title: "Listenkreuz",
    value: "countListenkreuze",
    sortable: true,
    groupable: true,
  },
  {
    title: "Gültigkeit",
    value: "isValid",
  },
  {
    title: "",
    value: "actions",
  },
];

const defaultItems = createItems();
const items = defaultItems;
const sortBy: Ref<SortItem[]> = ref([
  {
    key: "index",
    order: "desc",
  },
]);

function createItems(count = 20) {
  const result = [];
  for (let i = 1; i <= count; i++) {
    result.push({
      index: i,
      countUserVotes: Math.floor(Math.random() * 80),
      countUserVotesInvalid: Math.floor(Math.random() * 10),
      countListenkreuze: Math.floor(Math.random() * 2),
      countUserDiscards: Math.floor(Math.random() * 4),
      isValid: Math.floor(Math.random() * 3),
    });
  }
  return result;
}

function valideStateToIcon(state: number) {
  if (state === 0) {
    return "$stimmzettelValid ";
  } else if (state === 1) {
    return "$stimmzettelPartialValid  ";
  } else {
    return "$stimmzettelInvalid";
  }
}

function valideStateToColor(state: number) {
  if (state === 0) {
    return "success";
  } else if (state === 1) {
    return "warning";
  } else {
    return "error";
  }
}
</script>
