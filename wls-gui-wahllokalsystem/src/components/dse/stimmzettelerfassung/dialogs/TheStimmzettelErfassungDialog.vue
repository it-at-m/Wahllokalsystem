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
        :is-readonly="isReadonly"
        :stimmzettelkennung="stimmzettelkennung"
        :wahlvorschlaege="wahlvorschlaege"
        :stimmzettel-gueltigkeit="stimmzettelGueltigkeit"
      />
      <v-card-actions v-if="isReadonly">
        <v-spacer />
        <base-text-button
          :active="true"
          @click="closeDialog"
        >
          Schließen
        </base-text-button>
      </v-card-actions>
      <v-card-actions v-else>
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
        <v-tooltip
          :disabled="
            stimmzettelGueltigkeit !==
            StimmzettelGueltigkeitEnum.BeschlussAusstehend
          "
          location="top"
          content-class="noPadding"
        >
          <template #default>
            <base-feedback-card
              title="Beschluss notwendig"
              :type="InputFeedbackTypeEnum.information"
            >
              Bitte legen Sie den Stimmzettel für die Beschlussfassung separat
              ab.
            </base-feedback-card>
          </template>
          <template #activator="{ props }">
            <base-save-button-with-action-menu
              :tooltip-activator-props="props"
              :disabled="isSaveDisabled"
              :model-value="currentAction"
              :actions="actions"
            />
          </template>
        </v-tooltip>
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

import BaseSaveButtonWithActionMenu from "@/components/common/buttons/BaseSaveButtonWithActionMenu.vue";
import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseFeedbackCard from "@/components/common/cards/BaseFeedbackCard.vue";
import BaseStimmzettelErfassungCardContent from "@/components/dse/stimmzettelerfassung/baseComponents/BaseStimmzettelErfassungCardContent.vue";
import TheStimmzettelErfassungCancelConfirmationDialog from "@/components/dse/stimmzettelerfassung/dialogs/TheStimmzettelErfassungCancelConfirmationDialog.vue";
import { useStimmzettelerfassungDialogUtils } from "@/composables/dse/stimmzettelerfassung/stimmzettelerfassungDialogUtils.ts";
import { InputFeedbackTypeEnum } from "@/types/common/InputFeedbackTypeEnum.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";

const properties = defineProps({
  wahlvorschlaege: {
    type: Array as PropType<Wahlvorschlag[]>,
    required: true,
  },
});

const emit = defineEmits<{
  cancel: [];
  confirmClose: [stimmzettel: PersistedStimmzettel];
  confirmNext: [stimmzettel: PersistedStimmzettel];
}>();

const {
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
} = useStimmzettelerfassungDialogUtils(properties.wahlvorschlaege, emit);

defineExpose({ closeDialog, focusCommandProcessingTextField, showDialog });
</script>
