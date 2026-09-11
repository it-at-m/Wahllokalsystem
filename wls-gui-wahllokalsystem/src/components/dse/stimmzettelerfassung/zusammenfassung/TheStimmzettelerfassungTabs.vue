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
        <the-stimmzettel-uebersicht
          :team-id="teamID"
          :stimmzettel-liste="savedStimmzettel"
          :is-stimmzettel-loading="isStimmzettelLoading"
          :wahlvorschlaege="wahlvorschlaege"
          :has-stimmzettel="hasStimmzettel"
          :save-stimmzettel="saveOrUpdateStimmzettel"
          class="mt-3"
        />
      </v-tabs-window-item>
      <v-tabs-window-item
        value="two"
        eager
      >
        <the-stimmzettel-zusammenfassung
          :stimmzettel-liste="savedStimmzettel"
          :wahlvorschlaege="wahlvorschlaege"
        />
      </v-tabs-window-item>
    </v-tabs-window>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRoute } from "vue-router";

import TheStimmzettelUebersicht from "@/components/dse/stimmzettelerfassung/zusammenfassung/TheStimmzettelUebersicht.vue";
import TheStimmzettelZusammenfassung from "@/components/dse/stimmzettelerfassung/zusammenfassung/TheStimmzettelZusammenfassung.vue";
import { useStimmzettelState } from "@/composables/dse/stimmzettelerfassung/stimmzettelState.ts";
import { useWahlvorschlaegeState } from "@/composables/dse/stimmzettelerfassung/wahlvorschlaegeState.ts";
import { useUserStore } from "@/stores/userStore.ts";

const route = useRoute();
const userStore = useUserStore();

const teamID = userStore.currentUserTeamName || "";
const wahlID = (route.params.wahlId as string) || "";
const wahlbezirkID = (route.params.wahlbezirkId as string) || "";

const {
  isStimmzettelLoading,
  savedStimmzettel,
  hasStimmzettel,
  saveOrUpdateStimmzettel,
} = useStimmzettelState(wahlID, wahlbezirkID, teamID);

const { wahlvorschlaege } = useWahlvorschlaegeState(wahlID, wahlbezirkID);

const tab = ref("one");
</script>
