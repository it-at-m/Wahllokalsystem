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
      <v-card-text
        style="min-height: 0"
        class="ga-3 d-flex"
      >
        <div
          class="d-flex flex-column"
          style="flex: 0 0 200px"
        >
          <the-eingabehistorie-card
            :change-history="changeHistory.changeHistoryInReverseOrder.value"
            class="d-flex flex-column"
          />
          <base-stimmzettel-zusammenfassung-card
            class="mt-2 d-flex flex-column"
            :listenstimmen="
              stimmzettelManager.managedStimmzettel
                .wahlvorschlaegeWithListenkreuz.value
            "
            :ungueltigestimmen="
              stimmzettelManager.managedStimmzettel.stimmenSummary.value
                .ungueltigeStimmen
            "
            :direktstimmen="
              stimmzettelManager.managedStimmzettel.stimmenSummary.value
                .einzelstimmen
            "
            :reststimmen="
              stimmzettelManager.managedStimmzettel.stimmenSummary.value
                .reststimmen
            "
            :streichungen="
              stimmzettelManager.managedStimmzettel.stimmenSummary.value
                .streichungen
            "
            :gueltigkeit="
              stimmzettelManager.managedStimmzettel
                .effectiveStimmzettelGueltigkeit.value
            "
          />
        </div>
        <div
          class="flex-1-1 d-flex flex-column"
          style="min-height: 0; min-width: 0"
        >
          <the-stimmzettel-command-processing-text-field
            class="flex-0-0"
            :stimmzettel-manager="stimmzettelManager"
            :disabled="isCommandInputFieldDisabled"
          />
          <div
            class="flex-1-1-0 d-flex"
            style="min-height: 0; min-width: 0"
          >
            <the-stimmzettel-content
              :active-wahlvorschlag-id="latestChangedWahlvorschlagId"
              :active-kandidat="latestChangedKandidat"
              :wahlvorschlaege="
                stimmzettelManager.managedStimmzettel.stimmzettel.value
                  .wahlvorschlaege
              "
              style="min-height: 0; overflow-y: auto; min-width: 0"
            />
          </div>
        </div>
        <div
          class="d-flex flex-column"
          style="flex: 0 0 300px"
        >
          <base-stimmzettel-sonderfaelle-card
            v-model:invalid-votes="
              stimmzettelManager.managedStimmzettel.stimmzettel.value
                .invalideVotes
            "
            v-model:beschlussfassung-valid="isBeschlussfassungValid"
            v-model:gueltigkeit="
              stimmzettelManager.managedStimmzettel.stimmzettel.value
                .gueltigkeit
            "
            v-model:wahlvorstand-beschlussvorschlag="
              stimmzettelManager.managedStimmzettel.stimmzettel.value
                .wahlvorstandBeschlussvorschlag
            "
            :deny-selection-of-stimmzettel-fehlt="
              stimmzettelManager.managedStimmzettel.hasAnyValuesSet.value
            "
            :deny-selection-of-stimmzettel-leer="
              stimmzettelManager.managedStimmzettel.hasAnyValuesSet.value
            "
            :team-id="currentUserTeamName"
            :system-beschlussgruende="
              stimmzettelManager.managedStimmzettel.systemErrors.value
            "
            :stimmzettelkennung="stimmzettel.stimmzettelkennung"
            :is-b-w-b="isBWB"
          />
        </div>
      </v-card-text>
      <v-card-actions>
        <base-text-button @click="onResetClicked"
          >Zurücksetzen</base-text-button
        >
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
import type { Stimmzettel } from "@/types/dse/persistedStimmzettel/Stimmzettel.ts";
import type { Kandidat } from "@/types/dse/stimmzettelerfassung/Kandidat.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";
import type { PropType } from "vue";

import { storeToRefs } from "pinia";
import { computed, ref, watch } from "vue";
import { useRoute } from "vue-router";

