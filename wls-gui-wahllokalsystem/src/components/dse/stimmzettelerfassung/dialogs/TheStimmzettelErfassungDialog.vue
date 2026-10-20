<template>
  <v-dialog
    v-model="isDialogVisible"
    eager
    persistent
    fullscreen
  >
    <v-card
      class="h-screen"
      style="min-height: 0; max-width: 100%"
    >
      <v-card-title>
        Erfassung Stimmzettel Nummer {{ currentUserTeamName }}
        {{ stimmzettelkennung }}
      </v-card-title>
      <base-stimmzettel-erfassung-card-content
        :ref="BASE_CONTENT_TEMPLATE_REF_NAME"
        v-model="stimmzettelManager"
        :stimmzettelkennung="stimmzettelkennung"
        :wahlvorschlaege="wahlvorschlaege"
        :stimmzettel-gueltigkeit="stimmzettelGueltigkeit"
      />
      <v-card-actions>
        <base-text-button
          :disabled="isResetDisabled"
          @click="onResetClicked"
        >
          Zurücksetzen
        </base-text-button>
        <base-text-button @click="onLeerenClicked">
          Stimmzettel leeren
        </base-text-button>
        <v-spacer />
        <base-text-button
          :disabled="isCancelButtonDisabled"
          @click="onCancelClicked"
          >Abbrechen</base-text-button
        >
        <base-save-button-with-action-menu
          :disabled="isSaveDisabled"
          :model-value="currentAction"
          :actions="actions"
        />
      </v-card-actions>
      <the-stimmzettel-erfassung-cancel-confirmation-dialog
        :visible="isCancelConfirmationDialogVisible"
        :team-name="currentUserTeamName"
        :stimmzettelkennung="stimmzettelkennung"
        @confirm="onCancelConfirmationDialogConfirmed"
        @cancel="onCancelConfirmationDialogCancelled"
      />
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";
import type { PropType } from "vue";

import { storeToRefs } from "pinia";
import { computed, nextTick, ref, useTemplateRef } from "vue";
import { useRoute } from "vue-router";

import BaseSaveButtonWithActionMenu from "@/components/common/buttons/BaseSaveButtonWithActionMenu.vue";
import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseStimmzettelErfassungCardContent from "@/components/dse/stimmzettelerfassung/baseComponents/BaseStimmzettelErfassungCardContent.vue";
import TheStimmzettelErfassungCancelConfirmationDialog from "@/components/dse/stimmzettelerfassung/dialogs/TheStimmzettelErfassungCancelConfirmationDialog.vue";
import { useCloneTools } from "@/composables/common/cloneTools.ts";
import { useStimmzettelerfassungDialogUtils } from "@/composables/dse/stimmzettelerfassung/stimmzettelerfassungDialogUtils.ts";
import { SAVE_CONTINUE } from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";

const isDialogVisible = ref(false);
const stimmzettelkennung = ref(0);

const properties = defineProps({
  wahlvorschlaege: {
    type: Array as PropType<Wahlvorschlag[]>,
    required: true,
  },
});

const route = useRoute();
const wahlID = route.params.wahlId as string;

const { currentUserTeamName } = storeToRefs(useUserStore());

const { stimmzettelManager } = useStimmzettelerfassungDialogUtils(
  computed(() => stimmzettelkennung.value),
  properties.wahlvorschlaege,
  wahlID
);

const emit = defineEmits<{
  cancel: [];
  confirmClose: [stimmzettel: PersistedStimmzettel];
  confirmNext: [stimmzettel: PersistedStimmzettel];
}>();

defineExpose({ closeDialog, focusCommandProcessingTextField, showDialog });

const confirmActions = {
  saveAndContinue: {
    title: SAVE_CONTINUE,
    action: () => onSavedClickedAndNext(),
  },
  saveAndClose: {
    title: "Speichern und Schließen",
    action: () => onSavedClickedAndClose(),
  },
};

const actions = Object.values(confirmActions);

const BASE_CONTENT_TEMPLATE_REF_NAME =
  "baseStimmzettelErfassungCardContent" as const;
const baseContentRef = useTemplateRef<
  InstanceType<typeof BaseStimmzettelErfassungCardContent>
>(BASE_CONTENT_TEMPLATE_REF_NAME);

const currentAction = ref(confirmActions.saveAndContinue);

const isCancelButtonDisabled = computed(
  () => stimmzettelManager.hasStimmzettelBeenEdited.value
);

const isSaveDisabled = computed(() => {
  if (
    stimmzettelManager.stimmzettelBeforeEdit.value !== null &&
    stimmzettelManager.bearbeitenDialogStimmzettelUtils.hasAnyValuesSet.value &&
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

const stimmzettelGueltigkeit = computed(
  () =>
    stimmzettelManager.bearbeitenDialogStimmzettelUtils.stimmzettel.value
      .gueltigkeit
);

const isCancelConfirmationDialogVisible = ref(false);

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
    isDialogVisible.value = false;
  } else {
    isCancelConfirmationDialogVisible.value = true;
  }
}

function onCancelConfirmationDialogCancelled() {
  isCancelConfirmationDialogVisible.value = false;
  focusCommandProcessingTextField();
}

function onCancelConfirmationDialogConfirmed() {
  isCancelConfirmationDialogVisible.value = false;
  emit("cancel");
}

function onSavedClickedAndClose() {
  emit("confirmClose", stimmzettelManager.getStimmzettelSnapshot());
}

function onSavedClickedAndNext() {
  emit("confirmNext", stimmzettelManager.getStimmzettelSnapshot());
  stimmzettelManager.startNewStimmzettel(currentUserTeamName.value);
  void focusCommandProcessingTextField();
}

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

function onLeerenClicked() {
  stimmzettelManager.startWithClearedStimmzettel();
  void focusCommandProcessingTextField();
}

function showDialog(
  stimmzettelToEditOrKennungOfNew: PersistedStimmzettel | number
) {
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
  focusCommandProcessingTextField();
}
</script>
