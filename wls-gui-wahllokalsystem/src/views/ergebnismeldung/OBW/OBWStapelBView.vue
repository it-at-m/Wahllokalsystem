<template>
  <the-o-b-w-stapel-b-card :wahl-id="wahlID" />
</template>

<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";

import TheOBWStapelBCard from "@/components/ergebnismeldung/OBW/stapelB/TheOBWStapelBCard.vue";
import { EXAMPLE_ROUTES_NOTFOUND } from "@/constants.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const route = useRoute();
const router = useRouter();
const { wahlenActions } = useWahlenStore();

const wahlID = route.params.wahlId as string;
const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);
if (!wahl) {
  router.push({
    name: EXAMPLE_ROUTES_NOTFOUND,
  });
}
</script>
