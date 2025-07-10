<template>
  <v-container>
    <v-card>
      <v-card-title>Zahl der Wahlurnen</v-card-title>
      <v-card-text class="pb-0 pt-2">
        <v-form
          ref="wahlurnenForm"
          v-model="anzahlWahlurnenValidForm"
        >
          <div class="d-flex flex-wrap justify-start">
            <div
              v-for="(wahl, index) in briefwahlVorbereitung.urnenAnzahl"
              :key="index"
            >
              <v-number-input
                v-model="wahl.anzahl"
                class="mr-4"
                :rules="[REQUIRED, MIN_NUMBER(1), MAX_NUMBER(99)]"
                :data-test="`textFieldUrnenAnzahl_${index}`"
                :label="`Anzahl der Wahlurnen ${getWahlNameOrBlankStringById(wahl.wahlID)}`"
                min-width="30rem"
                clearable
              />
            </div>
          </div>
          <v-checkbox
            v-model="briefwahlVorbereitung.urneVersiegelt"
            :label="checkboxLabelText"
            data-test="checkboxAlleVersiegelt"
          />
        </v-form>
      </v-card-text>
      <v-card-actions>
        <base-button-save
          active
          :disabled="isSaveButtonDisabled"
          :loading="briefWahlVorbereitungIsSaving"
          @click="onSaveWahlumgebungBWBClicked"
        />
      </v-card-actions>
    </v-card>
  </v-container>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed, ref } from "vue";
import {
  VCard,
  VCardActions,
  VCardText,
  VCardTitle,
  VCheckbox,
  VContainer,
  VForm,
  VNumberInput,
} from "vuetify/components";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { MAX_NUMBER, MIN_NUMBER, REQUIRED } from "@/util/rules.ts";

const anzahlWahlurnenValidForm = ref<null | boolean>(null);
const wahlurnenForm = ref<HTMLFormElement | null>(null);

const { wahlen } = storeToRefs(useWahlenStore());
const { sendBriefwahlvorbereitung } = useWahlbezirkStore();
const { briefWahlVorbereitungIsSaving, briefwahlVorbereitung } =
  storeToRefs(useWahlbezirkStore());
const { getWahlNameOrBlankStringById } = useWahlenStore();

const isSaveButtonDisabled = computed(() => {
  return (
    anzahlWahlurnenValidForm.value !== true ||
    !briefwahlVorbereitung.value.urneVersiegelt
  );
});

function onSaveWahlumgebungBWBClicked() {
  sendBriefwahlvorbereitung(briefwahlVorbereitung.value);
}

const checkboxLabelText = computed(() => {
  if (wahlen.value && wahlen.value?.length > 1) {
    return "Die Wahlurnen waren leer und wurden ordnungsgemäß versiegelt";
  }
  return "Die Wahlurne war leer und wurde ordnungsgemäß versiegelt";
});
</script>
<style scoped></style>