import BaseSaveButtonWithActionMenu from "@/components/common/buttons/BaseSaveButtonWithActionMenu.vue";
import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseStimmzettelSonderfaelleCard from "@/components/dse/stimmzettelerfassung/baseComponents/BaseStimmzettelSonderfaelleCard.vue";
import BaseStimmzettelZusammenfassungCard from "@/components/dse/stimmzettelerfassung/baseComponents/BaseStimmzettelZusammenfassungCard.vue";
import TheStimmzettelErfassungCancelConfirmationDialog from "@/components/dse/stimmzettelerfassung/dialogs/TheStimmzettelErfassungCancelConfirmationDialog.vue";
import TheEingabehistorieCard from "@/components/dse/stimmzettelerfassung/TheEingabehistorieCard.vue";
import TheStimmzettelCommandProcessingTextField from "@/components/dse/stimmzettelerfassung/TheStimmzettelCommandProcessingTextField.vue";
import TheStimmzettelContent from "@/components/dse/stimmzettelerfassung/TheStimmzettelContent.vue";
import { useStimmzettelerfassungDialogUtils } from "@/composables/dse/stimmzettelerfassung/stimmzettelerfassungDialogUtils.ts";
import { SAVE_CONTINUE } from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { StimmzettelGueltigkeitEnum } from "@/types/dse/persistedStimmzettel/StimmzettelGueltigkeitEnum.ts";

const isDialogVisibleModel = defineModel("modelValue", {
  type: Boolean,
  required: false,
});

const properties = defineProps({
  stimmzettel: {
    type: Object as PropType<Stimmzettel>,
    required: true,
  },
  wahlvorschlaege: {
    type: Array as PropType<Wahlvorschlag[]>,
    required: true,
  },
});

const emit = defineEmits<{
  cancel: [];
  confirmClose: [stimmzettel: Stimmzettel];
  confirmNext: [stimmzettel: Stimmzettel];
}>();

const actions = [
  {
    title: SAVE_CONTINUE,
    action: () => onSavedClickedAndNext(),
  },
  {
    title: "Speichern und schließen",
    action: () => onSavedClickedAndClose(),
  },
];

const currentAction = ref(actions[0]);

watch(
  () => isDialogVisibleModel.value,
  () => {
    if (isDialogVisibleModel.value) {
      currentAction.value = actions[0];
    }
  }
);

const route = useRoute();
const wahlID = route.params.wahlId as string;

const { stimmzettelManager } = useStimmzettelerfassungDialogUtils(
  properties.wahlvorschlaege,
  wahlID
);

const { currentUserTeamName, isBWB } = storeToRefs(useUserStore());

const isBeschlussfassungValid = ref(true);

const changeHistory = computed(
  () => stimmzettelManager.managedStimmzettel.changeHistory
);
const isCancelButtonDisabled = computed(
  () => stimmzettelManager.managedStimmzettel.hasAnyValuesSet.value
);
const isCommandInputFieldDisabled = computed(
  () =>
    stimmzettelGueltigkeit.value ===
      StimmzettelGueltigkeitEnum.BwbPseudoStimmzettelLeererUmschlag ||
    stimmzettelGueltigkeit.value === StimmzettelGueltigkeitEnum.Leer
);
const isSaveDisabled = computed(() => isBeschlussfassungValid.value === false);
const latestChangedWahlvorschlagId = computed<string | null>(
  () =>
    changeHistory.value.lastUsedWahlvorschlag?.value?.wahlvorschlagID ?? null
);
const latestChangedKandidat = computed<Kandidat | null>(
  () => changeHistory.value.lastUsedKandidat.value ?? null
);
const stimmzettelGueltigkeit = computed(
  () => stimmzettelManager.managedStimmzettel.stimmzettel.value.gueltigkeit
);

const isCancelConfirmationDialogVisible = ref(false);

function onCancelClicked() {
  isCancelConfirmationDialogVisible.value = true;
}

function onCancelConfirmationDialogCancelled() {
  isCancelConfirmationDialogVisible.value = false;
}

function onCancelConfirmationDialogConfirmed() {
  isCancelConfirmationDialogVisible.value = false;
  emit("cancel");
}

function onSavedClickedAndClose() {
  emit("confirmClose", properties.stimmzettel);
}

function onSavedClickedAndNext() {
  emit("confirmNext", properties.stimmzettel);
}

function onResetClicked() {
  stimmzettelManager.managedStimmzettel.resetStimmzettel();
}
</script>
