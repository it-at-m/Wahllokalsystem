<template>
  <v-data-table
    class="mt-3"
    :headers="headers"
    :items="stimmzettelListe"
    :items-per-page="itemsPerPage"
    v-model:page="page"
    v-model:sort-by="sortBy"
    :loading="stimmzettelLoading"
  >
    <template #item.stimmzettelkennung="{ item }">
      {{ teamId }}-{{ item.stimmzettelkennung }}
    </template>

    <template #item.gueltigkeit="{ item }">
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
        :icon="mdiCircleOutline"
        color="error"
      />
    </template>

    <template #item.vormerkungsGrund="{ item }">
      <span v-if="isVorgemerktFuerBeschluss(item)">
        {{ getVormerkungsGrund(item) }}
      </span>
    </template>

    <template #item.actions="{ item }">
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
import type { Beschlussgrund } from "@/types/dse/Beschlussgrund.ts";
import type { Stimmzettel } from "@/types/dse/Stimmzettel.ts";

import {
  mdiCheckboxMarkedCircleOutline,
  mdiCircleOutline,
  mdiListStatus,
} from "@mdi/js";
import { onActivated, ref } from "vue";

import { StimmzettelGueltigkeitEnum } from "@/types/dse/StimmzettelGueltigkeitEnum.ts";

const props = defineProps<{
  teamId: string;
}>();

const headers = [
  { title: "#", key: "stimmzettelkennung", sortable: true },
  { title: "Gültigkeit", key: "gueltigkeit", sortable: true },
  { title: "Beschlussbegründung", key: "vormerkungsGrund", sortable: false },
  { title: "", key: "actions", sortable: false },
];

const stimmzettelListe = ref<Stimmzettel[]>([]);
const stimmzettelLoading = ref(false);

const itemsPerPage = ref(10);
const page = ref(1);
const sortBy = ref([{ key: "stimmzettelkennung", order: "asc" }] as const);

onActivated(() => {
  //TODO: stimmzettelService.getStimmzettel() mit pagination anbinden.
  stimmzettelLoading.value = true;
  stimmzettelListe.value = createDummyStimmzettelListe();
  stimmzettelLoading.value = false;
});

function createDummyStimmzettelListe(): Stimmzettel[] {
  return [
    {
      stimmzettelkennung: 1,
      wahlvorschlaege: [],
      invalideVotes: 0,
      gueltigkeit: StimmzettelGueltigkeitEnum.Valid,
      beschlussvorschlag: [],
      beschlussfassung: null,
    },
    {
      stimmzettelkennung: 2,
      wahlvorschlaege: [],
      invalideVotes: 1,
      gueltigkeit: StimmzettelGueltigkeitEnum.Invalid,
      beschlussvorschlag: [],
      beschlussfassung: null,
    },
    {
      stimmzettelkennung: 3,
      wahlvorschlaege: [],
      invalideVotes: 0,
      gueltigkeit: StimmzettelGueltigkeitEnum.Invalid,
      beschlussvorschlag: [
        {
          text: "Stimmzettel zur Beschlussfassung vorgemerkt (Dummy)",
        } as Beschlussgrund,
      ],
      beschlussfassung: null,
    },
    {
      stimmzettelkennung: 4,
      wahlvorschlaege: [],
      invalideVotes: 0,
      gueltigkeit: StimmzettelGueltigkeitEnum.Invalid,
      beschlussvorschlag: [
        {
          text: "Wählerwille nicht zweifelfrei erkennbar. Kennzeichnung nicht eindeutig zuzuordnen.",
        } as Beschlussgrund,
      ],
      beschlussfassung: null,
    },
  ];
}

function isVorgemerktFuerBeschluss(stimmzettel: Stimmzettel): boolean {
  return stimmzettel.beschlussvorschlag.length > 0;
}

function getVormerkungsGrund(stimmzettel: Stimmzettel): string {
  if (!isVorgemerktFuerBeschluss(stimmzettel)) {
    return "";
  }
  const ersterGrund = stimmzettel.beschlussvorschlag[0];
  return ersterGrund.text;
}

function onStimmzettelBearbeitenClicked(stimmzettel: Stimmzettel) {
  // Bearbeiten-Funktionalität Platzhalter.
}
</script>
