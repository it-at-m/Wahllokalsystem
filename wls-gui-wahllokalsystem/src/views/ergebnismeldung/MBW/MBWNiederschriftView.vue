<template>
  <div>
    <the-mbw-dse-niederschrift-card
      v-if="isDseAktiv"
      :wahlbezirk-i-d="currentUserWahlbezirkID"
      :wahl-i-d="wahlID"
      :is-sending-niederschrift="isSendingNiederschrift"
      :is-korrigieren-valid="isKorrigierenValid"
      :is-drucken-active="isDruckenActive"
      :is-drucken-loading="isDruckenLoading"
      :is-senden-active="isSendenActive"
      @save="onSendenClicked"
      @edit="onKorrigierenClicked"
      @print="onDruckenClicked"
    />
    <the-mbw-stapel-niederschrift-card
      v-else
      :wahlbezirk-i-d="currentUserWahlbezirkID"
      :wahl-i-d="wahlID"
      :is-sending-niederschrift="isSendingNiederschrift"
      :is-korrigieren-valid="isKorrigierenValid"
      :is-drucken-active="isDruckenActive"
      :is-drucken-loading="isDruckenLoading"
      :is-senden-active="isSendenActive"
      @save="onSendenClicked"
      @edit="onKorrigierenClicked"
      @print="onDruckenClicked"
    />
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
import { computed, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import TheMbwDseNiederschriftCard from "@/components/ergebnismeldung/MBW/TheMbwDseNiederschriftCard.vue";
import TheMbwStapelNiederschriftCard from "@/components/ergebnismeldung/MBW/TheMbwStapelNiederschriftCard.vue";
import OfflineSyncerDialog from "@/components/wlsComponents/OfflineSyncerDialog.vue";
import { useLogging } from "@/composables/common/logging.ts";
import { useStatusUtils } from "@/composables/ergebnismeldung/common/statusUtils.ts";
import { useMbwUtils } from "@/composables/ergebnismeldung/MBW/mbwUtils.ts";
import { useMbtUtilsNiederschrift } from "@/composables/ergebnismeldung/MBW/mbwUtilsNiederschrift.ts";
import { useNiederschriftDruckBWB } from "@/composables/ergebnismeldung/MBW/niederschriftDruckBWB.ts";
import { useNiederschriftDruckUWB } from "@/composables/ergebnismeldung/MBW/niederschriftDruckUWB.ts";
import { useNavigationService } from "@/composables/navigation/navigationService.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { useEreignisUtils } from "@/composables/vorfaelleundvorkommnisse/ereignisUtils.ts";
import { ROUTE_NOTFOUND } from "@/constants.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { MeldungsArtEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import { MbwStepsEnum } from "@/types/navigation/MbwStepsEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const route = useRoute();
const router = useRouter();
const { wahlenActions } = useWahlenStore();
const { loadStatusByWahlIdAndWahlbezirkId } = useStatusUtils();
const { addNotification } = useUserNotificationService();
const { hasDoneVorkommnisse } = useEreignisUtils();
const { setStepDone, getElectionWorkflowState } = useWorkflowStore();
const { getNextRoute } = useNavigationService();
const { isDseAktiv } = storeToRefs(useInfomanagementStore());

// button logic to be implemented
const isKorrigierenValid = ref<null | boolean>();
const isDruckenLoading = ref<boolean>(false);
const isNiederschriftSendenClicked = ref<boolean>(false);

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
    (!workflowState.value?.isNiederschriftDone &&
      !status.value?.niederschrift.gedruckt &&
      !!status.value?.niederschrift.uebermittelt) ||
    !status.value?.niederschrift.uebermittelt
);

const isDruckenActive = computed(
  () =>
    hasDoneVorkommnisse(ereignisse.value) &&
    (status.value?.niederschrift.uebermittelt ||
      status.value?.niederschrift.gedruckt ||
      isNiederschriftSendenClicked.value)
);

function onSendenClicked() {
  isOfflineSyncDialogVisible.value = true;
  isNiederschriftSendenClicked.value = true;
}

async function onSyncSuccess() {
  isOfflineSyncDialogVisible.value = false;
  await sendNiederschrift();
  status.value = await loadStatusByWahlIdAndWahlbezirkId(
    wahlID,
    currentUserWahlbezirkID
  );
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
      MbwStepsEnum.MBW_NIEDERSCHRIFT
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
