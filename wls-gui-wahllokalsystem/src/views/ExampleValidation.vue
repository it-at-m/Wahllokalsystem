<template>
  <v-card>
    <v-col class="text-center">
      <h2>
        Dieser View zeigt, wie Validierung mit Regeln und Forms funktioniert.
      </h2>
    </v-col>
    <div>
      <v-tabs
        v-model="tab"
        align-tabs="center"
      >
        <v-tab value="0">input fields</v-tab>
        <v-tab value="1">form button disabled</v-tab>
        <v-tab value="2">form method validation check</v-tab>
      </v-tabs>
      <v-tabs-window v-model="tab">
        <v-tabs-window-item value="0">
          <v-container class="d-flex flex-column align-center mt-5 mb-5">
            <v-text-field
              min-width="400"
              label="Input required"
              clearable
              :rules="[REQUIRED]"
            ></v-text-field>
            <v-text-field
              min-width="400"
              label="Input has to be between 2 and 10"
              :rules="[MIN_LENGTH(2), MAX_LENGTH(10)]"
            ></v-text-field>
          </v-container>
        </v-tabs-window-item>
        <v-tabs-window-item value="1">
          <v-container class="d-flex flex-column align-center mt-5 mb-5">
            <p>
              Alle inputs müssen valide sein, bevor der submit-button betätigt
              werden kann
            </p>
            <v-form
              ref="firstForm"
              v-model="firstFormIsValid"
              class="mt-5"
              fast-fail
              @submit.prevent="submitFirstForm"
            >
              <v-text-field
                :rules="[REQUIRED]"
                clearable
                label="Input required"
                min-width="400"
              ></v-text-field>
              <v-text-field
                :rules="[MAX_LENGTH(20)]"
                label="Input cant be longer then 20"
                min-width="400"
              ></v-text-field>
              <v-text-field
                :rules="[MIN_LENGTH(5)]"
                label="Input cant be shorter than 5"
                min-width="400"
              ></v-text-field>
              <v-btn
                primary
                class="mt-2"
                text="Submit"
                type="submit"
                :disabled="!firstFormIsValid"
                block
              ></v-btn>
            </v-form>
          </v-container>
        </v-tabs-window-item>
        <v-tabs-window-item value="2">
          <v-container class="d-flex flex-column align-center mt-5 mb-5">
            <p>
              Funktionalität des submit-buttons wird nur aktiviert, wenn alle
              Felder valide sind
            </p>
            <v-form
              ref="secondForm"
              v-model="secondFormIsValid"
              class="mt-5"
              fast-fail
              @submit.prevent="submitSecondForm"
            >
              <v-text-field
                :rules="[REQUIRED]"
                clearable
                label="Input required"
                min-width="400"
              ></v-text-field>
              <v-text-field
                :rules="[MAX_LENGTH(20)]"
                label="Input cant be longer then 20"
                min-width="400"
              ></v-text-field>
              <v-text-field
                :rules="[MIN_LENGTH(5)]"
                label="Input cant be shorter than 5"
                min-width="400"
              ></v-text-field>
              <v-btn
                primary
                class="mt-2"
                text="Submit"
                type="submit"
              ></v-btn>
            </v-form>
          </v-container>
        </v-tabs-window-item>
      </v-tabs-window>
    </div>
  </v-card>
</template>

<script setup lang="ts">
import { ref } from "vue";
import {
  VBtn,
  VCard,
  VCol,
  VContainer,
  VForm,
  VTab,
  VTabs,
  VTabsWindow,
  VTabsWindowItem,
  VTextField,
} from "vuetify/components";

import { MAX_LENGTH, MIN_LENGTH, REQUIRED } from "@/util/rules";

const tab = ref(null);
const firstFormIsValid = ref(false);
const secondFormIsValid = ref(false);
const firstForm = ref<HTMLFormElement>();
const secondForm = ref<HTMLFormElement>();

function submitFirstForm() {
  firstForm.value?.reset();
}

async function submitSecondForm() {
  if (secondForm.value) {
    const { valid } = await secondForm.value.validate();
    if (valid) {
      secondForm.value.reset();
    }
  }
}
</script>
