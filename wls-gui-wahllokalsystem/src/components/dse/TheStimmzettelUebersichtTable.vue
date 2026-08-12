<template>
  <v-data-table
    v-model:page="page"
    v-model:sort-by="sortBy"
    class="mt-3"
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
      <v-icon
        v-if="isVorgemerktFuerBeschluss(item)"
        :icon="mdiListStatus"
        color="info"
      />
      <v-icon
        v-else-if="item.gueltigkeit === StimmzettelGueltigkeitEnum.Valid"
        :icon="mdiCheckboxMarkedCircleOutline"
        color="success"
      />
      <v-icon
        v-else
        :icon="mdiMinusCircleOutline"
        color="error"
      />
    </template>

    <template #[`item.vormerkungsGrund`]="{ item }">
      <span v-if="isVorgemerktFuerBeschluss(item)">
        {{ getVormerkungsGrund(item) }}
      </span>
    </template>

    <template #[`item.actions`]="{ item }">
      <v-btn
        icon="$edit"
        size="small"
        variant="text"
        @click="onStimmzettelBearbeitenClicked(item)"
      />
    </template>
  </v-data-table>
</template>

<script setup lang="ts">
import type { Stimmzettel } from "@/types/dse/Stimmzettel.ts";

import {
  mdiCheckboxMarkedCircleOutline,
  mdiListStatus,
  mdiMinusCircleOutline,
} from "@mdi/js";
import { ref } from "vue";

import {
  ITEMS_PER_PAGE_TITLE,
  TABLE_LOADING_DATA_STIMMZETTEL,
  TABLE_NO_DATA_TEXT_STIMMZETTEL,
} from "@/constants.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/StimmzettelGueltigkeitEnum.ts";

const props = defineProps<{
  teamId: string;
  stimmzettelListe: Stimmzettel[];
  stimmzettelLoading: boolean;
}>();

const headers = [
  { title: "#", key: "stimmzettelkennung", sortable: true },
  { title: "Gültigkeit", key: "gueltigkeit", sortable: true },
  { title: "Beschlussbegründung", key: "vormerkungsGrund", sortable: false },
  { title: "", key: "actions", sortable: false },
];

const itemsPerPage = ref(10);
const page = ref(1);
const sortBy = ref([{ key: "stimmzettelkennung", order: "desc" }] as const);

function isVorgemerktFuerBeschluss(stimmzettel: Stimmzettel): boolean {
  return stimmzettel.beschlussvorschlag.length > 0;
}

function getVormerkungsGrund(stimmzettel: Stimmzettel): string {
  if (!isVorgemerktFuerBeschluss(stimmzettel)) {
    return "";
  }
  return stimmzettel.beschlussvorschlag.map((grund) => grund.text).join(", ");
}

function onStimmzettelBearbeitenClicked(stimmzettel: Stimmzettel) {
  // Bearbeiten-Funktionalität Platzhalter.
  console.debug(JSON.stringify(stimmzettel));
}
</script>
