<template>
  <the-ergebnisermittlung-stimmzettelumschlaege-card
    v-if="wahl"
    :title="`Wahlurne öffnen und ${getStimmzettelTermForWahl(wahl)} zählen`"
    :wahl-id="wahlID"
    :wahlbezirk-id="wahlbezirkId"
  />
</template>

<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";

import TheErgebnisermittlungStimmzettelumschlaegeCard from "@/components/ergebnismeldung/common/TheErgebnisermittlungStimmzettelumschlaegeCard.vue";
import { useTextFormatter } from "@/composables/common/textFormatter.ts";
import { ROUTE_NOTFOUND } from "@/constants.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const route = useRoute();
const router = useRouter();
const { wahlenActions } = useWahlenStore();
const { getStimmzettelTermForWahl } = useTextFormatter();

const wahlID = route.params.wahlId as string;
const wahlbezirkId = route.params.wahlbezirkId as string;
const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);

if (!wahl) {
  router.push({
    name: ROUTE_NOTFOUND,
  });
}
</script>
