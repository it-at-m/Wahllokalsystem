<template>
  <v-container>
    <v-card>
      <v-form v-model="stimmabgabevermerkeModel">
        <the-u-w-b-stimmabgabevermerke-erfassen-card />
        <the-u-w-b-stimmabgabevermerke-eingenommene-wahlscheine-card />
        <the-u-w-b-stimmabgabevermerke-darstellung-summe-card />
      </v-form>
      <v-card-actions>
        <base-button-save
          :disabled="!stimmabgabevermerkeModel"
          :loading="isStimmabgabevermerkeSaving"
          :save-text="SAVE_CONTINUE"
          @click="onSaveClicked"
        /> </v-card-actions
    ></v-card>
    <base-dialog-begruendung
      v-for="dialog in dialogs"
      :key="dialog.differenceBegruendung.wahlId"
      :visible="dialog.isVisible"
      :dialogtitle="`Abweichung zwischen der Anzahl der ${getStimmzettelTermForWahl(wahlenActions.getWahlOrUndefinedById(dialog.differenceBegruendung.wahlId))} und der Anzahl der ${getWahlscheineOrStimmabgabevermerkeTerm()}`"
      :is-save-disabled="!dialog.differenceBegruendung.isBegruendungValid"
      :save-text="SAVE_CONTINUE"
      @cancel="dialog.isVisible = false"
      @confirm="onConfirmClicked(dialog)"
    >
      <div class="font-weight-bold mb-3">
        {{
          wahlenActions.getWahlNameOrBlankStringById(
            dialog.differenceBegruendung.wahlId
          )
        }}
      </div>
      <div class="mb-3">
        {{ getDialogContent(dialog.differenceBegruendung) }}
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
  </v-container>
</template>
<script setup lang="ts">
import type { DifferenceDialogItem } from "@/types/ergebnismeldung/common/DifferenceDialogItem.ts";

import { storeToRefs } from "pinia";
import { ref } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseDialogBegruendung from "@/components/common/dialogs/BaseDialogBegruendung.vue";
import TheUWBStimmabgabevermerkeDarstellungSummeCard from "@/components/stimmabgabevermerke/TheUWBStimmabgabevermerkeDarstellungSummeCard.vue";
import TheUWBStimmabgabevermerkeEingenommeneWahlscheineCard from "@/components/stimmabgabevermerke/TheUWBStimmabgabevermerkeEingenommeneWahlscheineCard.vue";
import TheUWBStimmabgabevermerkeErfassenCard from "@/components/stimmabgabevermerke/TheUWBStimmabgabevermerkeErfassenCard.vue";
import { useRules } from "@/composables/common/rules.ts";
import { useTextFormatter } from "@/composables/common/textFormatter.ts";
import { useMultipleDifferenceDialogUtils } from "@/composables/ergebnismeldung/common/multipleDifferenceDialogUtils.ts";
import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";
import {
  MAX_LENGTH_FOR_TEXT_INPUT,
  MIN_LENGTH_FOR_BEGRUENDUNG,
  SAVE_CONTINUE,
} from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useStimmabgabevermerkeStore } from "@/stores/stimmabgabevermerkeStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const { minLength, maxLength } = useRules();
const { wahlenActions } = useWahlenStore();
const { isStimmabgabevermerkeSaving } = storeToRefs(
  useStimmabgabevermerkeStore()
);
const { getStimmzettelTermForWahl, getWahlscheineOrStimmabgabevermerkeTerm } =
  useTextFormatter();
const {
  dialogs,
  checkForDifferencesAndAddDialogsOrSaveStimmabgabevermerkeWahlscheine,
  saveBegruendungAndStimmabgabevermerkeWahlscheine,
  updateValidationStateForBegruendung,
  getDialogContent,
} = useMultipleDifferenceDialogUtils();
const { getNextRoute } = useNavigationUtils();

const stimmabgabevermerkeModel = ref(false);

async function onSaveClicked() {
  await checkForDifferencesAndAddDialogsOrSaveStimmabgabevermerkeWahlscheine();
  if (dialogs.value.length === 0) {
    await router.push(getNextRoute());
  }
}

async function onConfirmClicked(dialog: DifferenceDialogItem) {
  await saveBegruendungAndStimmabgabevermerkeWahlscheine(dialog);
  await router.push(getNextRoute());
}
</script>
