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
import { VCheckbox } from "vuetify/components";

import { useEreignisStore } from "@/stores/ereignisStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const { schliessungsuhrzeitSent } = storeToRefs(useWahlbezirkStore());
const { wahlbezirkEreignisse, hasVorkommnisse, hasVorfaelle } =
  storeToRefs(useEreignisStore());
const { currentUserWahlbezirksArt } = storeToRefs(useUserStore());

const isCheckboxNoVorfaelleDisabled = computed(() => hasVorfaelle.value);
const isCheckboxNoVorkommnisseDisabled = computed(() => {
  if (hasVorkommnisse.value) {
    return true;
  }
  if (currentUserWahlbezirksArt.value === WahlbezirksArtEnum.BWB) {
    return false;
  }
  return schliessungsuhrzeitSent.value === undefined;
});
const showCheckboxNoVorfaelle = computed(
  () => currentUserWahlbezirksArt.value !== WahlbezirksArtEnum.BWB
);
</script>
