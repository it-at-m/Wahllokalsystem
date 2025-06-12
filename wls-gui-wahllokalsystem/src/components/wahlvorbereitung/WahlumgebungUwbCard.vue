<template>
  <v-container>
    <v-card>
      <v-card-title>Zahl der Wahlurnen</v-card-title>
      <v-card-text class="pb-0">
        <v-form
          ref="wahlurnenForm"
          v-model="anzahlWahlurnenValidForm"
        >
          <div class="d-flex flex-wrap justify-start">
            <div
              v-for="(wahl, index) in urnenwahlVorbereitung.urnenAnzahl"
              :key="index"
            >
              <v-number-input
                class="mr-4"
                v-model="wahl.anzahl"
                :rules="[REQUIRED, MIN_NUMBER(1), MAX_NUMBER(99)]"
                :data-test="`textFieldUrnenAnzahl_${index}`"
                :label="`Anzahl der Wahlurnen ${getWahlNameById(wahl.wahlID)}`"
                min-width="20rem"
                clearable
              />
            </div>
          </div>
          <v-checkbox
            v-model="isCheckboxAlleVersiegeltEnabled"
            label="Die Wahlurne(n) war(en) leer und wurde(n) ordnungsgemäß versiegelt"
            data-test="checkboxAlleVersiegelt"
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
          <div class="d-flex flex-wrap justify-start">
            <div>
              <v-number-input
                class="mr-4"
                v-model="urnenwahlVorbereitung.anzahlWahltische"
                :rules="[REQUIRED, MIN_NUMBER(0), MAX_NUMBER(99)]"
                min-width="20rem"
                data-test="numberInputAnzahlWahltische"
                label="Anzahl der Tische mit Sichtblenden"
                clearable
              />
            </div>
            <div>
              <v-number-input
                class="mr-4"
                v-model="urnenwahlVorbereitung.anzahlNebenraeume"
                :rules="[REQUIRED, MIN_NUMBER(0), MAX_NUMBER(99)]"
                data-test="numberInputAnzahlNebenraeume"
                label="Anzahl der Nebenräume im Wahlraum"
                min-width="20rem"
                clearable
              />
            </div>
            <div>
              <v-number-input
                class="mr-4"
                v-model="urnenwahlVorbereitung.anzahlWahlkabinen"
                :rules="[REQUIRED, MIN_NUMBER(0), MAX_NUMBER(99)]"
                data-test="numberInputAnzahlWahlkabinen"
                label="Anzahl der Wahlkabinen"
                min-width="20rem"
                clearable
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
            color="red"
            class="mr-2"
            icon="$invalid"
          />
          <div class="text-red">
            Die Summe der Kabinen, Tische und Nebenräume muss mindestens 1
            betragen.
          </div>
        </div>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup lang="ts">
import type { Urnenwahlvorbereitung } from "@/types/wahlvorbereitung/Urnenwahlvorbereitung.ts";

import { storeToRefs } from "pinia";
import { computed, ref, watch } from "vue";
import {
  VCard,
  VCardActions,
  VCardText,
  VCardTitle,
  VCheckbox,
  VContainer,
  VForm,
  VIcon,
  VNumberInput,
} from "vuetify/components";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { MAX_NUMBER, MIN_NUMBER, REQUIRED } from "@/util/rules.ts";

const { currentUserWahlbezirksArt } = storeToRefs(useUserStore());
const anzahlWahlurnenValidForm = ref<null | boolean>(null);
const wahlurnenForm = ref<HTMLFormElement | null>(null);
const isCheckboxAlleVersiegeltEnabled = ref(false);
const abstimmungsschutzvorrichtungenValidForm = ref<null | boolean>(null);
const abstimmungsschutzvorrichtungenForm = ref<HTMLFormElement>();
const wahlbezirkStore = useWahlbezirkStore();
const { getWahlNameById } = useWahlenStore();
const { wahlen } = storeToRefs(useWahlenStore());

const isSaveButtonDisabled = computed(() => {
  return (
    anzahlWahlurnenValidForm.value !== true ||
    abstimmungsschutzvorrichtungenValidForm.value !== true ||
    !isCheckboxAlleVersiegeltEnabled.value ||
    isMinimumRequired.value
  );
});

const isMinimumRequired = computed(() => {
  if (abstimmungsschutzvorrichtungenValidForm.value !== true) {
    return false;
  }
  const tischeSichtblenden =
    Number(urnenwahlVorbereitung.value.anzahlWahltische) || 0;
  const nebenraeumeWahlraum =
    Number(urnenwahlVorbereitung.value.anzahlNebenraeume) || 0;
  const wahlkabinen =
    Number(urnenwahlVorbereitung.value.anzahlWahlkabinen) || 0;

  return tischeSichtblenden + nebenraeumeWahlraum + wahlkabinen < 1;
});

const urnenwahlVorbereitung = ref<Urnenwahlvorbereitung>({
  wahlbezirkID: currentUserWahlbezirksArt.value,
  anzahlWahlkabinen: null,
  anzahlWahltische: null,
  anzahlNebenraeume: null,
  urnenAnzahl:
    wahlen.value?.map((wahl) => ({
      wahlID: wahl.wahlID,
      anzahl: null,
      urneVersiegelt: isCheckboxAlleVersiegeltEnabled.value,
    })) || [],
});

watch(isCheckboxAlleVersiegeltEnabled, (newValue) => {
  if (urnenwahlVorbereitung.value.urnenAnzahl) {
    urnenwahlVorbereitung.value.urnenAnzahl.forEach((urnen) => {
      urnen.urneVersiegelt = newValue;
    });
  }
});

function onSaveWahlumgebungUWBClicked() {
  wahlbezirkStore.sendUrnenwahlvorbereitung(urnenwahlVorbereitung.value);
}
</script>
<style scoped></style>
