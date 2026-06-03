<template>
  <div>
    <base-ergebnismeldung-cards-container
      title="Schnellmeldung"
      subtitle="Kontrolle, Übermittlung und Druck der Schnellmeldung"
      :is-sending="isSendingSchnellmeldung"
      :is-korrigieren-active="isKorrigierenValid"
      :is-drucken-active="isDruckenActive"
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
        :is-schnellmeldung="true"
        :wahlbezirk-id="wahlbezirkID"
        :wahl-id="wahlID"
      />
    </base-ergebnismeldung-cards-container>
    <offline-syncer-dialog
      :is-dialog-visible="isOfflineSyncDialogVisible"
      @sync-success="onSyncSuccess"
      @sync-error="onSyncError"
    />
    <base-dialog
      :visible="isUebermitteltErrorDialogVisible"
      dialogtitle="Fehler beim Senden der Schnellmeldung"
      confirmtext="Hinweis schließen"
      icon="$information"
      @confirm="isUebermitteltErrorDialogVisible = false"
    >
      <div class="mb-4">
        Die Schnellmeldung kann derzeit nicht gesendet werden. Bitte geben Sie
        daher die Ergebnisse telefonisch an die Wahl-Hotline durch. Danach
        können Sie die Auszählung ohne Einschränkung fortsetzen.
      </div>
    </base-dialog>
  </div>
</template>

<script setup lang="ts">
import type { SchnellmeldungDruckInput } from "@/types/ergebnismeldung/common/SchnellmeldungDruckInput.ts";
import type { Status } from "@/types/ergebnismeldung/common/Status.ts";

import { computed, onActivated, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import BaseErgebnismeldungCardsContainer from "@/components/ergebnismeldung/common/BaseErgebnismeldungCardsContainer.vue";
import TheMBWGueltigeStimmenAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWGueltigeStimmenAnzeigenCard.vue";
import TheMBWWaehlerAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWaehlerAnzeigenCard.vue";
import TheMBWWahlberechtigteAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWahlberechtigteAnzeigenCard.vue";
import TheMBWUngueltigeStimmenAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelC/TheMBWUngueltigeStimmenAnzeigenCard.vue";
import OfflineSyncerDialog from "@/components/wlsComponents/OfflineSyncerDialog.vue";
import { useStatusUtils } from "@/composables/ergebnismeldung/common/statusUtils.ts";
import { useMbwUtils } from "@/composables/ergebnismeldung/MBW/mbwUtils.ts";
import { useSchnellmeldungDruck } from "@/composables/ergebnismeldung/MBW/schnellmeldungDruck.ts";
import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { ROUTE_NOTFOUND } from "@/constants.ts";
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
const {
  isSendingSchnellmeldung,
  sendSchnellmeldung,
  prepareDataForSchnellmeldungDruck,
  updateStatusAfterSchnellmeldungDrucken,
} = useMbwUtils(wahlID, wahlbezirkID);
const { buildSchnellmeldungTemplateFromData } = useSchnellmeldungDruck();
const { setStepDone, getElectionWorkflowState } = useWorkflowStore();
const { getNextRoute } = useNavigationUtils();
const { loadStatusByWahlIdAndWahlbezirkId } = useStatusUtils();

// button logic to be implemented
const isKorrigierenValid = ref<null | boolean>();
const isDruckenLoading = ref<boolean>(false);
const isSchnellmeldungSendenClicked = ref<boolean>(false);

const status = ref<Status | null>(null);

const isOfflineSyncDialogVisible = ref(false);
const isUebermitteltErrorDialogVisible = ref(false);

const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);
if (!wahl) {
  router.push({
    name: ROUTE_NOTFOUND,
  });
}

const workflowState = computed(() =>
  getElectionWorkflowState(wahlID, wahlbezirkID)
);

const isSendenActive = computed(
  () =>
    !workflowState.value?.isSchnellmeldungDone &&
    !status.value?.schnellmeldung.gedruckt
);

const isDruckenActive = computed(
  () =>
    status.value?.schnellmeldung.uebermittelt ||
    isSchnellmeldungSendenClicked.value
);

onActivated(async () => {
  status.value = await loadStatusByWahlIdAndWahlbezirkId(wahlID, wahlbezirkID);
});

function onSendenClicked() {
  isOfflineSyncDialogVisible.value = true;
  isSchnellmeldungSendenClicked.value = true;
}

async function onSyncSuccess() {
  isOfflineSyncDialogVisible.value = false;
  await sendSchnellmeldung();
  status.value = await loadStatusByWahlIdAndWahlbezirkId(wahlID, wahlbezirkID);
  if (!status.value.schnellmeldung.uebermittelt) {
    isUebermitteltErrorDialogVisible.value = true;
  }
}

function onSyncError() {
  isOfflineSyncDialogVisible.value = false;
  isUebermitteltErrorDialogVisible.value = true;
}

function onKorrigierenClicked() {
  // to be implemented
}

async function onDruckenClicked() {
  isDruckenLoading.value = true;
  const status = await loadStatusByWahlIdAndWahlbezirkId(wahlID, wahlbezirkID);
  try {
    if (wahl) {
      const data: SchnellmeldungDruckInput =
        await prepareDataForSchnellmeldungDruck(
          wahl,
          status,
          MeldungsArtEnum.Schnellmeldung
        );

      const printWindow = window.open(
        "",
        "",
        "left=0,top=0,width=800,height=900,toolbar=0,scrollbars=0,status=0"
      );

      if (printWindow) {
        printWindow.document.body.innerHTML =
          buildSchnellmeldungTemplateFromData(data);
        printWindow.print();
        printWindow.close();

        await updateStatusAfterSchnellmeldungDrucken();

        setStepDone(wahlID, wahlbezirkID, MbwRoutesEnum.MBW_SCHNELLMELDUNG);
        await router.push(getNextRoute());

        if (workflowState.value) {
          workflowState.value.isSchnellmeldungDone = true;
        }
      }
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
