<template>
  <base-ergebnismeldung-cards-container
    title="Niederschrift"
    subtitle="Kontrolle, Übermittlung und Druck der Niederschrift"
    :is-sending="isSendingNiederschrift"
    :is-korrigieren-active="isKorrigierenValid"
    :is-drucken-active="isDruckenValid"
    :is-drucken-loading="isDruckenLoading"
    @save="onSendenClicked"
    @edit="onKorrigierenClicked"
    @print="onDruckenClicked"
  >
    <the-m-b-w-wahlberechtigte-anzeigen-card
      :wahlbezirk-id="currentUserWahlbezirkID"
      :wahl-id="wahlID"
    />
    <the-m-b-w-waehler-anzeigen-card
      :wahlbezirk-id="currentUserWahlbezirkID"
      :wahl-id="wahlID"
    />
    <the-m-b-w-ungueltige-stimmen-anzeigen-card
      :wahlbezirk-id="currentUserWahlbezirkID"
      :wahl-id="wahlID"
    />
    <the-m-b-w-gueltige-stimmen-anzeigen-card
      :wahlbezirk-id="currentUserWahlbezirkID"
      :wahl-id="wahlID"
    />
    <the-m-b-w-gueltige-kandidatenstimmen-anzeigen-card
      :wahlbezirk-id="currentUserWahlbezirkID"
      :wahl-id="wahlID"
    />
  </base-ergebnismeldung-cards-container>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRoute, useRouter } from "vue-router";

import BaseErgebnismeldungCardsContainer from "@/components/ergebnismeldung/common/BaseErgebnismeldungCardsContainer.vue";
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
const isSendingNiederschrift = ref<boolean>(false);
const isKorrigierenValid = ref<null | boolean>();
const isDruckenValid = ref<null | boolean>(true);
const isDruckenLoading = ref<boolean>(false);

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
