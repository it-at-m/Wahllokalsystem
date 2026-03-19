<template>
  <div>
    <v-card-title>Wahlschein prüfen</v-card-title>
    <v-card-text class="pb-0">
      <v-form
        ref="wahlscheinValidationForm"
        v-model="isFormValid"
      >
        <base-number-input
          :model-value="wahlscheinnummer"
          :min-valid="1"
          :max-valid="9999999"
          :rules="[required]"
          label="Wahlscheinnummer"
          max-width="300"
          data-test="number-input-wahlscheinnummer"
          @update:model-value="onWahlscheinnummerChanged"
        />

        <base-feedback-card
          v-if="feedbackWahlscheinIsGueltigIsVisible"
          title="Wahlschein ist gültig"
          :type="InputFeedbackTypeEnum.success"
        >
          <ul>
            <li>
              Gleichen Sie die Daten der Person mit einem Ausweisdokument ab.
            </li>
            <li>
              Nehmen Sie den Wahlschein ein und sammeln ihn hinten im
              Wählerverzeichnis.
            </li>
            <li>
              Lassen Sie den Stimmzettel einwerfen und vermerken dies mit einem
              Haken auf dem Wahlschein. Es erfolgt kein Eintrag im
              Wählerverzeichnis.
            </li>
          </ul>
        </base-feedback-card>

        <base-feedback-card
          v-if="feedbackWahlscheinIsUngueltigIsVisible"
          :title="titleFeedbackWahlscheinUngueltig"
          :type="InputFeedbackTypeEnum.error"
        >
          <ul>
            <li>Die Person darf mit diesem Wahlschein keine Stimme abgeben!</li>
            <li>Behalten Sie den Wahlschein ein.</li>
            <li>Fassen Sie einen Beschluss über die Zurückweisung.</li>
            <li>Erfassen Sie dies als besonderes Ereignis:</li>
          </ul>
          <template #additionalFeedback>
            <v-row>
              <v-col cols="4">
                {{ ereignisBeschreibungWahlscheinUnguelttig }}
              </v-col>
              <v-col>
                <v-form v-model="isAbstimmungsergebnisFormValid">
                  <v-textarea
                    v-model="abstimmungsergebnis"
                    label="Abstimmungsergebnis"
                    width="350"
                    :rules="[
                      required,
                      minLength(0),
                      maxLength(maxLengthForAbstimmungsergebnis),
                    ]"
                    persistent-counter
                    :counter="maxLengthForAbstimmungsergebnis"
                    rows="1"
                    auto-grow
                    data-test="text-input-abstimmungsergebnis"
                  />
                </v-form>
              </v-col>
              <v-col>
                <base-button-save
                  class="mt-2 ml-5"
                  save-text="Beschluss speichern"
                  :disabled="!isAbstimmungsergebnisFormValid"
                  @click="onSaveAbstimmungsergebnisClicked"
                />
              </v-col>
              <v-spacer />
            </v-row>
          </template>
        </base-feedback-card>
        <v-img
          position="left"
          style="height: 40mm"
          class="mt-2"
          :src="wahlscheinExampleImage"
        />

        <base-feedback-card
          v-if="feedbackLoadingFailedIsVisible"
          title="Liste ungültiger Wahlscheine nicht verfügbar"
          :type="InputFeedbackTypeEnum.error"
          >Die ungültigen Wahlscheine konnten nicht geladen
          werden.</base-feedback-card
        >
        <base-feedback-card
          v-if="feedbackNoDataAvailableIsVisible"
          title="Liste ungültiger Wahlscheine nicht verfügbar"
          :type="InputFeedbackTypeEnum.error"
          >Der Inhalt der Datei mit den ungültigen Wahlscheinen ist
          leer.</base-feedback-card
        >
      </v-form>
    </v-card-text>
    <v-card-actions>
      <base-text-button
        active
        :disabled="isSearchButtonDisabled"
        data-test="button-search"
        @click="onSearchClicked"
        >{{ searchButtonLabel }}</base-text-button
      >
      <base-button-refresh
        :loading="ungueltigeWahlscheineState.ungueltigeWahlscheineIsLoading"
        @click="onRefreshClicked"
      />
    </v-card-actions>
  </div>
</template>

<script setup lang="ts">
import type { UngueltigerWahlschein } from "@/types/wahlbezirk/UngueltigerWahlschein.ts";
import type { ShallowRef } from "vue";
import type { VForm } from "vuetify/components";

import { storeToRefs } from "pinia";
import { computed, ref, useTemplateRef } from "vue";

