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
import type { Status } from "@/types/ergebnismeldung/common/Status.ts";
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
import { useLogging } from "@/composables/common/logging.ts";
import { useStatusUtils } from "@/composables/ergebnismeldung/common/statusUtils.ts";
import { useMbwUtils } from "@/composables/ergebnismeldung/MBW/mbwUtils.ts";
import { useMbtUtilsNiederschrift } from "@/composables/ergebnismeldung/MBW/mbwUtilsNiederschrift.ts";
import { useNiederschriftDruckBWB } from "@/composables/ergebnismeldung/MBW/niederschriftDruckBWB.ts";
import { useNiederschriftDruckUWB } from "@/composables/ergebnismeldung/MBW/niederschriftDruckUWB.ts";
import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { useEreignisService } from "@/composables/vorfaelleundvorkommnisse/ereignisService.ts";
import { useEreignisUtils } from "@/composables/vorfaelleundvorkommnisse/ereignisUtils.ts";
import { ROUTE_NOTFOUND } from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { InputFeedbackTypeEnum } from "@/types/common/InputFeedbackTypeEnum.ts";
import { MeldungsArtEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import { MbwRoutesEnum } from "@/types/navigation/MbwRoutesEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const route = useRoute();
const router = useRouter();
const { wahlenActions } = useWahlenStore();
const { loadStatusByWahlIdAndWahlbezirkId } = useStatusUtils();
const { addNotification } = useUserNotificationService();
const { hasDoneVorkommnisse } = useEreignisUtils();
const { getEreignisse } = useEreignisService();
const { setStepDone, getElectionWorkflowState } = useWorkflowStore();
const { getNextRoute } = useNavigationUtils();

// button logic to be implemented
const isKorrigierenValid = ref<null | boolean>();
const isDruckenLoading = ref<boolean>(false);

const isOfflineSyncDialogVisible = ref(false);
const isSyncErrorDialogVisible = ref(false);
const { logError } = useLogging("mbwNiederschriftView");
const currentUserWahlbezirkID = route.params.wahlbezirkId as string;
const wahlID = route.params.wahlId as string;
const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);
const ereignisse = ref<WahlbezirkEreignisse | null>(null);
const status = ref<Status | null>(null);

const { isSendingNiederschrift, sendNiederschrift, sendAusdruckNiederschrift } =
  useMbwUtils(wahlID, currentUserWahlbezirkID);
const { currentUserWahlbezirksArt } = storeToRefs(useUserStore());

const {
  buildNiederschriftTemplateFromData: buildNiederschriftTemplateFromDataUWB,
} = useNiederschriftDruckUWB();
const {
  buildNiederschriftTemplateFromData: buildNiederschriftTemplateFromDataBWB,
} = useNiederschriftDruckBWB();
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

const isSendenActive = computed(
  () =>
    !workflowState.value?.isNiederschriftDone &&
    !status.value?.niederschrift.gedruckt
);

onActivated(async () => {
  ereignisse.value = await getEreignisse(currentUserWahlbezirkID);
  status.value = await loadStatusByWahlIdAndWahlbezirkId(
    wahlID,
    currentUserWahlbezirkID
  );
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
    const pdfText = await buildNiederschriftTemplate();
    const printWindow = window.open(
      "",
      "",
      "left=0,top=0,width=800,height=900,toolbar=0,scrollbars=0,status=0"
    );

    if (printWindow) {
      printWindow.document.writeln(pdfText);
      printWindow.document.close();
      printWindow.print();
      printWindow.close();
    }

    setStepDone(
      wahlID,
      currentUserWahlbezirkID,
      MbwRoutesEnum.MBW_NIEDERSCHRIFT
    );
    if (workflowState.value) {
      workflowState.value.isNiederschriftDone = true;
    }
    await router.push(getNextRoute());

    await sendAusdruckNiederschrift(MeldungsArtEnum.Niederschrift, pdfText);
  } catch (e) {
    logError("mbwUtilsNiederschrift wirft einen Fehler", e);
    addNotification(
      "Fehler beim Drucken der Niederschrift.",
      UserNotificationCategoryEnum.ERROR
    );
  } finally {
    isDruckenLoading.value = false;
  }
}

async function buildNiederschriftTemplate() {
  if (status.value && wahl) {
    const templateData = await prepareDataForNiederschriftDruck(
      status.value,
      MeldungsArtEnum.Niederschrift,
      wahl
    );
    if (currentUserWahlbezirksArt.value === WahlbezirksArtEnum.UWB) {
      // @ts-expect-error correct data is determined by the if check
      return buildNiederschriftTemplateFromDataUWB(templateData);
    } else {
      // @ts-expect-error correct data is determined by the if check
      return buildNiederschriftTemplateFromDataBWB(templateData);
    }
  }
  return " ";
}
</script>
