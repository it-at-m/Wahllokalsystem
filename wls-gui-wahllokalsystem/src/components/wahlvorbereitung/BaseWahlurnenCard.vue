<template>
  <v-card>
    <v-card-title>Zahl der Wahlurnen</v-card-title>
    <v-card-text class="pb-0">
      a
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
  VTextField,
} from "vuetify/components";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { MAX_NUMBER, MIN_NUMBER, REQUIRED } from "@/util/rules.ts";

const anzahlWahlurnen = ref<{ [key: string]: number }>({});
const anzahlWahlurnenValidForm = ref<null | boolean>(null);

const checkboxValue = ref(false);

const abstimmungsschutzvorrichtungenValidForm = ref<null | boolean>(null);
const abstimmungsschutzvorrichtungenForm = ref<HTMLFormElement>();
const anzahlTischeSichtblenden = ref<string | undefined>(undefined);
const anzahlNebenrauemeWahlraum = ref<string | undefined>(undefined);
const anzahlWahlkabinen = ref<string | undefined>(undefined);

const wahlbezirkStore = useWahlbezirkStore();
const wahlenStore = useWahlenStore();

// TODO
//const wahlen = wahlenStore.wahlen;
const wahlen = ref([
  { wahlID: 1, name: "Bundestagswahl" },
  { wahlID: 2, name: "Landtagswahl" },
  { wahlID: 3, name: "Europawahl" },
]);

const isSaveButtonDisabled = computed(
  () =>
    anzahlWahlurnenValidForm.value !== true ||
    abstimmungsschutzvorrichtungenValidForm.value !== true ||
    !checkboxValue.value
);

function onSaveWahlumgebungUWBClicked() {
  const wahlurnenData = Object.entries(anzahlWahlurnen.value).map(
    ([wahlID, anzahl]) => ({
      wahlID: Number(wahlID),
      anzahlWahlurnen: anzahl,
    })
  );
  // TODO
  //wahlbezirkStore.saveWahlumgebungUWB(wahlumgebung);
  console.log("Saved Wahlurnen:", wahlurnenData);
}
</script>
