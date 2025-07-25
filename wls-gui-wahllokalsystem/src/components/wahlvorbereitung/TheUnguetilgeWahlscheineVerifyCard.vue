<template>
  <v-card>
    <v-card-title>Wahlschein prüfen</v-card-title>
    <v-card-text class="pb-0">
      <v-form
        ref="wahlscheinValidationForm"
        v-model="isFormValid"
      >
        <v-number-input
          :model-value="wahlscheinnummer"
          :rules="[REQUIRED, MIN_NUMBER(1), MAX_NUMBER(9999999)]"
          label="Wahlscheinnummer"
          max-width="300"
          data-test="number-input-wahlscheinnummer"
          @update:model-value="onWahlscheinnummerChanged"
        />

        <base-input-feedback-card
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
        </base-input-feedback-card>

        <base-input-feedback-card
          v-if="feedbackWahlscheinIsUngueltigIsVisible"
          :title="titleFeedbackWahlscheinUngueltig"
          :type="InputFeedbackTypeEnum.error"
        >
          <div>
            <ul>
              <li>
                Die Person darf mit diesem Wahlschein keine Stimme abgeben!
              </li>
              <li>Behalten Sie den Wahlschein ein.</li>
              <li>
                Fassen Sie einen Beschluss über die Zurückweisung der wählenden
                Person.
              </li>
              <li>
                Erfassen Sie dies als besonderes Vorkommnis unter dem Punkt
                "Störungen".
              </li>
            </ul>
          </div>
        </base-input-feedback-card>
        <v-img
          position="left"
          style="height: 40mm"
          :src="wahlscheinExampleImage"
        />

        <base-input-feedback-card
          v-if="feedbackLoadingFailedIsVisible"
          title="Liste ungültiger Wahlscheine nicht verfügbar"
          :type="InputFeedbackTypeEnum.error"
          >Die ungültigen Wahlscheine konnten nicht geladen
          werden.</base-input-feedback-card
        >
        <base-input-feedback-card
          v-if="feedbackNoDataAvailableIsVisible"
          title="Liste ungültiger Wahlscheine nicht verfügbar"
          :type="InputFeedbackTypeEnum.error"
          >Der Inhalt der Datei mit den ungültigen Wahlscheinen ist
          leer.</base-input-feedback-card
        >
      </v-form>
    </v-card-text>
    <v-card-actions>
      <v-btn
        active
        :disabled="isSearchButtonDisabled"
        data-test="button-search"
        @click="onSearchClicked"
        >{{ searchButtonLabel }}</v-btn
      >
      <base-button-refresh
        :loading="ungueltigeWahlscheineIsLoading"
        @click="onRefreshClicked"
      />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import type { UngueltigerWahlschein } from "@/types/wahlbezirk/UngueltigerWahlschein.ts";
import type { ShallowRef } from "vue";

import { storeToRefs } from "pinia";
import { computed, ref, useTemplateRef } from "vue";
import {
  VBtn,
  VCard,
  VCardActions,
  VCardText,
  VCardTitle,
  VForm,
  VImg,
  VNumberInput,
} from "vuetify/components";

import wahlscheinExampleImage from "@/assets/previewWahlscheinnummerOnWahlschein.png";
import BaseButtonRefresh from "@/components/common/buttons/BaseButtonRefresh.vue";
import BaseInputFeedbackCard from "@/components/common/cards/BaseInputFeedbackCard.vue";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { InputFeedbackTypeEnum } from "@/types/common/InputFeedbackTypeEnum.ts";
import { MAX_NUMBER, MIN_NUMBER, REQUIRED } from "@/util/rules.ts";

const isFormValid = ref<boolean | null>(null);
const isSearchButtonDisabled = computed(() => !isFormValid.value)
const wahlscheinValidationForm = useTemplateRef(
  "wahlscheinValidationForm"
) as Readonly<ShallowRef<InstanceType<typeof VForm>>>;

const {
  getUngueltigerWahlscheinByWahlscheinnummer,
  loadUngueltigeWahlscheine,
} = useWahlbezirkStore();
const {
  ungueltigeWahlscheineIsLoading,
  ungueltigeWahlscheineIsEmpty,
  ungueltigeWahlscheineLoadingFailed,
} = storeToRefs(useWahlbezirkStore());

const wahlscheinnummer = ref<null | number>(null);
//null - no hit on search
//undefined - not search yet, or search was reset
//value - found something while searching
const ungueltigerWahlschein = ref<null | undefined | UngueltigerWahlschein>(
  undefined
);

const feedbackNoDataAvailableIsVisible = computed(
  () =>
    ungueltigeWahlscheineIsEmpty.value &&
    !ungueltigeWahlscheineLoadingFailed.value &&
    !ungueltigeWahlscheineIsLoading.value
);
const feedbackLoadingFailedIsVisible = computed(
  () =>
    ungueltigeWahlscheineLoadingFailed.value &&
    !ungueltigeWahlscheineIsLoading.value
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

function onRefreshClicked() {
  loadUngueltigeWahlscheine();
}

function onSearchClicked() {
  if (ungueltigerWahlschein.value !== undefined) {
    resetUngueltigerWahlschein();
    wahlscheinValidationForm.value.reset();
  } else if (wahlscheinnummer.value !== null) {
    ungueltigerWahlschein.value = getUngueltigerWahlscheinByWahlscheinnummer(
      `${wahlscheinnummer.value}`
    );
  }
}

function onWahlscheinnummerChanged(newValue: number) {
  wahlscheinnummer.value = newValue;
  resetUngueltigerWahlschein();
}

function resetUngueltigerWahlschein() {
  ungueltigerWahlschein.value = undefined;
}
</script>
