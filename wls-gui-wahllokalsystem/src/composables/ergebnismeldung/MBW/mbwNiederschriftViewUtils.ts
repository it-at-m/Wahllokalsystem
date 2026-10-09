import type { Status } from "@/types/ergebnismeldung/common/Status.ts";
import type { WahlbezirkEreignisse } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse.ts";
import type { Router } from "vue-router";

import { storeToRefs } from "pinia";
import { computed, onActivated, ref } from "vue";

import { useLogging } from "@/composables/common/logging.ts";
import { useBeschlussentscheidungenDruckenTools } from "@/composables/dse/beschlussfassung/beschlussentscheidungenDruckenTools.ts";
import { useBeschlussentscheidungenDruckTemplateTools } from "@/composables/dse/beschlussfassung/beschlussentscheidungenDruckTemplateTools.ts";
import { useBeschlussfassungViewUtils } from "@/composables/dse/beschlussfassung/beschlussfassungViewUtils.ts";
import { useStatusUtils } from "@/composables/ergebnismeldung/common/statusUtils.ts";
import { useMbwNiederschriftDruckService } from "@/composables/ergebnismeldung/MBW/mbwNiederschriftDruckService.ts";
import { useMbwUtils } from "@/composables/ergebnismeldung/MBW/mbwUtils.ts";
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
import { MeldungsArtEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import { MbwStepsEnum } from "@/types/navigation/MbwStepsEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

export function useMbwNiederschriftViewUtils(
  wahlID: string,
  wahlbezirkID: string,
  router: Router
) {
  const { wahlenActions } = useWahlenStore();
  const { loadStatusByWahlIdAndWahlbezirkId } = useStatusUtils();
  const { addNotification } = useUserNotificationService();
  const { hasDoneVorkommnisse } = useEreignisUtils();
  const { getEreignisse } = useEreignisService();
  const { isStepDone, setStepDone, getElectionWorkflowState } =
    useWorkflowStore();
  const { getNextRoute } = useNavigationService();
  const { logError } = useLogging("mbwNiederschriftView");
  const { currentUserWahlbezirksArt } = storeToRefs(useUserStore());
  const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);

  const isKorrigierenValid = ref<null | boolean>();
  const isBeschlussentscheidungenDruckenLoading = ref(false);
  const isDruckenLoading = ref(false);
  const isNiederschriftSendenClicked = ref(false);
  const isOfflineSyncDialogVisible = ref(false);
  const isSyncErrorDialogVisible = ref(false);
  const isBeschlussentscheidungenDruckenDialogVisble = ref(false);
  const ereignisse = ref<WahlbezirkEreignisse | null>(null);
  const status = ref<Status | null>(null);

  const {
    getAusdruckNiederschrift,
    isSendingNiederschrift,
    sendNiederschrift,
    sendAusdruckNiederschrift,
  } = useMbwUtils(wahlID, wahlbezirkID);
  const { stimmzettelForBeschlussfassung } = useBeschlussfassungViewUtils(
    wahlID,
    wahlbezirkID
  );
  const {
    sendAusdruckBeschlussentscheidungen,
    prepareDataForBeschlussentscheidungenDruck,
  } = useBeschlussentscheidungenDruckenTools(wahlID, wahlbezirkID);
  const { buildTemplate } = useBeschlussentscheidungenDruckTemplateTools();
  const {
    buildNiederschriftTemplateFromData: buildNiederschriftTemplateFromDataUWB,
  } = useNiederschriftDruckUWB();
  const {
    buildNiederschriftTemplateFromData: buildNiederschriftTemplateFromDataBWB,
  } = useNiederschriftDruckBWB();
  const { prepareDataForNiederschriftDruck } = useMbwNiederschriftDruckService(
    wahlID,
    wahlbezirkID
  );

  if (!wahl) {
    router.push({ name: ROUTE_NOTFOUND });
  }

  const workflowState = computed(() =>
    getElectionWorkflowState(wahlID, wahlbezirkID)
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

  onActivated(async () => {
    ereignisse.value = await getEreignisse(wahlbezirkID);
    status.value = await loadStatusByWahlIdAndWahlbezirkId(
      wahlID,
      wahlbezirkID
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
      wahlbezirkID
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
    const niederschriftAlreadyDone = isStepDone(
      wahlID,
      wahlbezirkID,
      MbwStepsEnum.MBW_NIEDERSCHRIFT
    );

    try {
      const pdfText = niederschriftAlreadyDone
        ? await getAusdruckNiederschrift()
        : await buildNiederschriftTemplate();

      const printWindow = window.open(
        "",
        "",
        "left=0,top=0,width=800,height=900,toolbar=0,scrollbars=0,status=0"
      );

      if (printWindow) {
        printWindow.document.writeln(pdfText);
        printWindow.document.close();

        const images = printWindow.document.querySelectorAll("img");

        const imagePromises = Array.from(images).map((img) =>
          img.complete
            ? Promise.resolve()
            : img.decode().catch(() => {
                return;
              })
        );

        await Promise.all(imagePromises);
        printWindow.print();
        printWindow.close();
      }

      if (!niederschriftAlreadyDone) {
        await sendAusdruckNiederschrift(MeldungsArtEnum.Niederschrift, pdfText);

        setStepDone(wahlID, wahlbezirkID, MbwStepsEnum.MBW_NIEDERSCHRIFT);
        if (workflowState.value) {
          workflowState.value.isNiederschriftDone = true;
        }
      }
      await router.push(getNextRoute());
    } catch (e) {
      logError(
        "Fehler während des Druckvorgangs oder der Datenvorbereitung",
        e
      );
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
          MeldungsArtEnum.Beschlussentscheidungen,
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

  return {
    ereignisse,
    isBeschlussentscheidungenDruckenDialogVisble,
    isBeschlussentscheidungenDruckenLoading,
    isDruckenActive,
    isDruckenLoading,
    isKorrigierenValid,
    isOfflineSyncDialogVisible,
    isSendenActive,
    isSendingNiederschrift,
    isSyncErrorDialogVisible,
    onBeschlussentscheidungenDruckenClicked,
    onDruckenClicked,
    onKorrigierenClicked,
    onSendenClicked,
    onSyncError,
    onSyncSuccess,
  };
}
