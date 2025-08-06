<template>
  <v-container>
    <v-card>
      <v-card-title>Wahlurne öffnen und Stimmzettel zählen</v-card-title>
      <v-card-text class="pb-0 pt-2">
        <v-form
          v-model="anzahlStimmzettelValidForm"
        >
          <div class="d-flex flex-wrap justify-start">
            <div>
              <v-number-input
                v-model="wahl.stimmzettelumschlaege.anzahlWaehler"
                class="mr-4"
                :rules="[REQUIRED, MIN_NUMBER(0), MAX_NUMBER(9999)]"
                min-width="20rem"
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
          :loading="isStimmzettelumschlaegeSaving"
          :disabled="isSaveButtonDisabled"
          @click="onSaveAnzahlStimmzettelClicked"
        />
      </v-card-actions>
    </v-card>
  </v-container>
</template>

<script setup lang="ts">
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

const props = defineProps<{
  wahlId: string;
}>();

const {
  getWahlOrUndefinedById,
  saveStimmzettelumschlaege,
  isStimmzettelumschlaegeSaving,
} = useWahlenStore();

const wahl = getWahlOrUndefinedById(props.wahlId);
if (!wahl) {
  throw new Error(`Wahl with ID ${props.wahlId} not found`);
}

const anzahlStimmzettelValidForm = ref<null | boolean>(null);

const isSaveButtonDisabled = computed(() => {
  return anzahlStimmzettelValidForm.value !== true;
});

function onSaveAnzahlStimmzettelClicked() {
  saveStimmzettelumschlaege(props.wahlId);
}
</script>
