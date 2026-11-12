import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";
import type { EmitFn } from "vue";

import { storeToRefs } from "pinia";
import { computed, nextTick, ref, useTemplateRef } from "vue";
import { useRoute } from "vue-router";

import { useStimmzettelManager } from "@/composables/dse/stimmzettelerfassung/stimmzettelManager.ts";
import { SAVE_CONTINUE } from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";

export function useStimmzettelerfassungDialogUtils(
  wahlvorschlaege: Wahlvorschlag[],
  emit: EmitFn<{
    cancel: [];
    confirmClose: [stimmzettel: PersistedStimmzettel];
    confirmNext: [stimmzettel: PersistedStimmzettel];
  }>
) {
  const isDialogVisible = ref(false);
  const isReadonly = ref(false);
  const stimmzettelkennung = ref(0);
  const isCancelConfirmationDialogVisible = ref(false);

  const route = useRoute();
  const wahlID = route.params.wahlId as string;
  const { currentUserTeamName } = storeToRefs(useUserStore());

  const BASE_CONTENT_TEMPLATE_REF_NAME =
    "baseStimmzettelErfassungCardContent" as const;
  const baseContentRef = useTemplateRef<{
    focusCommandProcessingTextField: () => void;
  }>(BASE_CONTENT_TEMPLATE_REF_NAME);

  const stimmzettelManager = useStimmzettelManager(
    computed(() => stimmzettelkennung.value),
    wahlvorschlaege,
    wahlID
  );

  const confirmActions = {
    saveAndContinue: {
      title: SAVE_CONTINUE,
      action: () =>
        emit("confirmNext", stimmzettelManager.getStimmzettelSnapshot()),
    },
    saveAndClose: {
      title: "Speichern und Schließen",
      action: () =>
        emit("confirmClose", stimmzettelManager.getStimmzettelSnapshot()),
    },
  };
  const actions = Object.values(confirmActions);
  const currentAction = ref(confirmActions.saveAndContinue);

  const stimmzettelGueltigkeit = computed(
    () =>
      stimmzettelManager.bearbeitenDialogStimmzettelUtils.stimmzettel.value
        .gueltigkeit
  );

  const isSaveDisabled = computed(() => {
    if (
      stimmzettelManager.stimmzettelBeforeEdit.value !== null &&
      stimmzettelManager.bearbeitenDialogStimmzettelUtils.hasAnyValuesSet
        .value &&
      !stimmzettelManager.hasStimmzettelBeenEdited.value
    )
      return true;

    return (
      !stimmzettelManager.bearbeitenDialogStimmzettelUtils.hasAnyValuesSet
        .value &&
      stimmzettelGueltigkeit.value !== StimmzettelGueltigkeitEnum.Leer &&
      stimmzettelGueltigkeit.value !==
        StimmzettelGueltigkeitEnum.BwbPseudoStimmzettelLeererUmschlag &&
      stimmzettelGueltigkeit.value !==
        StimmzettelGueltigkeitEnum.BeschlussAusstehend &&
      !!stimmzettelManager.bearbeitenDialogStimmzettelUtils.stimmzettel.value
    );
  });

  const isResetDisabled = computed(() => {
    if (stimmzettelManager.stimmzettelBeforeEdit.value !== null) {
      return !stimmzettelManager.hasStimmzettelBeenEdited.value;
    }
    return !stimmzettelManager.bearbeitenDialogStimmzettelUtils.hasAnyValuesSet
      .value;
  });

  const isCancelButtonDisabled = computed(
    () => stimmzettelManager.hasStimmzettelBeenEdited.value
  );

  function onResetClicked() {
    if (
      stimmzettelManager.hasStimmzettelBeenEdited.value &&
      stimmzettelManager.stimmzettelBeforeEdit.value !== null
    ) {
      stimmzettelManager.bearbeitenDialogStimmzettelUtils.resetStimmzettelAndHistory(
        stimmzettelManager.stimmzettelBeforeEdit.value
      );
    } else {
      stimmzettelManager.bearbeitenDialogStimmzettelUtils.resetStimmzettelAndHistory();
    }
  }

  function closeDialog() {
    isDialogVisible.value = false;
  }

  async function focusCommandProcessingTextField() {
    await nextTick();
    baseContentRef.value?.focusCommandProcessingTextField();
  }

  function onCancelClicked() {
    if (
      stimmzettelManager.bearbeitenDialogStimmzettelUtils.hasAnyValuesSet.value
    ) {
      closeDialog();
    } else {
      isCancelConfirmationDialogVisible.value = true;
    }
  }

  function onCancelConfirmationDialogCancelled() {
    isCancelConfirmationDialogVisible.value = false;
    void focusCommandProcessingTextField();
  }

  function onCancelConfirmationDialogConfirmed() {
    isCancelConfirmationDialogVisible.value = false;
    emit("cancel");
  }

  function onLeerenClicked() {
    stimmzettelManager.startWithClearedStimmzettel();
    void focusCommandProcessingTextField();
  }

  function showDialog(
    stimmzettelToEditOrKennungOfNew: PersistedStimmzettel | number,
    readonly = false
  ) {
    isReadonly.value = readonly;

    if (typeof stimmzettelToEditOrKennungOfNew === "number") {
      stimmzettelkennung.value = stimmzettelToEditOrKennungOfNew;
      stimmzettelManager.startNewStimmzettel(currentUserTeamName.value);
      currentAction.value = confirmActions.saveAndContinue;
    } else {
      stimmzettelkennung.value =
        stimmzettelToEditOrKennungOfNew.stimmzettelkennung;
      stimmzettelManager.startEditStimmzettel(stimmzettelToEditOrKennungOfNew);
      currentAction.value = confirmActions.saveAndClose;
    }
    isDialogVisible.value = true;
    void focusCommandProcessingTextField();
  }

  return {
    isDialogVisible,
    isReadonly,
    stimmzettelkennung,
    currentUserTeamName,
    isCancelConfirmationDialogVisible,
    BASE_CONTENT_TEMPLATE_REF_NAME,
    actions,
    currentAction,
    stimmzettelGueltigkeit,
    isSaveDisabled,
    isResetDisabled,
    isCancelButtonDisabled,
    stimmzettelManager,
    onResetClicked,
    closeDialog,
    focusCommandProcessingTextField,
    showDialog,
    onCancelClicked,
    onCancelConfirmationDialogCancelled,
    onCancelConfirmationDialogConfirmed,
    onLeerenClicked,
  };
}
