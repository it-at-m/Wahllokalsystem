<template>
  <div>
    <v-tabs
      v-model="tab"
      bg-color="grey-lighten-3"
      slider-color="primary"
      color="primary"
      class="rounded-t border-b"
    >
      <v-tab value="one"> Stimmzettelerfassung Übersicht </v-tab>
      <v-tab value="two"> Zusammenfassung </v-tab>
    </v-tabs>
    <v-tabs-window v-model="tab">
      <v-tabs-window-item value="one">
        <base-stimmzettel-uebersicht
          :team-id="teamId"
          :stimmzettel-liste="stimmzettelListe"
          :stimmzettel-loading="stimmzettelLoading"
          class="mt-3"
        />
      </v-tabs-window-item>
      <v-tabs-window-item value="two">
        <base-stimmzettel-zusammenfassung
          :stimmzettel-liste="stimmzettelListe"
        />
      </v-tabs-window-item>
    </v-tabs-window>
  </div>
</template>

<script setup lang="ts">
import type { Stimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";

import { ref } from "vue";

import BaseStimmzettelUebersicht from "@/components/dse/stimmzettelerfassung/baseComponents/BaseStimmzettelUebersicht.vue";
import BaseStimmzettelZusammenfassung from "@/components/dse/stimmzettelerfassung/baseComponents/BaseStimmzettelZusammenfassung.vue";

const tab = ref("one");

defineProps<{
  teamId: string;
  stimmzettelListe: Stimmzettel[];
  stimmzettelLoading: boolean;
}>();
</script>
