<template>
  <the-m-b-w-stapel-d-card :wahl-id="wahlID" />
</template>
<script setup lang="ts">
import { computed, watch } from "vue";
import { useRoute, useRouter } from "vue-router";

import TheMBWStapelDCard from "@/components/ergebnisermittlung/MBW/stapelD/TheMBWStapelDCard.vue";
import { EXAMPLE_ROUTES_NOTFOUND } from "@/constants.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const route = useRoute();
const router = useRouter();
const { wahlenActions } = useWahlenStore();

const wahlID = computed(() => route.params.wahlId as string);

watch(
  () => wahlID.value,
  (wahlID) => {
    const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);

    if (!wahl) {
      router.push({
        name: EXAMPLE_ROUTES_NOTFOUND,
      });
    }
  }
);
</script>
