<template>
  <v-dialog
    v-model="isDialogVisibleModel"
    persistent
    fullscreen
  >
    <v-card
      class="h-screen"
      style="min-height: 0; max-width: 100%"
    >
      <v-card-title>
        Erfassung Stimmzettel Nummer {{ currentUserTeamName }}
        {{ stimmzettel.stimmzettelkennung }}
      </v-card-title>
      <base-stimmzettel-erfassung-card-content
        v-model="stimmzettelManager"
        :stimmzettel="stimmzettel"
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
        :stimmzettelkennung="stimmzettel.stimmzettelkennung"
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
import { computed, nextTick, ref, useTemplateRef, watch } from "vue";
import { useRoute } from "vue-router";

import BaseSaveButtonWithActionMenu from "@/components/common/buttons/BaseSaveButtonWithActionMenu.vue";
import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseStimmzettelErfassungCardContent from "@/components/dse/stimmzettelerfassung/baseComponents/BaseStimmzettelErfassungCardContent.vue";
import TheStimmzettelErfassungCancelConfirmationDialog from "@/components/dse/stimmzettelerfassung/dialogs/TheStimmzettelErfassungCancelConfirmationDialog.vue";
import TheStimmzettelCommandProcessingTextField from "@/components/dse/stimmzettelerfassung/TheStimmzettelCommandProcessingTextField.vue";
import { useStimmzettelerfassungDialogUtils } from "@/composables/dse/stimmzettelerfassung/stimmzettelerfassungDialogUtils.ts";
import { SAVE_CONTINUE } from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";

const isDialogVisibleModel = defineModel("modelValue", {
  type: Boolean,
  required: false,
});

const properties = defineProps({
  stimmzettel: {
    type: Object as PropType<PersistedStimmzettel>,
    required: true,
  },
  wahlvorschlaege: {
    type: Array as PropType<Wahlvorschlag[]>,
    required: true,
  },
});

const route = useRoute();
const wahlID = route.params.wahlId as string;

const { currentUserTeamName } = storeToRefs(useUserStore());

const { stimmzettelManager } = useStimmzettelerfassungDialogUtils(
  computed(() => properties.stimmzettel.stimmzettelkennung),
  properties.wahlvorschlaege,
  wahlID,
  currentUserTeamName.value
);

const emit = defineEmits<{
  cancel: [];
  confirmClose: [stimmzettel: PersistedStimmzettel];
  confirmNext: [stimmzettel: PersistedStimmzettel];
}>();

defineExpose({ focusCommandProcessingTextField });

const actions = [
  {
    title: SAVE_CONTINUE,
    action: () => onSavedClickedAndNext(),
  },
  {
    title: "Speichern und Schließen",
    action: () => onSavedClickedAndClose(),
  },
];

const COMMAND_PROCESSING_TEXT_FIELD_TEMPLATE_REF_NAME =
  "commandProcessingTextField";
const commandProcessingTextField = useTemplateRef<
  InstanceType<typeof TheStimmzettelCommandProcessingTextField>
>(COMMAND_PROCESSING_TEXT_FIELD_TEMPLATE_REF_NAME);

const currentAction = ref(actions[0]);

watch(
  () => isDialogVisibleModel.value,
  () => {
    if (isDialogVisibleModel.value) {
      stimmzettelManager.setActiveStimmzettelWhenEditing(
        properties.stimmzettel
      );

      currentAction.value = stimmzettelManager.bearbeitenDialogStimmzettelUtils
        .hasAnyValuesSet.value
        ? actions[1]
        : actions[0];
    }
    void focusCommandProcessingTextField();
  },
  { immediate: true }
);

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

async function focusCommandProcessingTextField() {
  await nextTick();
  commandProcessingTextField.value?.focus();
}

function onCancelClicked() {
  if (
    stimmzettelManager.bearbeitenDialogStimmzettelUtils.hasAnyValuesSet.value
  ) {
    isDialogVisibleModel.value = false;
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
  stimmzettelManager.startNewStimmzettel();
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
</script>