import wahlscheinExampleImage from "@/assets/previewWahlscheinnummerOnWahlschein.png";
import BaseButtonRefresh from "@/components/common/buttons/BaseButtonRefresh.vue";
import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseTextButton from "@/components/common/buttons/BaseTextButton.vue";
import BaseFeedbackCard from "@/components/common/cards/BaseFeedbackCard.vue";
import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import { useRules } from "@/composables/common/rules.ts";
import { MAX_LENGTH_FOR_TEXT_INPUT } from "@/constants.ts";
import { useEreignisStore } from "@/stores/ereignisStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { InputFeedbackTypeEnum } from "@/types/common/InputFeedbackTypeEnum.ts";

const { required, maxLength, minLength } = useRules();
const { addEreignis, sendEreignisse } = useEreignisStore();

const isFormValid = ref<boolean | null>(null);
const isAbstimmungsergebnisFormValid = ref<boolean | null>(null);

const isSearchButtonDisabled = computed(() => !isFormValid.value);
const wahlscheinValidationForm = useTemplateRef(
  "wahlscheinValidationForm"
) as Readonly<ShallowRef<InstanceType<typeof VForm>>>;

const { ungueltigeWahlscheineActions } = useWahlbezirkStore();
const { ungueltigeWahlscheineState, ungueltigeWahlscheineGetter } =
  storeToRefs(useWahlbezirkStore());

const wahlscheinnummer = ref<null | number>(null);
//null - no hit on search
//undefined - not search yet, or search was reset
//value - found something while searching
const ungueltigerWahlschein = ref<null | undefined | UngueltigerWahlschein>(
  undefined
);
const abstimmungsergebnis = ref<string | undefined>(undefined);

const maxLengthForAbstimmungsergebnis = computed(
  () =>
    MAX_LENGTH_FOR_TEXT_INPUT -
    ereignisBeschreibungWahlscheinUnguelttig.value.length
);

const feedbackNoDataAvailableIsVisible = computed(
  () =>
    ungueltigeWahlscheineGetter.value.ungueltigeWahlscheineIsEmpty &&
    !ungueltigeWahlscheineState.value.ungueltigeWahlscheineLoadingFailed &&
    !ungueltigeWahlscheineState.value.ungueltigeWahlscheineIsLoading
);
const feedbackLoadingFailedIsVisible = computed(
  () =>
    ungueltigeWahlscheineState.value.ungueltigeWahlscheineLoadingFailed &&
    !ungueltigeWahlscheineState.value.ungueltigeWahlscheineIsLoading
);
const feedbackWahlscheinIsGueltigIsVisible = computed(
  () => ungueltigerWahlschein.value === null
);
const feedbackWahlscheinIsUngueltigIsVisible = computed(
  () => ungueltigerWahlschein.value
);
const searchButtonLabel = computed(() =>
  ungueltigerWahlschein.value === undefined ? "Suchen" : "Suche zurücksetzen"
);
const titleFeedbackWahlscheinUngueltig = computed(() => {
  return `Wahlschein ${ungueltigerWahlschein.value?.wahlscheinnummer ?? ""} für ${ungueltigerWahlschein.value?.vorname} ${ungueltigerWahlschein.value?.familienname} ist ungültig`;
});
const ereignisBeschreibungWahlscheinUnguelttig = computed(() => {
  return `${titleFeedbackWahlscheinUngueltig.value}. Die Person wurde zurückgewiesen. Abstimmungsergebnis: `;
});

function onRefreshClicked() {
  ungueltigeWahlscheineActions.loadUngueltigeWahlscheine();
}

function onSearchClicked() {
  if (ungueltigerWahlschein.value !== undefined) {
    resetUngueltigerWahlschein();
    wahlscheinValidationForm.value.reset();
  } else if (wahlscheinnummer.value !== null) {
    ungueltigerWahlschein.value =
      ungueltigeWahlscheineActions.getUngueltigerWahlscheinByWahlscheinnummer(
        `${wahlscheinnummer.value}`
      );
  }
}

async function onSaveAbstimmungsergebnisClicked() {
  addEreignis({
    uhrzeit: new Date(),
    beschreibung: `${ereignisBeschreibungWahlscheinUnguelttig.value}${abstimmungsergebnis.value}`,
  });
  await sendEreignisse();

  abstimmungsergebnis.value = undefined;
  isAbstimmungsergebnisFormValid.value = false;
}

function onWahlscheinnummerChanged(newValue: number | null | undefined) {
  if (newValue !== undefined) {
    wahlscheinnummer.value = newValue;
  } else {
    wahlscheinnummer.value = null;
  }
  resetUngueltigerWahlschein();
}

function resetUngueltigerWahlschein() {
  ungueltigerWahlschein.value = undefined;
  abstimmungsergebnis.value = undefined;
}
</script>
