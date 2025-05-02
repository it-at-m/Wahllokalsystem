<template>
  <div>
    <v-checkbox
      v-model="ereignisStore.wahlbezirkEreignisse.keineVorfaelle"
      :disabled="isCheckboxNoVorfaelleDisabled"
      label="Besondere Vorfälle während der Wahlhandlung waren nicht zu verzeichnen."
      hide-details
      data-test="checkboxKeineVorfaelle"
    ></v-checkbox>
    <v-checkbox
      v-model="ereignisStore.wahlbezirkEreignisse.keineVorkommnisse"
      :disabled="isCheckboxNoVorkommnisseDisabled"
      label="Besondere Vorkommnisse während der Auszählung waren nicht zu verzeichnen."
      hide-details
      data-test="checkboxKeineVorkommnisse"
    ></v-checkbox>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { VCheckbox } from "vuetify/components";

import { useEreignisStore } from "@/stores/ereignisStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";

const ereignisStore = useEreignisStore();
const wahlbezirkStore = useWahlbezirkStore();

const isCheckboxNoVorfaelleDisabled = computed(
  () => ereignisStore.hasVorfaelle
);
const isCheckboxNoVorkommnisseDisabled = computed(
  () =>
    ereignisStore.hasVorkommnisse ||
    wahlbezirkStore.schliessungsUhrzeitSent === null
);
</script>
