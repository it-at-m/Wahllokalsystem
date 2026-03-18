<template>
  <base-ergebnismeldung-cards-container
    title="Niederschrift"
    subtitle="Kontrolle, Übermittlung und Druck der Niederschrift"
    :is-sending="isNiederschriftAndStatusSaving"
    :is-korrigieren-active="isKorrigierenValid"
    :is-drucken-active="hasDoneVorkommnisse(ereignisse)"
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
        hasDoneVorkommnisse(ereignisse)
          ? InputFeedbackTypeEnum.information
          : InputFeedbackTypeEnum.error
      "
    />
  </base-ergebnismeldung-cards-container>
</template>

<script setup lang="ts">
import type { WahlbezirkEreignisse } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse.ts";

import { storeToRefs } from "pinia";
import { onActivated, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

import BaseErgebnismeldungCardsContainer from "@/components/ergebnismeldung/common/BaseErgebnismeldungCardsContainer.vue";
import TheVorkommnisseRequirementCard from "@/components/ergebnismeldung/common/TheVorkommnisseRequirementCard.vue";
import TheMBWGueltigeStimmenAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWGueltigeStimmenAnzeigenCard.vue";
import TheMBWWaehlerAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWaehlerAnzeigenCard.vue";
import TheMBWWahlberechtigteAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWahlberechtigteAnzeigenCard.vue";
import TheMBWGueltigeKandidatenstimmenAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelBC/TheMBWGueltigeKandidatenstimmenAnzeigenCard.vue";
import TheMBWUngueltigeStimmenAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelC/TheMBWUngueltigeStimmenAnzeigenCard.vue";
import { useEreignisService } from "@/composables/vorfaelleundvorkommnisse/ereignisService.ts";
import { useEreignisUtils } from "@/composables/vorfaelleundvorkommnisse/ereignisUtils.ts";
import { ROUTE_NOTFOUND } from "@/constants.ts";
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
const { hasDoneVorkommnisse } = useEreignisUtils();

const { getEreignisse } = useEreignisService();

// button logic to be implemented
const isKorrigierenValid = ref<null | boolean>();
const isDruckenLoading = ref<boolean>(false);
const isSendenActive = ref<boolean>(true);

const currentUserWahlbezirkID = route.params.wahlbezirkId as string;
const wahlID = route.params.wahlId as string;
const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);
const ereignisse = ref<WahlbezirkEreignisse | null>(null);

if (!wahl) {
  router.push({
    name: ROUTE_NOTFOUND,
  });
}

onActivated(async () => {
  ereignisse.value = await getEreignisse(currentUserWahlbezirkID);
});

function onSendenClicked() {
  if (wahl) {
    sendNiederschrift(wahl);
  }
}
function onKorrigierenClicked() {
  // to be implemented
}
function onDruckenClicked() {
  isDruckenLoading.value = true;
  const printWindow = window.open(
    "",
    "",
    "left=0,top=0,width=800,height=900,toolbar=0,scrollbars=0,status=0"
  );

  if (printWindow) {
    printWindow.document.body.innerHTML = "<div>test</div>";
    printWindow.print();
    printWindow.close();
  }
  isDruckenLoading.value = false;
}
</script>
