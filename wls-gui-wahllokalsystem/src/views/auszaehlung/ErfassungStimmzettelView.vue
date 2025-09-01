<template>
  <the-ergebnisermittlung-stimmzettelumschlaege-card
    :title="title"
    :wahl-id="wahlID"
    :use-time="!isUWB"
  />
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed, watch } from "vue";
import { useRoute, useRouter } from "vue-router";

import TheErgebnisermittlungStimmzettelumschlaegeCard from "@/components/ergebnisermittlung/TheErgebnisermittlungStimmzettelumschlaegeCard.vue";
import { ROUTE_AUSZAEHLUNG_STIMMZETTEL } from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const route = useRoute();
const router = useRouter();
const { isUWB } = storeToRefs(useUserStore());
const { getWahlOrUndefinedById } = useWahlenStore();

const wahlID = computed(() => route.params.wahlId as string);

const title = computed(() => {
  return isUWB.value
    ? "Wahlurne öffnen und Stimmzettel zählen"
    : "Wahlurne öffnen und Stimmzettelumschläge zählen";
});

watch(
  () => route.params.wahlId,
  (newId) => {
    const wahl = getWahlOrUndefinedById(newId as string);

    if (wahl) {
      router.push({
        name: ROUTE_AUSZAEHLUNG_STIMMZETTEL,
        params: { wahlId: String(wahl.wahlID) },
      });
    }
  }
);
</script>
