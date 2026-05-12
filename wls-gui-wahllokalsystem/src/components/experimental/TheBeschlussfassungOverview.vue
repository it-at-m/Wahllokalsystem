<template>
  <v-card>
    <v-card-title>Beschlüsse fassen</v-card-title>
    <v-card-text>
      <v-tabs v-model="tabBeschlussfassung">
        <v-tab value="overview">Übersicht</v-tab>
        <v-tab value="details">Details</v-tab>
      </v-tabs>
      <v-tabs-window v-model="tabBeschlussfassung">
        <v-tabs-window-item value="overview">
          <v-container>
            <v-row>
              <v-col cols="3">Beschlüsse notwendig</v-col>
              <v-col cols="1">15</v-col>
            </v-row>
            <v-row>
              <v-col cols="3">Beschlüsse gefasst</v-col>
              <v-col cols="1">2</v-col>
            </v-row>
          </v-container>
        </v-tabs-window-item>
        <v-tabs-window-item value="details">
          <v-container>
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
              <template #item.isBeschlussGefasst="{ item }">
                <v-icon
                  :icon="getIcon(item.isBeschlussGefasst)"
                  :color="getColor(item.isBeschlussGefasst)"
                />
              </template>
            </v-data-table>
          </v-container>
        </v-tabs-window-item>
      </v-tabs-window>
    </v-card-text>
    <v-card-actions>
      <base-text-button :active="true"
        >Beschlussfassung fortsetzen</base-text-button
      >
      <base-wls-button-save
        :active="false"
        save-text="Weiter zur Niederschrift"
      />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import type { Ref } from "vue";
import type { SortItem } from "vuetify/lib/components/VDataTable/composables/sort";

import { storeToRefs } from "pinia";
import { ref } from "vue";

import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";
import { useExperimentalFeaturesStore } from "@/stores/experimentalFeaturesStore.ts";

type tabsBeschlussfassung = "overview" | "details";
const tabBeschlussfassung: Ref<tabsBeschlussfassung> = ref("overview");

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
    title: "Beschluss gefasst",
    value: "isBeschlussGefasst",
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

function getIcon(value: boolean) {
  return value ? "$valid" : "$edit";
}

function getColor(value: boolean) {
  return value ? "success" : "error";
}
</script>
