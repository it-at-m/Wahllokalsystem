<template>
  <v-card>
    <v-card-title>Zahl der Wahlurnen</v-card-title>
    <v-card-text class="pb-0">
      <v-form v-model="anzahlWahlurnenValidForm">
        <div class="d-flex flex-wrap justify-space-between">
          <div
            v-for="wahl in wahlen"
            :key="wahl.wahlID"
          >
            <v-text-field
              v-model="anzahlWahlurnen[wahl.wahlID]"
              :label="`Anzahl der Wahlurnen ${wahl.name}`"
              clearable
              variant="solo"
              :rules="[REQUIRED, MIN_NUMBER(1), MAX_NUMBER(99)]"
              type="number"
              hide-spin-buttons
              width="20rem"
            />
          </div>
        </div>
        <v-checkbox
          v-model="checkboxValue"
          label="Die Wahlurne(n) war(en) leer und wurde(n) ordnungsgemäß versiegelt"
        />
      </v-form>
    </v-card-text>
  </v-card>

  <v-card>
    <v-card-title>Abstimmungsschutzvorrichtungen</v-card-title>
    <v-card-text class="pb-0">
      <v-form
        ref="abstimmungsschutzvorrichtungenForm"
        v-model="abstimmungsschutzvorrichtungenValidForm"
      >
        <div class="d-flex flex-wrap justify-space-between">
          <div>
            <v-text-field
              v-model="anzahlTischeSichtblenden"
              label="Anzahl der Tische mit Sichtblenden"
              clearable
              variant="solo"
              :rules="[REQUIRED, MIN_NUMBER(0), MAX_NUMBER(99)]"
              type="number"
              hide-spin-buttons
              width="20rem"
            />
          </div>
          <div>
            <v-text-field
              v-model="anzahlNebenrauemeWahlraum"
              label="Anzahl der Nebenräume im Wahlraum"
              clearable
              variant="solo"
              :rules="[REQUIRED, MIN_NUMBER(0), MAX_NUMBER(99)]"
              type="number"
              hide-spin-buttons
              width="20rem"
            />
          </div>
          <div>
            <v-text-field
              ref="inputRef"
              v-model="anzahlWahlkabinen"
              label="Anzahl der Wahlkabinen"
              clearable
              variant="solo"
              :rules="[REQUIRED, MIN_NUMBER(0), MAX_NUMBER(99)]"
              type="number"
              hide-spin-buttons
              width="20rem"
            />
          </div>
        </div>
      </v-form>
    </v-card-text>
    <v-card-actions>
      <base-button-save
        active
        :disabled="isSaveButtonDisabled"
        @click="onSaveWahlumgebungUWBClicked"
      />
    </v-card-actions>
  </v-card>

  <v-card
    v-show="isMinimumRequired"
    class="border-lg border-error"
  >
    <v-card-title>Ungültige Eingaben</v-card-title>
    <v-card-text>
      <div class="d-flex align-center mb-2">
        <v-icon
          class="mr-2 error-text"
          icon="$invalid"
        />
        <div class="error-text">
          Die Summe der Kabinen, Tische und Nebenräume muss mindestens 1
          betragen.
        </div>
      </div>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import {
  VCard,
  VCardActions,
  VCardText,
  VCardTitle,
  VCheckbox,
  VForm,
  VIcon,
  VTextField,
} from "vuetify/components";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { MAX_NUMBER, MIN_NUMBER, REQUIRED } from "@/util/rules.ts";

const anzahlWahlurnenValidForm = ref<null | boolean>(null);
const anzahlWahlurnen = ref<{ [key: string]: number }>({});
const checkboxValue = ref(false);

const abstimmungsschutzvorrichtungenValidForm = ref<null | boolean>(null);
const abstimmungsschutzvorrichtungenForm = ref<HTMLFormElement>();
const anzahlTischeSichtblenden = ref<string | undefined>(undefined);
const anzahlNebenrauemeWahlraum = ref<string | undefined>(undefined);
const anzahlWahlkabinen = ref<string | undefined>(undefined);

const urnenwahlVorbereitung = computed(
  () => wahlbezirkStore.urnenwahlVorbereitung
);

const isMinimumRequired = computed(() => {
  if (abstimmungsschutzvorrichtungenValidForm.value !== true) {
    return false;
  }

  const tischeSichtblenden = Number(anzahlTischeSichtblenden.value) || 0;
  const nebenraeumeWahlraum = Number(anzahlNebenrauemeWahlraum.value) || 0;
  const wahlkabinen = Number(anzahlWahlkabinen.value) || 0;

  return tischeSichtblenden + nebenraeumeWahlraum + wahlkabinen < 1;
});

const wahlbezirkStore = useWahlbezirkStore();
const wahlenStore = useWahlenStore();

const wahlen = wahlenStore.wahlen;

const isSaveButtonDisabled = computed(
  () =>
    anzahlWahlurnenValidForm.value !== true ||
    abstimmungsschutzvorrichtungenValidForm.value !== true ||
    !checkboxValue.value ||
    isMinimumRequired.value
);

function onSaveWahlumgebungUWBClicked() {
  // TODO: type Urnenwahlvorbereitung und Wahlurne[] verwenden
  const urnenwahlvorbereitung = Object.entries(anzahlWahlurnen.value).map(
    ([wahlID, anzahl]) => ({
      wahlID: Number(wahlID),
      anzahlWahlurnen: anzahl,
    })
  );

  // TODO: type Urnenwahlvorbereitung im Store wahlbezirk erfassen
  //wahlbezirkStore.sendUrnenwahlvorbereitung(urnenwahlvorbereitung);
  console.log("Saved Wahlurnen:", urnenwahlvorbereitung);
}
</script>
<style scoped>
.error-text {
  color: rgb(var(--v-theme-error));
}
</style>
