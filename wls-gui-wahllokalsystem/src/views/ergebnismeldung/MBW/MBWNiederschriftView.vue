<template>
  <div>
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
    <offline-syncer-dialog
      :is-dialog-visible="isOfflineSyncDialogVisible"
      @sync-success="onSyncSuccess"
      @sync-error="onSyncError"
    />
    <base-dialog
      :visible="isSyncErrorDialogVisible"
      dialogtitle="Fehler bei der Synchronisation"
      confirmtext="Hinweis schließen"
      icon="$information"
      @confirm="isSyncErrorDialogVisible = false"
    >
      <div class="mb-4">
        Bei der Synchronisation der Offline-Daten ist ein Fehler aufgetreten. Um
        zu verhindern, dass beim Senden der Niederschrift unvollständige Daten
        verschickt werden, wurde der Vorgang abgebrochen.
      </div>
    </base-dialog>
  </div>
</template>

<script setup lang="ts">
import type { WahlbezirkEreignisse } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse.ts";

import { storeToRefs } from "pinia";
import { computed, onActivated, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import BaseErgebnismeldungCardsContainer from "@/components/ergebnismeldung/common/BaseErgebnismeldungCardsContainer.vue";
import TheVorkommnisseRequirementCard from "@/components/ergebnismeldung/common/TheVorkommnisseRequirementCard.vue";
import TheMBWGueltigeStimmenAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWGueltigeStimmenAnzeigenCard.vue";
import TheMBWWaehlerAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWaehlerAnzeigenCard.vue";
import TheMBWWahlberechtigteAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWahlberechtigteAnzeigenCard.vue";
import TheMBWGueltigeKandidatenstimmenAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelBC/TheMBWGueltigeKandidatenstimmenAnzeigenCard.vue";
import TheMBWUngueltigeStimmenAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelC/TheMBWUngueltigeStimmenAnzeigenCard.vue";
import OfflineSyncerDialog from "@/components/wlsComponents/OfflineSyncerDialog.vue";
import { useMbwUtils } from "@/composables/ergebnismeldung/MBW/mbwUtils.ts";
import { useMbtUtilsNiederschrift } from "@/composables/ergebnismeldung/MBW/mbwUtilsNiederschrift.ts";
import { useNiederschriftDruckUWB } from "@/composables/ergebnismeldung/MBW/niederschriftDruckUWB.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { useEreignisService } from "@/composables/vorfaelleundvorkommnisse/ereignisService.ts";
import { useEreignisUtils } from "@/composables/vorfaelleundvorkommnisse/ereignisUtils.ts";
import { ROUTE_NOTFOUND } from "@/constants.ts";
import { useErgebnismeldungStore } from "@/stores/ergebnismeldungStore.ts";
import { useStatusStore } from "@/stores/statusStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { InputFeedbackTypeEnum } from "@/types/common/InputFeedbackTypeEnum.ts";
import { MeldungsArtEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const route = useRoute();
const router = useRouter();
const { wahlenActions } = useWahlenStore();
const { sendNiederschrift } = useErgebnismeldungStore();
const { isNiederschriftAndStatusSaving } = storeToRefs(
  useErgebnismeldungStore()
);
const { addNotification } = useUserNotificationService();
const { hasDoneVorkommnisse } = useEreignisUtils();
const { status } = storeToRefs(useStatusStore());
const { getEreignisse } = useEreignisService();
const { setStepDone, getElectionWorkflowState } = useWorkflowStore();

// button logic to be implemented
const isKorrigierenValid = ref<null | boolean>();
const isDruckenLoading = ref<boolean>(false);
const isSendenActive = ref<boolean>(true);

const isOfflineSyncDialogVisible = ref(false);
const isSyncErrorDialogVisible = ref(false);

const currentUserWahlbezirkID = route.params.wahlbezirkId as string;
const wahlID = route.params.wahlId as string;
const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);
const ereignisse = ref<WahlbezirkEreignisse | null>(null);
const { isSendingNiederschrift, sendNiederschrift, sendAusdruckNiederschrift } =
  useMbwUtils(wahlID, currentUserWahlbezirkID);

const { gatherData } = useNiederschriftDruckUWB();
const { prepareDataForNiederschriftDruck } = useMbtUtilsNiederschrift(
  wahlID,
  currentUserWahlbezirkID
);
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
  isOfflineSyncDialogVisible.value = true;
}

async function onSyncSuccess() {
  isOfflineSyncDialogVisible.value = false;
  await sendNiederschrift();
}

function onSyncError() {
  isOfflineSyncDialogVisible.value = false;
  isSyncErrorDialogVisible.value = true;
}

function onKorrigierenClicked() {
  // to be implemented
}
async function onDruckenClicked() {
  isDruckenLoading.value = true;
  try {
    const pdfText = await collectDataForTemplateBuild();
    const printWindow = window.open(
      "",
      "",
      "left=0,top=0,width=800,height=900,toolbar=0,scrollbars=0,status=0"
    );

    if (printWindow) {
      printWindow.document.body.innerHTML = pdfText;
      printWindow.print();
      printWindow.close();
    }
    const statusForWahlAndWahlbezirk = status.value.find(
      (status) =>
        status.bezirkUndWahlID.wahlID == wahlID &&
        status.bezirkUndWahlID.wahlbezirkID == currentUserWahlbezirkID
    );
    if (statusForWahlAndWahlbezirk) {
      statusForWahlAndWahlbezirk.niederschrift.gedruckt = true;
    }
    await sendAusdruckNiederschrift(MeldungsArtEnum.Niederschrift, pdfText);
  } catch {
    addNotification(
      "Fehler beim Drucken der Niederschrift.",
      UserNotificationCategoryEnum.WARNING
    );
  } finally {
    isDruckenLoading.value = false;
  }
}

async function collectDataForTemplateBuild() {
  const statusForWahlAndWahlbezirk = status.value.find(
    (status) =>
      status.bezirkUndWahlID.wahlID == wahlID &&
      status.bezirkUndWahlID.wahlbezirkID == currentUserWahlbezirkID
  );
  if (statusForWahlAndWahlbezirk && wahl) {
    const templateData = await prepareDataForNiederschriftDruck(
      statusForWahlAndWahlbezirk,
      MeldungsArtEnum.Niederschrift,
      wahl
    );
    return gatherData(templateData);
  }
  return " ";
}
</script>
