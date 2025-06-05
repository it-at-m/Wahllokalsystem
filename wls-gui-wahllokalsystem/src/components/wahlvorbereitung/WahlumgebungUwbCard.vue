<template>
  <v-card>
    <v-card-title>Zahl der Wahlurnen</v-card-title>
    <v-card-text class="pb-0">
      <v-form
        ref="wahlurnenForm"
        v-model="anzahlWahlurnenValidForm"
      >
        <div class="d-flex flex-wrap justify-space-between">
          <div
            v-for="(wahl, index) in wahlen"
            :key="index"
          >
            <v-text-field
              :model-value="urnenwahlVorbereitung.urnenAnzahl[index]?.anzahl"
              :label="`Anzahl der Wahlurnen ${wahl.name}`"
              clearable
              variant="solo"
              :rules="[REQUIRED, MIN_NUMBER(1), MAX_NUMBER(99)]"
              type="number"
              hide-spin-buttons
              width="20rem"
              :data-test="`textFieldUrnenAnzahl_${index}`"
              @update:model-value="
                (value) => onAnzahlUpdateModelValue(value, index)
              "
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
        <div class="d-flex flex-wrap justify-space-between">
          <div>
            <v-text-field
              v-model="urnenwahlVorbereitung.anzahlWahltische"
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
              v-model="urnenwahlVorbereitung.anzahlNebenraeume"
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
              v-model="urnenwahlVorbereitung.anzahlWahlkabinen"
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
import type { Urnenwahlvorbereitung } from "@/types/wahlvorbereitung/Urnenwahlvorbereitung.ts";

import { storeToRefs } from "pinia";
import { computed, ref, watch } from "vue";
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

async function onAnzahlUpdateModelValue(
  newAnzahl: string | undefined,
  urnenWahlIndex: number
) {
  updateAnzahlByIndex(newAnzahl, urnenWahlIndex);
  if (wahlurnenForm.value) {
    await wahlurnenForm.value?.validate();
  }
}

function updateAnzahlByIndex(anzahl: string | undefined, index: number) {
  if (urnenwahlVorbereitung.value.urnenAnzahl) {
    const anzahlToChange = urnenwahlVorbereitung.value.urnenAnzahl[index];
    if (anzahlToChange == undefined) {
      return;
    }
    if (anzahl) {
      const parsedAnzahl = Number(anzahl);
      if (!isNaN(parsedAnzahl)) {
        anzahlToChange.anzahl = parsedAnzahl;
      } else {
        anzahlToChange.anzahl = 0;
      }
    } else {
      anzahlToChange.anzahl = 0;
    }
  }
}

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
<style scoped>
.error-text {
  color: rgb(var(--v-theme-error));
}
</style>
