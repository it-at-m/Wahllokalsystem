<template>
  <v-card>
    <v-card-title
      >Anzahl der Wahlbriefe (aus Wahlurne und Wahlbrife, die vor 18 Uhr
      übergeben wurden)</v-card-title
    >
    <v-card-text class="pb-0 pt-2">
      <v-form v-model="anzahlWahlbriefeValid">
        <v-number-input
          v-model="wahlbriefDaten.wahlbriefe"
          class="mr-4"
          :rules="[REQUIRED, MIN_NUMBER(1), MAX_NUMBER(9999)]"
          data-test="textFieldWahlbriefeAnzahl"
          label="Anzahl Wahlbriefe"
          max-width="300"
          clearable
        />
      </v-form>
    </v-card-text>
    <v-card-title
      >Anzahl der Verzeichnisse der für ungültig erklärten
      Wahlscheine</v-card-title
    >
    <v-card-text class="pb-0 pt-2">
      <v-form v-model="anzahlVerzeichnisseValid">
        <v-number-input
          v-model="wahlbriefDaten.verzeichnisseUngueltige"
          class="mr-4"
          :rules="[REQUIRED, MIN_NUMBER(0), MAX_NUMBER(9999)]"
          data-test="textFieldVerzeichnisseAnzahl"
          label="Anzahl Verzeichnisse"
          max-width="300"
          clearable
        />
      </v-form>
    </v-card-text>
    <v-card-title>Anzahl der Nachträge zu den Verzeichnissen</v-card-title>
    <v-card-text class="pb-0 pt-2">
      <v-form v-model="anzahlNachtraegeValid">
        <v-number-input
          v-model="wahlbriefDaten.nachtraege"
          class="mr-4"
          :rules="[REQUIRED, MIN_NUMBER(0), MAX_NUMBER(9999)]"
          data-test="textFieldNachtraegeAnzahl"
          label="Anzahl Nachträge"
          max-width="300"
          clearable
        />
      </v-form>
    </v-card-text>
    <v-card-title
      >Anzahl der nach 18 Uhr nachgelieferten Wahlbriefe</v-card-title
    >
    <v-card-text>
      <v-form
        ref="nachtraeglichUeberbrachteForm"
        v-model="anzahlNachtraeglichUeberbrachteValid"
      >
        <div class="d-flex flex-wrap justify-start">
          <div>
            <v-number-input
              v-model="wahlbriefDaten.nachtraeglichUeberbrachte"
              class="mr-4"
              :rules="[MIN_NUMBER(0), MAX_NUMBER(9999)]"
              data-test="textFieldNachtraeglichUeberbrachteAnzahl"
              label="Anzahl Wahlbriefe"
              min-width="300"
              max-width="300"
              clearable
            />
          </div>
          <div>
            <base-time-input
              v-model="wahlbriefDaten.zeitNachtraeglichUeberbrachte"
              class="mr-4"
              min-width="300"
              max-width="300"
              data-test="timeInputZeitNachtraeglichUeberbrachteAnzahl"
              :rules="getDateRules()"
              :disabled="!isZeitNachtragelichUeberbrachtRequired()"
            />
          </div>
        </div>
      </v-form>
    </v-card-text>
    <v-card-actions>
      <base-button-save
        active
        data-test="button-save"
        :disabled="isSaveButtonDisabled"
        :loading="wahlbriefDatenIsSaving"
        @click="onSaveBriefwahldatenClicked"
      />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed, ref, watch } from "vue";
import {
  VCard,
  VCardActions,
  VCardText,
  VCardTitle,
  VForm,
  VNumberInput,
} from "vuetify/components";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseTimeInput from "@/components/common/inputs/BaseTimeInput.vue";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import {
  MAX_NUMBER,
  MIN_NUMBER,
  REQUIRED,
  TIME_NOT_IN_FUTURE,
} from "@/util/rules.ts";

const { sendWahlbriefdaten } = useWahlbezirkStore();
const { wahlbriefDaten, wahlbriefDatenIsSaving } =
  storeToRefs(useWahlbezirkStore());

const anzahlWahlbriefeValid = ref<null | boolean>(null);
const anzahlVerzeichnisseValid = ref<null | boolean>(null);
const anzahlNachtraegeValid = ref<null | boolean>(null);
const anzahlNachtraeglichUeberbrachteValid = ref<null | boolean>(null);

const nachtraeglichUeberbrachteForm = ref<HTMLFormElement>();

const getDateRules = () => {
  const rules = [];
  if (isZeitNachtragelichUeberbrachtRequired()) {
    rules.push(REQUIRED);
  }
  rules.push(TIME_NOT_IN_FUTURE);
  return rules;
};

watch(
  () => wahlbriefDaten.value.nachtraeglichUeberbrachte,
  (newValue) => {
    if (newValue === undefined || newValue < 1) {
      nachtraeglichUeberbrachteForm.value?.resetValidation();
      wahlbriefDaten.value.zeitNachtraeglichUeberbrachte = undefined;
    } else {
      nachtraeglichUeberbrachteForm.value?.validate();
    }
  }
);

const isSaveButtonDisabled = computed(() => {
  return (
    anzahlWahlbriefeValid.value !== true ||
    anzahlVerzeichnisseValid.value !== true ||
    anzahlNachtraegeValid.value !== true ||
    (anzahlNachtraeglichUeberbrachteValid.value !== true &&
      isZeitNachtragelichUeberbrachtRequired())
  );
});

function isZeitNachtragelichUeberbrachtRequired() {
  return (
    wahlbriefDaten.value.nachtraeglichUeberbrachte !== undefined &&
    wahlbriefDaten.value.nachtraeglichUeberbrachte > 0
  );
}

function onSaveBriefwahldatenClicked() {
  sendWahlbriefdaten(wahlbriefDaten.value);
}
</script>
