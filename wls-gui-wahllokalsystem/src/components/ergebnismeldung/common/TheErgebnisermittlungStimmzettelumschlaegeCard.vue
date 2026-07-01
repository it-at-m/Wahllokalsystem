<template>
  <div>
    <v-card v-if="wahl">
      <v-card-title>{{ title }}</v-card-title>
      <v-card-text class="pb-0 pt-2 mr-4">
        <v-form v-model="anzahlStimmzettelValidForm">
          <base-time-input
            v-if="isBWB"
            v-model="wahl.stimmzettelumschlaege.urneneroeffnungsUhrzeit"
            :rules="[
              timeNotInFuture,
              timeGreaterOrEqual(fruehesteSchliessungsuhrzeit),
            ]"
            label="Uhrzeit der Öffnung der Wahlurne"
            min-width="20rem"
          />
          <base-number-input
            v-model="wahl.stimmzettelumschlaege.anzahlWaehler"
            :rules="[required]"
            min-width="20rem"
            :label="`Anzahl der ${getStimmzettelTermForWahl(wahl)}`"
          />
        </v-form>
      </v-card-text>
      <v-card-actions>
        <base-wls-button-save
          :loading="stimmzettelumschlaegeState.isStimmzettelumschlaegeSaving"
          :disabled="isMBWAuszaehlungDone || isSaveButtonDisabled"
          :save-text="SAVE_CONTINUE"
          @click="onSaveClicked"
        />
      </v-card-actions>
    </v-card>
    <base-dialog-begruendung
      v-if="dialog"
      :visible="dialog.isVisible"
      :dialogtitle="`Abweichung zwischen der Anzahl der ${getStimmzettelTermForWahl(wahl)} und der Anzahl der ${getWahlscheineOrStimmabgabevermerkeTerm()}`"
      :is-save-disabled="!dialog.differenceBegruendung.isBegruendungValid"
      :save-text="SAVE_CONTINUE"
      @cancel="dialog.isVisible = false"
      @confirm="onConfirmClicked"
    >
      <div class="font-weight-bold mb-3">
        {{ wahlenActions.getWahlNameOrBlankStringById(props.wahlId) }}
      </div>
      <div class="mb-3">
        {{ getDialogContent() }}
      </div>
      <v-textarea
        v-model="dialog.differenceBegruendung.begruendung"
        :rules="[
          minLength(MIN_LENGTH_FOR_BEGRUENDUNG),
          maxLength(MAX_LENGTH_FOR_TEXT_INPUT),
        ]"
        rows="1"
        label="Bitte begründen Sie hier die Abweichung"
        auto-grow
        autofocus
        persistent-counter
        :counter="MAX_LENGTH_FOR_TEXT_INPUT"
        data-test="basedialogbegruendung-textarea"
        @update:model-value="
          updateValidationStateForBegruendung(dialog.differenceBegruendung)
        "
      />
    </base-dialog-begruendung>
  </div>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed, ref } from "vue";

import BaseWlsButtonSave from "@/components/common/buttons/BaseWlsButtonSave.vue";
import BaseDialogBegruendung from "@/components/common/dialogs/BaseDialogBegruendung.vue";
import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import BaseTimeInput from "@/components/common/inputs/BaseTimeInput.vue";
import { useRules } from "@/composables/common/rules.ts";
import { useTextFormatter } from "@/composables/common/textFormatter.ts";
import { useSingleDifferenceDialogUtils } from "@/composables/ergebnismeldung/common/singleDifferenceDialogUtils.ts";
import { useNavigationService } from "@/composables/navigation/navigationService.ts";
import {
  MAX_LENGTH_FOR_TEXT_INPUT,
  MIN_LENGTH_FOR_BEGRUENDUNG,
  SAVE_CONTINUE,
} from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { useWahlvorstandStore } from "@/stores/wahlvorstandStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { MbwStepsEnum } from "@/types/navigation/MbwStepsEnum.ts";

const { required, timeGreaterOrEqual, timeNotInFuture, minLength, maxLength } =
  useRules();

const props = defineProps<{
  wahlId: string;
  wahlbezirkId: string;
  title: string;
}>();

const { wahlenActions } = useWahlenStore();
const { stimmzettelumschlaegeState } = storeToRefs(useWahlenStore());
const { fruehesteSchliessungsuhrzeit } = storeToRefs(useInfomanagementStore());
const { getStimmzettelTermForWahl, getWahlscheineOrStimmabgabevermerkeTerm } =
  useTextFormatter();
const {
  dialog,
  isWahlscheineUnequalToStimmzettel,
  checkForDifferencesAndOpenDialogOrSaveStimmzettelumschlaege,
  saveBegruendungAndStimmzettelumschlaege,
  updateValidationStateForBegruendung,
  getDialogContent,
} = useSingleDifferenceDialogUtils(props.wahlId, props.wahlbezirkId);
const { getNextRoute } = useNavigationService();
const { setStepDone, isElectionFinished } = useWorkflowStore();
const { resetAllAnwesenheiten } = useWahlvorstandStore();
const { isBWB } = storeToRefs(useUserStore());

const wahl = computed(() => wahlenActions.getWahlOrUndefinedById(props.wahlId));

const anzahlStimmzettelValidForm = ref<null | boolean>(null);

const isMBWAuszaehlungDone = computed(() =>
  isElectionFinished(props.wahlId, props.wahlbezirkId)
);
const isSaveButtonDisabled = computed(() => {
  return !anzahlStimmzettelValidForm.value;
});

async function onSaveClicked() {
  await checkForDifferencesAndOpenDialogOrSaveStimmzettelumschlaege();
  if (!isWahlscheineUnequalToStimmzettel.value) {
    await continueInWorkflow();
  }
}

async function onConfirmClicked() {
  await saveBegruendungAndStimmzettelumschlaege();
  await continueInWorkflow();
}

async function continueInWorkflow() {
  setStepDone(
    props.wahlId,
    props.wahlbezirkId,
    MbwStepsEnum.MBW_AUSZAEHLUNG_STIMMZETTEL
  );
  if (isBWB.value) {
    resetAllAnwesenheiten();
  }
  await router.push(getNextRoute());
}
</script>
