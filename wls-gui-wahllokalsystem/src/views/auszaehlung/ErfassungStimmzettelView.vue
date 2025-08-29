<template>
  <the-ergebnisermittlung-stimmzettelumschlaege-card
    :title="title"
    :wahl-id="wahlID"
    :use-time="!isUWB"
  />
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useRoute } from "vue-router";

import TheErgebnisermittlungStimmzettelumschlaegeCard from "@/components/ergebnisermittlung/TheErgebnisermittlungStimmzettelumschlaegeCard.vue";
import { useUserStore } from "@/stores/userStore.ts";

const route = useRoute();
const { isUWB } = useUserStore();

const wahlID = computed(() => route.params.wahlId as string);

const title = computed(() => {
  return isUWB
    ? "Wahlurne öffnen und Stimmzettel zählen"
    : "Wahlurne öffnen und Stimmzettelumschläge zählen";
});
</script>
