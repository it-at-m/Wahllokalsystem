<template>
  <v-container>
    <the-u-w-b-stimmabgabevermerke-erfassen-card />
    <the-u-w-b-stimmabgabevermerke-eingenommene-wahlscheine-card />
    <the-u-w-b-stimmabgabevermerke-darstellung-summe-card
  /></v-container>
</template>
<script setup lang="ts">
import { storeToRefs } from "pinia";
import { onMounted } from "vue";

import TheUWBStimmabgabevermerkeDarstellungSummeCard from "@/components/stimmabgabevermerke/TheUWBStimmabgabevermerkeDarstellungSummeCard.vue";
import TheUWBStimmabgabevermerkeEingenommeneWahlscheineCard from "@/components/stimmabgabevermerke/TheUWBStimmabgabevermerkeEingenommeneWahlscheineCard.vue";
import TheUWBStimmabgabevermerkeErfassenCard from "@/components/stimmabgabevermerke/TheUWBStimmabgabevermerkeErfassenCard.vue";
import { useStimmabgabevermerkeStore } from "@/stores/stimmabgabevermerkeStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const { loadStimmabgabevermerke } = useStimmabgabevermerkeStore();
const { currentUserWahlMetadata } = storeToRefs(useUserStore());
const { getWaehlerverzeichnisNummerOrUndefinedById } = useWahlenStore();

onMounted(() => {
  currentUserWahlMetadata.value.forEach((metadata) => {
    const waehlerverzeichnisNummer = getWaehlerverzeichnisNummerOrUndefinedById(
      metadata.wahlID
    );
    if (waehlerverzeichnisNummer) {
      loadStimmabgabevermerke(metadata.wahlbezirkID, waehlerverzeichnisNummer);
    }
  });
});
</script>
