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
import { EXAMPLE_ROUTES_NOTFOUND } from "@/constants.ts";
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
  () => wahlID.value,
  (wahlID) => {
    const wahl = getWahlOrUndefinedById(wahlID);

    if (!wahl) {
      router.push({
        name: EXAMPLE_ROUTES_NOTFOUND,
      });
    }
  }
);
</script>
