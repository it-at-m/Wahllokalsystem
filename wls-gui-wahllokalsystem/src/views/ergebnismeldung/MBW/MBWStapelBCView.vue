<template>
  <the-wahlvorschlaege-kandidaten-stimmen-card
    :wahl-i-d="wahlID"
    :wahlbezirk-i-d="wahlbezirkID"
  />
</template>

<script setup lang="ts">
import { useRoute } from "vue-router";

import TheWahlvorschlaegeKandidatenStimmenCard from "@/components/ergebnismeldung/MBW/stapelBC/TheWahlvorschlaegeKandidatenStimmenCard.vue";

const route = useRoute();

const wahlID = route.params.wahlId as string;
const wahlbezirkID = route.params.wahlbezirkId as string;
</script>
