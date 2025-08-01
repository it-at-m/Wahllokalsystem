<template>
  <v-container>
    <v-card>
      <v-card-title>Zählen der Stimmzettel</v-card-title>
      <v-card-text class="pb-0 pt-2">
        <v-form
          ref="anzahlStimmzettelForm"
          v-model="anzahlStimmzettelValidForm"
        >
          <div class="d-flex flex-wrap justify-start">
            <div>
              <v-number-input
                class="mr-4"
                :rules="[REQUIRED, MIN_NUMBER(0), MAX_NUMBER(999)]"
                min-width="30rem"
                data-test="numberInputAnzahlStimmzettel"
                label="Anzahl der Stimmzettel"
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
          :loading="isStimmzettelumschlaegeSaving"
          @click="onSaveAnzahlStimmzettelClicked"
        />
      </v-card-actions>
    </v-card>
  </v-container>
</template>

<script setup lang="ts">
// TODO:
// Objekt 'stimmzettelumschlaege' mit wahlID und wahlbezirkID übergeben
// Anzahl Stimmzettel eingeben, beim Speichern checken gegen Anzahl an Stimmabgabevermerken
// -> bei Ungleichheit Dialog anzeigen mit Begründung

// initial falls vorhanden stimmzettelvorschlaege vorbelegen
// GET /businessActions/stimmzettelumschlaege/{wahlID}/{wahlbezirkID}

// initial falls vorhanden Begruendung für von der Anzahl an Stimmabgabevermerken
// abweichenden Zahl an Stimmzetteln vorbelegen
// GET /businessActions/begruendung/{wahlbezirkID}/{wahlID}/{stapelart}

import { computed, ref } from "vue";
import {
  VCard,
  VCardActions,
  VCardText,
  VCardTitle,
  VContainer,
  VForm,
  VNumberInput,
} from "vuetify/components";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { MAX_NUMBER, MIN_NUMBER, REQUIRED } from "@/util/rules.ts";

const anzahlStimmzettelValidForm = ref<null | boolean>(null);
const anzahlStimmzettelForm = ref<HTMLFormElement>();

const { saveStimmzettelumschlaege, isStimmzettelumschlaegeSaving } =
  useWahlenStore();

const isSaveButtonDisabled = computed(() => {
  return anzahlStimmzettelValidForm.value !== true;
});

function onSaveAnzahlStimmzettelClicked() {
  saveStimmzettelumschlaege();
}
</script>
