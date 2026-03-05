<template>
  <base-ergebnismeldung-cards-container
    title="Schnellmeldung"
    subtitle="Kontrolle, Übermittlung und Druck der Schnellmeldung"
    :is-sending="isSendingSchnellmeldung"
    :is-korrigieren-active="isKorrigierenValid"
    :is-drucken-active="isDruckenValid"
    :is-drucken-loading="isDruckenLoading"
    :is-senden-active="isSendenActive"
    @save="onSendenClicked"
    @edit="onKorrigierenClicked"
    @print="onDruckenClicked"
  >
    <the-m-b-w-wahlberechtigte-anzeigen-card
      :wahlbezirk-id="wahlbezirkID"
      :wahl-id="wahlID"
    />
    <the-m-b-w-waehler-anzeigen-card
      :wahlbezirk-id="wahlbezirkID"
      :wahl-id="wahlID"
    />
    <the-m-b-w-ungueltige-stimmen-anzeigen-card
      :wahlbezirk-id="wahlbezirkID"
      :wahl-id="wahlID"
    />
    <the-m-b-w-gueltige-stimmen-anzeigen-card
      :wahlbezirk-id="wahlbezirkID"
      :wahl-id="wahlID"
    />
  </base-ergebnismeldung-cards-container>
</template>

<script setup lang="ts">
import type { SchnellmeldungDruckInput } from "@/types/ergebnismeldung/common/SchnellmeldungDruckInput.ts";

import { storeToRefs } from "pinia";
import { ref } from "vue";
import { useRoute, useRouter } from "vue-router";

import BaseErgebnismeldungCardsContainer from "@/components/ergebnismeldung/common/BaseErgebnismeldungCardsContainer.vue";
import TheMBWGueltigeStimmenAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWGueltigeStimmenAnzeigenCard.vue";
import TheMBWWaehlerAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWaehlerAnzeigenCard.vue";
import TheMBWWahlberechtigteAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWahlberechtigteAnzeigenCard.vue";
import TheMBWUngueltigeStimmenAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelC/TheMBWUngueltigeStimmenAnzeigenCard.vue";
import { useMbwUtils } from "@/composables/ergebnismeldung/MBW/mbwUtils.ts";
import { useSchnellmeldungDruck } from "@/composables/ergebnismeldung/MBW/schnellmeldungDruck.ts";
import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ROUTE_NOTFOUND } from "@/constants.ts";
import { useStatusStore } from "@/stores/statusStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { MeldungsArtEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import { MbwRoutesEnum } from "@/types/navigation/MbwRoutesEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const route = useRoute();
const router = useRouter();

const wahlbezirkID = route.params.wahlbezirkId as string;
const wahlID = route.params.wahlId as string;

const { addNotification } = useUserNotificationService();
const { wahlenActions } = useWahlenStore();
const { status } = storeToRefs(useStatusStore());
const {
  isSendingSchnellmeldung,
  sendSchnellmeldung,
  prepareDataForErgebnismeldungDruck,
} = useMbwUtils(wahlID, wahlbezirkID);
const { buildTemplateFromData } = useSchnellmeldungDruck();
const { setStepDone } = useWorkflowStore();
const { getNextRoute } = useNavigationUtils();

// button logic to be implemented
const isKorrigierenValid = ref<null | boolean>();
const isDruckenValid = ref<null | boolean>(true);
const isDruckenLoading = ref<boolean>(false);
const isSendenActive = ref<boolean>(true);

const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);
if (!wahl) {
  router.push({
    name: ROUTE_NOTFOUND,
  });
}

function onSendenClicked() {
  sendSchnellmeldung();
}
function onKorrigierenClicked() {
  // to be implemented
}

async function onDruckenClicked() {
  isDruckenLoading.value = true;
  try {
    const statusForWahlAndWahlbezirk = status.value.find(
      (status) =>
        status.bezirkUndWahlID.wahlID == wahlID &&
        status.bezirkUndWahlID.wahlbezirkID == wahlbezirkID
    );

    if (wahl && statusForWahlAndWahlbezirk) {
      const data: SchnellmeldungDruckInput =
        await prepareDataForErgebnismeldungDruck(
          wahl,
          statusForWahlAndWahlbezirk,
          MeldungsArtEnum.Schnellmeldung
        );

      const printWindow = window.open(
        "",
        "",
        "left=0,top=0,width=800,height=900,toolbar=0,scrollbars=0,status=0"
      );

      if (printWindow) {
        printWindow.document.body.innerHTML = buildTemplateFromData(data);
        printWindow.print();
        printWindow.close();
        isSendenActive.value = false;
        setStepDone(wahlID, wahlbezirkID, MbwRoutesEnum.MBW_SCHNELLMELDUNG);
        await router.push(getNextRoute());
      }

      // todo update status #2002
    }
  } catch {
    addNotification(
      "Fehler beim Drucken der Schnellmeldung.",
      UserNotificationCategoryEnum.WARNING
    );
  } finally {
    isDruckenLoading.value = false;
  }
}
</script>
