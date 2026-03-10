<template>
  <base-ergebnismeldung-cards-container
    title="Niederschrift"
    subtitle="Kontrolle, Übermittlung und Druck der Niederschrift"
    :is-sending="isNiederschriftAndStatusSaving"
    :is-korrigieren-active="isKorrigierenValid"
    :is-drucken-active="hasDoneVorkommnisse"
    :is-drucken-loading="isDruckenLoading"
    :is-senden-active="isSendenActive"
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
    <the-vorkommnisse-requirement-card
      :type="
        hasDoneVorkommnisse
          ? InputFeedbackTypeEnum.information
          : InputFeedbackTypeEnum.error
      "
    />
  </base-ergebnismeldung-cards-container>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

import BaseErgebnismeldungCardsContainer from "@/components/ergebnismeldung/common/BaseErgebnismeldungCardsContainer.vue";
import TheVorkommnisseRequirementCard from "@/components/ergebnismeldung/common/TheVorkommnisseRequirementCard.vue";
import TheMBWGueltigeStimmenAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWGueltigeStimmenAnzeigenCard.vue";
import TheMBWWaehlerAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWaehlerAnzeigenCard.vue";
import TheMBWWahlberechtigteAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWahlberechtigteAnzeigenCard.vue";
import TheMBWGueltigeKandidatenstimmenAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelBC/TheMBWGueltigeKandidatenstimmenAnzeigenCard.vue";
import TheMBWUngueltigeStimmenAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelC/TheMBWUngueltigeStimmenAnzeigenCard.vue";
import { ROUTE_NOTFOUND } from "@/constants.ts";
import { useEreignisStore } from "@/stores/ereignisStore.ts";
import { useErgebnismeldungStore } from "@/stores/ergebnismeldungStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { InputFeedbackTypeEnum } from "@/types/common/InputFeedbackTypeEnum.ts";

const route = useRoute();
const router = useRouter();
const { wahlenActions } = useWahlenStore();
const { sendNiederschrift } = useErgebnismeldungStore();
const { isNiederschriftAndStatusSaving } = storeToRefs(
  useErgebnismeldungStore()
);

const { wahlbezirkEreignisse, ereigniseintraegeContainsVorkommnisse } =
  storeToRefs(useEreignisStore());

// button logic to be implemented
const isKorrigierenValid = ref<null | boolean>();
const isDruckenLoading = ref<boolean>(false);
const isSendenActive = ref<boolean>(true);

const currentUserWahlbezirkID = route.params.wahlbezirkId as string;
const wahlID = route.params.wahlId as string;
const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);

if (!wahl) {
  router.push({
    name: ROUTE_NOTFOUND,
  });
}

const hasDoneVorkommnisse = computed(
  () =>
    ereigniseintraegeContainsVorkommnisse.value ||
    wahlbezirkEreignisse.value.keineVorkommnisse
);

function onSendenClicked() {
  if (wahl) {
    sendNiederschrift(wahl);
  }
}
function onKorrigierenClicked() {
  // to be implemented
}
function onDruckenClicked() {
  // to be implemented
}
</script>
