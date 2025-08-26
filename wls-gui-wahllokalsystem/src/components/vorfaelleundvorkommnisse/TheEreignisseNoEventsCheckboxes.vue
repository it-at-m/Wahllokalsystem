<template>
  <div>
    <v-checkbox
      v-if="showCheckboxNoVorfaelle"
      v-model="wahlbezirkEreignisse.keineVorfaelle"
      :disabled="isCheckboxNoVorfaelleDisabled"
      label="Besondere Vorfälle während der Wahlhandlung waren nicht zu verzeichnen."
      hide-details
      data-test="checkboxKeineVorfaelle"
    />
    <v-checkbox
      v-model="wahlbezirkEreignisse.keineVorkommnisse"
      :disabled="isCheckboxNoVorkommnisseDisabled"
      label="Besondere Vorkommnisse während der Auszählung waren nicht zu verzeichnen."
      hide-details
      data-test="checkboxKeineVorkommnisse"
    />
  </div>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { computed } from "vue";

import { useEreignisStore } from "@/stores/ereignisStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";

const { schliessungsuhrzeitState } = storeToRefs(useWahlbezirkStore());
const {
  wahlbezirkEreignisse,
  ereigniseintraegeContainsVorkommnisse,
  ereigniseintraegeContainsVorfaelle,
} = storeToRefs(useEreignisStore());
const { isBWB } = storeToRefs(useUserStore());

const isCheckboxNoVorfaelleDisabled = computed(
  () => ereigniseintraegeContainsVorfaelle.value
);
const isCheckboxNoVorkommnisseDisabled = computed(() => {
  if (ereigniseintraegeContainsVorkommnisse.value) {
    return true;
  }
  if (isBWB.value) {
    return false;
  }
  return schliessungsuhrzeitState.value.schliessungsuhrzeitSent === undefined;
});
const showCheckboxNoVorfaelle = computed(() => !isBWB.value);
</script>
