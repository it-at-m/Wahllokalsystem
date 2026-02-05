<template>
  <v-card>
    <v-card-title class="font-weight-bold">Niederschrift</v-card-title>
    <v-card-subtitle class="font-weight-bold mb-10"
      >Kontrolle, Übermittlung und Druck der Niederschrift</v-card-subtitle
    >
    <the-m-b-w-wahlberechtigte-anzeigen-card
      class="ma-5"
      :wahlbezirk-id="currentUserWahlbezirkID"
      :wahl-id="wahlID"
    />
    <the-m-b-w-waehler-anzeigen-card
      class="ma-5"
      :wahlbezirk-id="currentUserWahlbezirkID"
      :wahl-id="wahlID"
    />
    <the-m-b-w-ungueltige-stimmen-anzeigen-card
      class="ma-5"
      :wahlbezirk-id="currentUserWahlbezirkID"
      :wahl-id="wahlID"
    />
    <the-m-b-w-gueltige-stimmen-anzeigen-card
      class="ma-5"
      :wahlbezirk-id="currentUserWahlbezirkID"
      :wahl-id="wahlID"
    />
    <the-m-b-w-gueltige-kandidatenstimmen-anzeigen-card
      class="ma-5"
      :wahlbezirk-id="currentUserWahlbezirkID"
      :wahl-id="wahlID"
    />
    <v-card-actions>
      <base-button-save
        save-text="Niederschrift senden"
        prepend-icon="$cloudUpload"
        :disabled="!isSendenValid"
        @click="onSendenClicked"
      />
      <base-button-save
        save-text="Niederschrift korrigieren"
        prepend-icon="$edit"
        :disabled="!isKorrigierenValid"
        @click="onKorrigierenClicked"
      />
      <base-button-save
        save-text="Niederschrift drucken"
        prepend-icon="$printer"
        :disabled="!isDruckenValid"
        @click="onDruckenClicked"
      />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRoute, useRouter } from "vue-router";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import TheMBWGueltigeStimmenAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWGueltigeStimmenAnzeigenCard.vue";
import TheMBWWaehlerAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWaehlerAnzeigenCard.vue";
import TheMBWWahlberechtigteAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWahlberechtigteAnzeigenCard.vue";
import TheMBWGueltigeKandidatenstimmenAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelBC/TheMBWGueltigeKandidatenstimmenAnzeigenCard.vue";
import TheMBWUngueltigeStimmenAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelC/TheMBWUngueltigeStimmenAnzeigenCard.vue";
import { ROUTE_NOTFOUND } from "@/constants.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const route = useRoute();
const router = useRouter();
const { wahlenActions } = useWahlenStore();

// button logic to be implemented
const isSendenValid = ref<null | boolean>();
const isKorrigierenValid = ref<null | boolean>();
const isDruckenValid = ref<null | boolean>(true);

const currentUserWahlbezirkID = route.params.wahlbezirkId as string;
const wahlID = route.params.wahlId as string;
const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);

if (!wahl) {
  router.push({
    name: ROUTE_NOTFOUND,
  });
}

function onSendenClicked() {
  // to be implemented
}
function onKorrigierenClicked() {
  // to be implemented
}
function onDruckenClicked() {
  // to be implemented
}
</script>
