<template>
  <the-o-w-b-stapel-a-card
    :wahl-i-d="wahlID"
    :wahlbezirk-i-d="getWahlbezirkIdFromWahlMetaDataByWahlId(wahlID) || ''"
  />
</template>

<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";

import TheOWBStapelACard from "@/components/ergebnisermittlung/OBW/stapelA/TheOBWStapelACard.vue";
import { EXAMPLE_ROUTES_NOTFOUND } from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const route = useRoute();
const router = useRouter();
const { wahlenActions } = useWahlenStore();

const { getWahlbezirkIdFromWahlMetaDataByWahlId } = useUserStore();

const wahlID = route.params.wahlId as string;
const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);
if (!wahl) {
  router.push({
    name: EXAMPLE_ROUTES_NOTFOUND,
  });
}
</script>
