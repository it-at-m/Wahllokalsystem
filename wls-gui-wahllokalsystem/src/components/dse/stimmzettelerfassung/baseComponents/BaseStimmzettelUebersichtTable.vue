<template>
  <v-data-table
    v-model:sort-by="sortBy"
    :headers="headers"
    :items="props.stimmzettelListe"
    :items-per-page="itemsPerPage"
    :loading="props.stimmzettelLoading"
    :loading-text="TABLE_LOADING_DATA_STIMMZETTEL"
    :items-per-page-text="ITEMS_PER_PAGE_TITLE"
    :no-data-text="TABLE_NO_DATA_TEXT_STIMMZETTEL"
    multi-sort
  >
    <template #[`item.stimmzettelkennung`]="{ item }">
      {{ teamId }} {{ item.stimmzettelkennung }}
    </template>

    <template #[`item.gueltigkeit`]="{ item }">
      <base-stimmzettel-gueltigkeit-icon :value="item.gueltigkeit" />
    </template>

    <template #[`item.vormerkungsgrund`]="{ item }">
      <span v-if="isVorgemerktFuerBeschluss(item)">
        {{ getVormerkungsgrund(item) }}
      </span>
    </template>

    <template #[`item.actions`]="{ item }">
      <v-btn
        aria-label="Stimmzettel bearbeiten"
        icon="$edit"
        size="small"
        variant="text"
        @click="onStimmzettelBearbeitenClicked(item)"
      />
    </template>
  </v-data-table>
</template>

<script setup lang="ts">
import type { Stimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";

import { ref } from "vue";

import BaseStimmzettelGueltigkeitIcon from "@/components/dse/BaseStimmzettelGueltigkeitIcon.vue";
import { useStimmzettelUtils } from "@/composables/dse/stimmzettelerfassung/stimmzettelUtils.ts";
import {
  ITEMS_PER_PAGE_TITLE,
  TABLE_LOADING_DATA_STIMMZETTEL,
  TABLE_NO_DATA_TEXT_STIMMZETTEL,
} from "@/constants.ts";

const props = defineProps<{
  teamId: string;
  stimmzettelListe: Stimmzettel[];
  stimmzettelLoading: boolean;
}>();
const stimmzettelkennungKey = "stimmzettelkennung";

const headers = [
  { title: "#", key: stimmzettelkennungKey, sortable: true },
  { title: "Gültigkeit", key: "gueltigkeit", sortable: true },
  { title: "Beschlussbegründung", key: "vormerkungsgrund", sortable: false },
  { title: "", key: "actions", sortable: false },
];

const itemsPerPage = ref(10);
const sortBy = ref([{ key: stimmzettelkennungKey, order: "desc" }] as const);

const { isVorgemerktFuerBeschluss, getVormerkungsgrund } =
  useStimmzettelUtils();

function onStimmzettelBearbeitenClicked(stimmzettel: Stimmzettel) {
  // TODO Bearbeiten-Funktionalität Platzhalter. #3384
  console.debug(JSON.stringify(stimmzettel));
}
</script>
