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
              stimmzettelManager.bearbeitenDialogStimmzettelUtils
                .wahlvorschlaegeWithListenkreuz.value
            "
            :ungueltigestimmen="
              stimmzettelManager.bearbeitenDialogStimmzettelUtils.stimmenSummary
                .value.ungueltigeStimmen
            "
            :direktstimmen="
              stimmzettelManager.bearbeitenDialogStimmzettelUtils.stimmenSummary
                .value.einzelstimmen
            "
            :reststimmen="
              stimmzettelManager.bearbeitenDialogStimmzettelUtils.stimmenSummary
                .value.reststimmen
            "
            :streichungen="
              stimmzettelManager.bearbeitenDialogStimmzettelUtils.stimmenSummary
                .value.streichungen
            "
            :gueltigkeit="'VALID'"
          />
        </div>
        <div
          class="flex-1-1 d-flex flex-column"
          style="min-height: 0; min-width: 0"
        >
          <the-stimmzettel-command-processing-text-field
            class="flex-0-0"
            :stimmzettel-manager="stimmzettelManager"
          />
          <div
            class="flex-1-1-0 d-flex"
            style="min-height: 0; min-width: 0"
          >
            <the-stimmzettel-content
              :active-wahlvorschlag-id="latestChangedWahlvorschlagId"
              :active-kandidat="latestChangedKandidat"
              :wahlvorschlaege="
                stimmzettelManager.bearbeitenDialogStimmzettelUtils.stimmzettel
                  .value.wahlvorschlaege
              "
              style="min-height: 0; overflow-y: auto; min-width: 0"
            />
          </div>
        </div>
        <div
          class="d-flex flex-column"
          style="flex: 0 0 300px"
        >
          <the-eingabehistorie-card
            :change-history="changeHistory.changeHistoryInReverseOrder.value"
            class="d-flex flex-column"
          />
          <base-stimmzettel-zusammenfassung-card
            class="mt-2 d-flex flex-column"
            :listenstimmen="
              stimmzettelManager.bearbeitenDialogStimmzettelUtils
                .wahlvorschlaegeWithListenkreuz.value
            "
            :ungueltigestimmen="
              stimmzettelManager.bearbeitenDialogStimmzettelUtils.stimmenSummary
                .value.ungueltigeStimmen
            "
            :direktstimmen="
              stimmzettelManager.bearbeitenDialogStimmzettelUtils.stimmenSummary
                .value.einzelstimmen
            "
            :reststimmen="
              stimmzettelManager.bearbeitenDialogStimmzettelUtils.stimmenSummary
                .value.reststimmen
            "
            :streichungen="
              stimmzettelManager.bearbeitenDialogStimmzettelUtils.stimmenSummary
                .value.streichungen
            "
            :gueltigkeit="'VALID'"
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
import BaseStimmzettelZusammenfassungCard from "@/components/dse/stimmzettelerfassung/baseComponents/BaseStimmzettelZusammenfassungCard.vue";
import TheStimmzettelErfassungCancelConfirmationDialog from "@/components/dse/stimmzettelerfassung/dialogs/TheStimmzettelErfassungCancelConfirmationDialog.vue";
import TheEingabehistorieCard from "@/components/dse/stimmzettelerfassung/TheEingabehistorieCard.vue";
import TheStimmzettelCommandProcessingTextField from "@/components/dse/stimmzettelerfassung/TheStimmzettelCommandProcessingTextField.vue";
import TheStimmzettelContent from "@/components/dse/stimmzettelerfassung/TheStimmzettelContent.vue";
import { useStimmzettelerfassungDialogUtils } from "@/composables/dse/stimmzettelerfassung/stimmzettelerfassungDialogUtils.ts";
import { SAVE_CONTINUE } from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";

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
  confirmClose: [stimmzettel: Stimmzettel];
  confirmNext: [stimmzettel: Stimmzettel];
}>();

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

const currentAction = ref(actions[0]);

watch(
  () => isDialogVisibleModel.value,
  () => {
    if (isDialogVisibleModel.value) {
      currentAction.value = actions[0];
      stimmzettelManager.bearbeitenDialogStimmzettelUtils.resetStimmzettel();
      stimmzettelManager.setActiveStimmzettelWhenEditing(
        properties.stimmzettel
      );
    }
  },
  { immediate: true }
);

const changeHistory = computed(
  () => stimmzettelManager.bearbeitenDialogStimmzettelUtils.changeHistory
);
const isCancelButtonDisabled = computed(
  () => stimmzettelManager.hasStimmzettelBeenEdited.value
);
const latestChangedWahlvorschlagId = computed<string | null>(
  () =>
    changeHistory.value.lastUsedWahlvorschlag?.value?.wahlvorschlagID ?? null
);
const latestChangedKandidat = computed<Kandidat | null>(
  () => changeHistory.value.lastUsedKandidat.value ?? null
);

const isCancelConfirmationDialogVisible = ref(false);

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
}

function onResetClicked() {
  if (
    stimmzettelManager.hasStimmzettelBeenEdited.value &&
    stimmzettelManager.stimmzettelBeforeEdit.value !== null
  ) {
    stimmzettelManager.bearbeitenDialogStimmzettelUtils.resetStimmzettel(
      stimmzettelManager.stimmzettelBeforeEdit.value
    );
  } else {
    stimmzettelManager.bearbeitenDialogStimmzettelUtils.resetStimmzettel();
  }
}
</script>
