<template>
  <v-card>
    <v-card-title>Zahl der Wahlurnen</v-card-title>
    <v-card-text class="pb-0">
      <v-form
        ref="wahlurnenForm"
        v-model="anzahlWahlurnenValidForm"
      >
        <div
          v-for="wahl in wahlen"
          :key="wahl.wahlID"
        >
          <base-number-input
            v-model="anzahlWahlurnen[wahl.wahlID]"
            :label="`Anzahl der Wahlurnen für ${wahl.name}`"
            :rules="[REQUIRED, MIN_NUMBER(1), MAX_NUMBER(99)]"
            width="500"
          />
        </div>
      </v-form>
    </v-card-text>
    <v-card-actions>
      <base-button-save
        active
        :disabled="isSaveButtonDisabled"
        @click="onSaveAnzahlWahlurnenClicked"
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
  VForm,
} from "vuetify/components";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import BaseNumberInput from "@/components/common/inputs/BaseNumberInput.vue";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { MAX_NUMBER, MIN_NUMBER, REQUIRED } from "@/util/rules.ts";

const anzahlWahlurnen = ref<{ [key: string]: number }>({});

const anzahlWahlurnenValidForm = ref<null | boolean>(null);
const wahlurnenForm = ref<HTMLFormElement>();
const schliessungsuhrzeit = ref<Date | undefined>(undefined);

const wahlbezirkStore = useWahlbezirkStore();
const wahlenStore = useWahlenStore();

//const wahlen = wahlenStore.wahlen;
const wahlen = ref([
  { wahlID: 1, name: "Bundestagswahl" },
  { wahlID: 2, name: "Landtagswahl" },
  { wahlID: 3, name: "Europawahl" },
]);

const isSaveButtonDisabled = computed(
  () => anzahlWahlurnenValidForm.value !== true
);

function onSaveAnzahlWahlurnenClicked() {
  // TODO
  //if (schliessungsuhrzeit.value) {
  //  wahlbezirkStore.sendSchliessungsuhrzeit(
  //    schliessungsuhrzeit.value.toISOString()
  //  );
  //}
}
</script>
