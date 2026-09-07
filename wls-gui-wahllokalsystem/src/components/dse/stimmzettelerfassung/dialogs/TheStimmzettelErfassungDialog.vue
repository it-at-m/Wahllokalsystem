<template>
  <v-dialog
    v-model="isDialogVisibleModel"
    persistent
    fullscreen
  >
    <v-card>
      <v-card-title>
        Erfassung Stimmzettel Nummer {{ currentUserTeamName }}
        {{ stimmzettel.stimmzettelkennung }}
      </v-card-title>
      <v-card-text>
        <v-row>
          <v-col cols="2">
            <the-eingabehistorie-card
              :change-history="changeHistory.changeHistoryInReverseOrder.value"
            />
            <base-stimmzettel-zusammenfassung-card
              class="mt-2"
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
              :gueltigkeit="'VALID'"
            />
          </v-col>
          <v-col cols="10">
            <the-stimmzettel-command-processing-text-field
              :stimmzettel-manager="stimmzettelManager"
            />
            <the-stimmzettel-content
              :active-wahlvorschlag-id="latestChangedWahlvorschlagId"
              :active-kandidat="latestChangedKandidat"
              :wahlvorschlaege="
                stimmzettelManager.managedStimmzettel.stimmzettel.value
                  .wahlvorschlaege
              "
            />
          </v-col>
        </v-row>
      </v-card-text>
      <v-card-actions>
        <base-text-button @click="onResetClicked"
          >Zurücksetzen</base-text-button
        >
        <v-spacer />
        <base-text-button @click="onCancelClicked">Abbrechen</base-text-button>
        <base-save-button-with-action-menu
          :model-value="currentAction"
          :actions="actions"
        />
      </v-card-actions>
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

const { currentUserTeamName } = storeToRefs(useUserStore());

const changeHistory = computed(
  () => stimmzettelManager.managedStimmzettel.changeHistory
);
const latestChangedWahlvorschlagId = computed<string | null>(
  () =>
    changeHistory.value.lastUsedWahlvorschlag?.value?.wahlvorschlagID ?? null
);
const latestChangedKandidat = computed<Kandidat | null>(
  () => changeHistory.value.lastUsedKandidat.value ?? null
);

function onCancelClicked() {
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
