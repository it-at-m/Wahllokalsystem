<template>
  <base-ergebnismeldung-cards-container
    title="Niederschrift"
    subtitle="Kontrolle, Übermittlung und Druck der Niederschrift"
    :is-sending="isSendingNiederschrift"
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
      :is-schnellmeldung="false"
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

import { computed, onActivated, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

import BaseErgebnismeldungCardsContainer from "@/components/ergebnismeldung/common/BaseErgebnismeldungCardsContainer.vue";
import TheVorkommnisseRequirementCard from "@/components/ergebnismeldung/common/TheVorkommnisseRequirementCard.vue";
import TheMBWGueltigeStimmenAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWGueltigeStimmenAnzeigenCard.vue";
import TheMBWWaehlerAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWaehlerAnzeigenCard.vue";
import TheMBWWahlberechtigteAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWahlberechtigteAnzeigenCard.vue";
import TheMBWGueltigeKandidatenstimmenAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelBC/TheMBWGueltigeKandidatenstimmenAnzeigenCard.vue";
import TheMBWUngueltigeStimmenAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelC/TheMBWUngueltigeStimmenAnzeigenCard.vue";
import { useMbwUtils } from "@/composables/ergebnismeldung/MBW/mbwUtils.ts";
import { useEreignisService } from "@/composables/vorfaelleundvorkommnisse/ereignisService.ts";
import { useEreignisUtils } from "@/composables/vorfaelleundvorkommnisse/ereignisUtils.ts";
import { ROUTE_NOTFOUND } from "@/constants.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { InputFeedbackTypeEnum } from "@/types/common/InputFeedbackTypeEnum.ts";
import { MeldungsArtEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import { MbwRoutesEnum } from "@/types/navigation/MbwRoutesEnum.ts";

const route = useRoute();
const router = useRouter();
const { wahlenActions } = useWahlenStore();
const { hasDoneVorkommnisse } = useEreignisUtils();
const { getEreignisse } = useEreignisService();
const { setStepDone, getElectionWorkflowState } = useWorkflowStore();

// button logic to be implemented
const isKorrigierenValid = ref<null | boolean>();
const isDruckenLoading = ref<boolean>(false);
const isSendenActive = ref<boolean>(true);

const currentUserWahlbezirkID = route.params.wahlbezirkId as string;
const wahlID = route.params.wahlId as string;
const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);
const ereignisse = ref<WahlbezirkEreignisse | null>(null);
const { isSendingNiederschrift, sendNiederschrift, sendAusdruckNiederschrift } =
  useMbwUtils(wahlID, currentUserWahlbezirkID);

if (!wahl) {
  router.push({
    name: ROUTE_NOTFOUND,
  });
}

const workflowState = computed(() =>
  getElectionWorkflowState(wahlID, currentUserWahlbezirkID)
);

onActivated(async () => {
  ereignisse.value = await getEreignisse(currentUserWahlbezirkID);
});

function onSendenClicked() {
  sendNiederschrift();
}
function onKorrigierenClicked() {
  // to be implemented
}

async function onDruckenClicked() {
  isDruckenLoading.value = true;
  const pdfText = "<div>test</div>";
  const printWindow = window.open(
    "",
    "",
    "left=0,top=0,width=800,height=900,toolbar=0,scrollbars=0,status=0"
  );

  if (printWindow) {
    printWindow.document.body.innerHTML = pdfText;
    printWindow.print();
    printWindow.close();

    setStepDone(
      wahlID,
      currentUserWahlbezirkID,
      MbwRoutesEnum.MBW_NIEDERSCHRIFT
    );
    if (workflowState.value) {
      workflowState.value.isNiederschriftDone = true;
    }
  }

  await sendAusdruckNiederschrift(MeldungsArtEnum.Niederschrift, pdfText);

  isDruckenLoading.value = false;
}
</script>
