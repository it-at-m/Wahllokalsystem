<template>
  <div>
    <base-ergebnismeldung-cards-container
      title="Niederschrift"
      subtitle="Kontrolle, Übermittlung und Druck der Niederschrift"
      :is-sending="isSendingNiederschrift"
      :is-korrigieren-active="isKorrigierenValid"
      :is-drucken-active="isDruckenActive"
      :is-drucken-loading="isDruckenLoading"
      :is-senden-active="isSendenActive"
      :is-beschlussentscheidungen-drucken-loading="
        isBeschlussentscheidungenDruckenLoading
      "
      @save="onSendenClicked"
      @edit="onKorrigierenClicked"
      @print-beschlussentscheidungen="onBeschlussentscheidungenDruckenClicked"
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
      <base-card-wahlvorschlaege-kandidatenstimmen-anzeigen
        :kandidatenstimmen="wahlvorschlaegeWithKandidatenErgebnissen"
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
    <base-beschlussentscheidungen-drucken-info-dialog
      v-model="isBeschlussentscheidungenDruckenDialogVisble"
    />
  </div>
</template>

<script setup lang="ts">
import type { Status } from "@/types/ergebnismeldung/common/Status.ts";
import type { WahlbezirkEreignisse } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse.ts";

import { storeToRefs } from "pinia";
import { computed, onActivated, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

import BaseDialog from "@/components/common/dialogs/BaseDialog.vue";
import BaseBeschlussentscheidungenDruckenInfoDialog from "@/components/dse/beschlussfassung/BaseBeschlussentscheidungenDruckenInfoDialog.vue";
import BaseCardWahlvorschlaegeKandidatenstimmenAnzeigen from "@/components/ergebnismeldung/common/BaseCardWahlvorschlaegeKandidatenstimmenAnzeigen.vue";
import BaseErgebnismeldungCardsContainer from "@/components/ergebnismeldung/common/BaseErgebnismeldungCardsContainer.vue";
import TheVorkommnisseRequirementCard from "@/components/ergebnismeldung/common/TheVorkommnisseRequirementCard.vue";
import TheMBWGueltigeStimmenAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWGueltigeStimmenAnzeigenCard.vue";
import TheMBWWaehlerAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWaehlerAnzeigenCard.vue";
import TheMBWWahlberechtigteAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWahlberechtigteAnzeigenCard.vue";
import TheMBWUngueltigeStimmenAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelC/TheMBWUngueltigeStimmenAnzeigenCard.vue";
import OfflineSyncerDialog from "@/components/wlsComponents/OfflineSyncerDialog.vue";
import { useLogging } from "@/composables/common/logging.ts";
import { useBeschlussentscheidungenDruckenTools } from "@/composables/dse/beschlussfassung/beschlussentscheidungenDruckenTools.ts";
import { useBeschlussentscheidungenDruckTemplateTools } from "@/composables/dse/beschlussfassung/beschlussentscheidungenDruckTemplateTools.ts";
import { useBeschlussfassungViewUtils } from "@/composables/dse/beschlussfassung/beschlussfassungViewUtils.ts";
import { useStatusUtils } from "@/composables/ergebnismeldung/common/statusUtils.ts";
import { useMbwUtils } from "@/composables/ergebnismeldung/MBW/mbwUtils.ts";
import { useMbtUtilsNiederschrift } from "@/composables/ergebnismeldung/MBW/mbwUtilsNiederschrift.ts";
import { useMwbStapelBCUtils } from "@/composables/ergebnismeldung/MBW/mwbStapelBCUtils.ts";
import { useNiederschriftDruckBWB } from "@/composables/ergebnismeldung/MBW/niederschriftDruckBWB.ts";
import { useNiederschriftDruckUWB } from "@/composables/ergebnismeldung/MBW/niederschriftDruckUWB.ts";
import { useNavigationService } from "@/composables/navigation/navigationService.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { useEreignisService } from "@/composables/vorfaelleundvorkommnisse/ereignisService.ts";
import { useEreignisUtils } from "@/composables/vorfaelleundvorkommnisse/ereignisUtils.ts";
import { ROUTE_NOTFOUND } from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { InputFeedbackTypeEnum } from "@/types/common/InputFeedbackTypeEnum.ts";
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
const { getEreignisse } = useEreignisService();
const { setStepDone, getElectionWorkflowState } = useWorkflowStore();
const { getNextRoute } = useNavigationService();

// button logic to be implemented
const isKorrigierenValid = ref<null | boolean>();
const isBeschlussentscheidungenDruckenLoading = ref<boolean>(false);
const isDruckenLoading = ref<boolean>(false);
const isNiederschriftSendenClicked = ref<boolean>(false);

const isOfflineSyncDialogVisible = ref(false);
const isSyncErrorDialogVisible = ref(false);
const isBeschlussentscheidungenDruckenDialogVisble = ref(false);
const { logError } = useLogging("mbwNiederschriftView");
const currentUserWahlbezirkID = route.params.wahlbezirkId as string;
const wahlID = route.params.wahlId as string;
const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);
const ereignisse = ref<WahlbezirkEreignisse | null>(null);
const status = ref<Status | null>(null);

const { isSendingNiederschrift, sendNiederschrift, sendAusdruckNiederschrift } =
  useMbwUtils(wahlID, currentUserWahlbezirkID);
const { currentUserWahlbezirksArt } = storeToRefs(useUserStore());
const { stimmzettelForBeschlussfassung } = useBeschlussfassungViewUtils(
  wahlID,
  currentUserWahlbezirkID
);

const {
  sendAusdruckBeschlussentscheidungen,
  prepareDataForBeschlussentscheidungenDruck,
} = useBeschlussentscheidungenDruckenTools(wahlID, currentUserWahlbezirkID);
const { buildTemplate } = useBeschlussentscheidungenDruckTemplateTools();
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

const {
  wahlvorschlaegeWithKandidatenErgebnissen,
  loadWahlvorschlaegeAndErgebnisse,
} = useMwbStapelBCUtils(currentUserWahlbezirkID, wahlID);

onActivated(async () => {
  await loadWahlvorschlaegeAndErgebnisse();
  ereignisse.value = await getEreignisse(currentUserWahlbezirkID);
  status.value = await loadStatusByWahlIdAndWahlbezirkId(
    wahlID,
    currentUserWahlbezirkID
  );
});

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

async function onBeschlussentscheidungenDruckenClicked() {
  isBeschlussentscheidungenDruckenLoading.value = true;
  try {
    const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);
    if (wahl) {
      const pdfText = buildTemplate(
        prepareDataForBeschlussentscheidungenDruck(
          wahl,
          stimmzettelForBeschlussfassung.value
        )
      );
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

      isBeschlussentscheidungenDruckenDialogVisble.value = true;

      await sendAusdruckBeschlussentscheidungen(
        MeldungsArtEnum.Schnellmeldung,
        pdfText
      );
    }
  } catch {
    addNotification(
      "Fehler beim Drucken der Beschlussentscheidungen.",
      UserNotificationCategoryEnum.WARNING
    );
  } finally {
    isBeschlussentscheidungenDruckenLoading.value = false;
  }
}
</script>
