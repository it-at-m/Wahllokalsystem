<template>
  <v-data-table
    v-model:sort-by="sortBy"
    :headers="headers"
    :items="props.stimmzettelListe"
    :items-per-page="itemsPerPage"
    :loading="props.stimmzettelLoading"
    :loading-text="TABLE_LOADING_DATA_STIMMZETTEL"
    :items-per-page-text="ITEMS_PER_PAGE_TITLE"
    :no-data-text="TABLE_NO_DATA_TEXT_BESCHLUSSFASSUNG"
    :multi-sort="true"
    sort-asc-icon="$asc"
    sort-desc-icon="$desc"
    sticky
  >
    <template #[`item.beschlussgrund`]="{ item }">
      {{ getVormerkungsOrEntscheidungsgrundBasedOnBeschlussfassung(item) }}
    </template>

    <template #[`item.beschlussfassung`]="{ value }">
      <v-icon
        :icon="value ? '$success' : ''"
        :color="value ? 'success' : ''"
      />
    </template>

    <template #[`item.gueltigkeit`]="{ value }">
      <base-stimmzettel-gueltigkeit-icon
        :value="value"
        hide-beschluss-icon
      />
      {{ value == "VALID" ? "gültig" : value == "INVALID" ? "ungültig" : "" }}
    </template>

    <template #[`item.actions`]="{ item }">
      <div class="d-flex ga-2">
        <v-btn
          aria-label="Beschluss bearbeiten"
          icon="$edit"
          size="x-small"
          variant="elevated"
          :color="item.beschlussfassung == null ? 'primary' : ''"
          @click="onBeschlussBearbeitenClicked(item)"
        />
      </div>
    </template>
  </v-data-table>
</template>

<script setup lang="ts">
import type { Stimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";

import { ref } from "vue";

import BaseStimmzettelGueltigkeitIcon from "@/components/dse/BaseStimmzettelGueltigkeitIcon.vue";
import {
  ITEMS_PER_PAGE_TITLE,
  TABLE_LOADING_DATA_STIMMZETTEL,
  TABLE_NO_DATA_TEXT_BESCHLUSSFASSUNG,
} from "@/constants.ts";

const props = defineProps<{
  stimmzettelListe: Stimmzettel[];
  stimmzettelLoading: boolean;
}>();

const headers = [
  { title: "Team", key: "teamID" },
  { title: "Kennung", key: "stimmzettelkennung" },
  { title: "Beschlussgrund", key: "beschlussgrund", sortable: false },
  { title: "Beschluss gefasst", key: "beschlussfassung" },
  { title: "Beschlussergebnis", key: "gueltigkeit", sortable: false },
  { title: "", key: "actions", sortable: false },
];

const itemsPerPage = ref(10);
const sortBy = ref([
  { key: "teamID", order: "asc" },
  { key: "beschlussfassung", order: "asc" },
  { key: "stimmzettelkennung", order: "asc" },
] as const);

function getVormerkungsOrEntscheidungsgrundBasedOnBeschlussfassung(
  stimmzettel: Stimmzettel
) {
  // todo: map enum values to strings
  if (stimmzettel.beschlussfassung) {
    return stimmzettel.beschlussfassung.text;
  } else {
    return stimmzettel.beschlussvorschlag
      .map((beschlussgrund) => beschlussgrund.text)
      .join(", ");
  }
}

const emit = defineEmits<{
  editBeschlussStimmzettel: [stimmzettel: Stimmzettel];
}>();

function onBeschlussBearbeitenClicked(stimmzettel: Stimmzettel) {
  emit("editBeschlussStimmzettel", stimmzettel);
}
</script>
