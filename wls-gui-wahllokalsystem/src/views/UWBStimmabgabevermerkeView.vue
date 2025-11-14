<template>
  <v-card>
    <v-form v-model="stimmabgabevermerkeModel">
      <the-u-w-b-stimmabgabevermerke-erfassen-card />
      <the-u-w-b-stimmabgabevermerke-eingenommene-wahlscheine-card />
      <the-u-w-b-stimmabgabevermerke-darstellung-summe-card />
    </v-form>
    <v-card-actions>
      <base-button-save
        :disabled="!stimmabgabevermerkeModel"
        :loading="isStimmabgabevermerkeSaving"
        @click="saveStimmabgabevermerke"
      /> </v-card-actions
  ></v-card>
</template>
<script setup lang="ts">
import { storeToRefs } from "pinia";
import { ref } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import TheUWBStimmabgabevermerkeDarstellungSummeCard from "@/components/stimmabgabevermerke/TheUWBStimmabgabevermerkeDarstellungSummeCard.vue";
import TheUWBStimmabgabevermerkeEingenommeneWahlscheineCard from "@/components/stimmabgabevermerke/TheUWBStimmabgabevermerkeEingenommeneWahlscheineCard.vue";
import TheUWBStimmabgabevermerkeErfassenCard from "@/components/stimmabgabevermerke/TheUWBStimmabgabevermerkeErfassenCard.vue";
import { useStimmabgabevermerkeStore } from "@/stores/stimmabgabevermerkeStore.ts";

const { saveStimmabgabevermerke } = useStimmabgabevermerkeStore();
const { isStimmabgabevermerkeSaving } = storeToRefs(
  useStimmabgabevermerkeStore()
);
const stimmabgabevermerkeModel = ref(false);
</script>
