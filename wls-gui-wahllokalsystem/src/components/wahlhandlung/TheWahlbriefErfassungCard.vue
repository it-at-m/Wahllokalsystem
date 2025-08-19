<template>
  <v-card>
    <v-card-title>
      Anzahl der Wahlbriefe (aus Wahlurne und Wahlbriefe, die vor
      {{ toHhMm(getDateFromTimeString(fruehesteSchliessungsuhrzeit)) }} Uhr
      übergeben wurden)
    </v-card-title>
    <v-card-text class="pb-0 pt-2">
      <v-form v-model="anzahlWahlbriefeValid">
        <v-number-input
          v-model="wahlbriefDatenState.wahlbriefDaten.wahlbriefe"
          class="mr-4"
          :rules="[required, minNumber(1), maxNumber(9999)]"
          data-test="textFieldWahlbriefeAnzahl"
          label="Anzahl Wahlbriefe"
          :max-width="WIDTH"
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
          v-model="wahlbriefDatenState.wahlbriefDaten.verzeichnisseUngueltige"
          class="mr-4"
          :rules="[required, minNumber(0), maxNumber(9999)]"
          data-test="textFieldVerzeichnisseAnzahl"
          label="Anzahl Verzeichnisse"
          :max-width="WIDTH"
          clearable
        />
      </v-form>
    </v-card-text>
    <v-card-title>Anzahl der Nachträge zu den Verzeichnissen</v-card-title>
    <v-card-text class="pb-0 pt-2">
      <v-form v-model="anzahlNachtraegeValid">
        <v-number-input
          v-model="wahlbriefDatenState.wahlbriefDaten.nachtraege"
          class="mr-4"
          :rules="[required, minNumber(0), maxNumber(9999)]"
          data-test="textFieldNachtraegeAnzahl"
          label="Anzahl Nachträge"
          :max-width="WIDTH"
          clearable
        />
      </v-form>
    </v-card-text>
    <v-card-title>
      Anzahl der nach
      {{ toHhMm(getDateFromTimeString(fruehesteSchliessungsuhrzeit)) }} Uhr
      nachgelieferten Wahlbriefe
    </v-card-title>
    <v-card-text>
      <v-form
        ref="nachtraeglichUeberbrachteForm"
        v-model="anzahlNachtraeglichUeberbrachteValid"
        data-test="nachtraeglichUeberbrachteForm"
      >
        <div class="d-flex flex-wrap justify-start">
          <div>
            <v-number-input
              v-model="
                wahlbriefDatenState.wahlbriefDaten.nachtraeglichUeberbrachte
              "
              class="mr-4"
              :rules="[minNumber(0), maxNumber(9999)]"
              data-test="textFieldNachtraeglichUeberbrachteAnzahl"
              label="Anzahl Wahlbriefe"
              :min-width="WIDTH"
              :max-width="WIDTH"
              clearable
            />
          </div>
          <div>
            <base-time-input
              v-model="
                wahlbriefDatenState.wahlbriefDaten.zeitNachtraeglichUeberbrachte
              "
              class="mr-4"
              :min-width="WIDTH"
              :max-width="WIDTH"
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
        :loading="wahlbriefDatenState.wahlbriefDatenIsSaving"
        @click="onSaveBriefwahldatenClicked"
      />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed, ref, watch } from "vue";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseTimeInput from "@/components/common/inputs/BaseTimeInput.vue";
import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useRules } from "@/composables/common/rules.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";

const { maxNumber, minNumber, required, timeNotInFuture } = useRules();

const { wahlbriefDatenActions } = useWahlbezirkStore();
const { wahlbriefDatenState } = storeToRefs(useWahlbezirkStore());
const { fruehesteSchliessungsuhrzeit } = useInfomanagementStore();
const { toHhMm, getDateFromTimeString } = useDateTimeFormatter();

const anzahlWahlbriefeValid = ref<null | boolean>(null);
const anzahlVerzeichnisseValid = ref<null | boolean>(null);
const anzahlNachtraegeValid = ref<null | boolean>(null);
const anzahlNachtraeglichUeberbrachteValid = ref<null | boolean>(null);

const nachtraeglichUeberbrachteForm = ref<HTMLFormElement>();

const WIDTH = 300;

const getDateRules = () => {
  const rules = [];
  if (isZeitNachtragelichUeberbrachtRequired()) {
    rules.push(required);
  }
  rules.push(timeNotInFuture);
  return rules;
};

watch(
  () => wahlbriefDatenState.value.wahlbriefDaten.nachtraeglichUeberbrachte,
  (newValue) => {
    if (newValue === undefined || newValue < 1) {
      nachtraeglichUeberbrachteForm.value?.resetValidation();
      wahlbriefDatenState.value.wahlbriefDaten.zeitNachtraeglichUeberbrachte =
        undefined;
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
    wahlbriefDatenState.value.wahlbriefDaten.nachtraeglichUeberbrachte !==
      undefined &&
    wahlbriefDatenState.value.wahlbriefDaten.nachtraeglichUeberbrachte > 0
  );
}

function onSaveBriefwahldatenClicked() {
  wahlbriefDatenActions.sendWahlbriefdaten();
}
</script>
