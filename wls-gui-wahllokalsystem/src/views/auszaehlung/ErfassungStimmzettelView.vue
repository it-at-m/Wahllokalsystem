<template>
  <the-ergebnisermittlung-stimmzettelumschlaege-card
    v-if="wahl"
    :title="`Wahlurne öffnen und ${getStimmzettelTermForWahl(wahl)} zählen`"
    :wahl-id="wahlID"
    :use-time="!isUWB"
  />
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { useRoute, useRouter } from "vue-router";

import TheErgebnisermittlungStimmzettelumschlaegeCard from "@/components/ergebnisermittlung/TheErgebnisermittlungStimmzettelumschlaegeCard.vue";
import { useTextFormatter } from "@/composables/common/textFormatter.ts";
import { EXAMPLE_ROUTES_NOTFOUND } from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const route = useRoute();
const router = useRouter();
const { isUWB } = storeToRefs(useUserStore());
const { wahlenActions } = useWahlenStore();
const { getStimmzettelTermForWahl } = useTextFormatter();

const wahlID = route.params.wahlId as string;
const wahl = wahlID ? wahlenActions.getWahlOrUndefinedById(wahlID) : undefined;

if (!wahl) {
  router.push({
    name: EXAMPLE_ROUTES_NOTFOUND,
  });
}
</script>
