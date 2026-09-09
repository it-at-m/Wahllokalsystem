<template>
  <v-card>
    <base-stimmzettelerfassung-tabs
      :team-id="teamID"
      :stimmzettel-liste="savedStimmzettel"
      :stimmzettel-loading="isStimmzettelLoading"
    />
  </v-card>
</template>
<script setup lang="ts">
import { useRoute } from "vue-router";

import BaseStimmzettelerfassungTabs from "@/components/dse/stimmzettelerfassung/baseComponents/BaseStimmzettelerfassungTabs.vue";
import { useStimmzettelErfassungViewUtils } from "@/composables/dse/stimmzettelerfassung/stimmzettelErfassungViewUtils.ts";
import { useUserStore } from "@/stores/userStore.ts";

const route = useRoute();
const userStore = useUserStore();

const teamID = userStore.currentUserTeamName || "";
const wahlID = (route.params.wahlId as string) || "";
const wahlbezirkID = (route.params.wahlbezirkId as string) || "";

const { isStimmzettelLoading, savedStimmzettel } =
  useStimmzettelErfassungViewUtils(wahlID, wahlbezirkID, teamID);
</script>
