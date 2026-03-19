<template>
  <v-container>
    <v-card>
      <v-card-title>Zählen der Wahlscheine</v-card-title>
      <v-card-text>
        <v-form
          v-model="isWahlscheineFormValid"
          data-test="wahlscheineForm"
        >
          <div class="d-flex flex-wrap justify-start">
            <template
              v-for="wahlschein in wahlscheine"
              :key="wahlschein.bezirkUndWahlID.wahlID"
            >
              <base-number-input
                v-model="wahlschein.stimmabgabevermerke"
                class="mr-4"
                :label="
                  wahlenActions.getWahlNameOrBlankStringById(
                    wahlschein.bezirkUndWahlID.wahlID
                  )
                "
                :min-valid="1"
                :rules="[required]"
                max-width="300"
              />
            </template>
          </div>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <base-button-save
          :disabled="!isWahlscheineFormValid"
          :loading="isWahlscheineSaving"
          save-text="Speichern und Weiter"
          @click="onSaveClicked"
        />
      </v-card-actions>
    </v-card>
    <base-dialog-begruendung
      v-for="dialog in dialogs"
      :key="dialog.differenceBegruendung.wahlId"
      :visible="dialog.isVisible"
      :dialogtitle="`Abweichung zwischen der Anzahl der ${getStimmzettelTermForWahl(wahlenActions.getWahlOrUndefinedById(dialog.differenceBegruendung.wahlId))} und der Anzahl der ${getWahlscheineOrStimmabgabevermerkeTerm()}`"
      :is-save-disabled="!dialog.differenceBegruendung.isBegruendungValid"
      save-text="Speichern und Weiter"
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
import type { Ref } from "vue";

import { storeToRefs } from "pinia";
import { ref } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseDialogBegruendung from "@/components/common/dialogs/BaseDialogBegruendung.vue";
import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import { useRules } from "@/composables/common/rules.ts";
import { useTextFormatter } from "@/composables/common/textFormatter.ts";
import { useMultipleDifferenceDialogUtils } from "@/composables/ergebnismeldung/common/multipleDifferenceDialogUtils.ts";
import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";
import {
  MAX_LENGTH_FOR_TEXT_INPUT,
  MIN_LENGTH_FOR_BEGRUENDUNG,
} from "@/constants.ts";
import router from "@/plugins/router.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { useWahlscheineStore } from "@/stores/wahlscheineStore.ts";

const { wahlenActions } = useWahlenStore();
const { required, minLength, maxLength } = useRules();
const { wahlscheine, isWahlscheineSaving } = storeToRefs(useWahlscheineStore());
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

const isWahlscheineFormValid: Ref<null | boolean> = ref(null);

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
