<template>
  <v-card>
    <v-card-title> Niederschrift für {{ wahlName }}</v-card-title>
    <v-card-subtitle
      >Kontrolle, Übermittlung und Druck der Niederschrift</v-card-subtitle
    >
    <v-card-actions>
      <v-form v-model="isFormValid">
        <!-- TBD: Platzhalter für Niederschrift-Komponenten -->
      </v-form>
      <base-button-save
        save-text="Niederschrift senden"
        prepend-icon="$cloudUpload"
        :disabled="!isFormValid"
        @click="onSendenClicked"
      />
      <base-button-save
        save-text="Niederschrift korrigieren"
        prepend-icon="$edit"
        :disabled="!isFormValid"
        @click="onKorrigierenClicked"
      />
      <base-button-save
        save-text="Niederschrift drucken"
        prepend-icon="$printer"
        :disabled="!isFormValid"
        @click="onDruckenClicked"
      />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import { computed, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import { EXAMPLE_ROUTES_NOTFOUND } from "@/constants.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const route = useRoute();
const router = useRouter();
const { wahlenActions } = useWahlenStore();

const wahlID = computed(() => route.params.wahlId as string);
const wahlName = computed(() =>
  wahlenActions.getWahlNameOrBlankStringById(wahlID.value)
);
const isFormValid = ref<null | boolean>(null);
watch(
  () => wahlID.value,
  (wahlID) => {
    const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);

    if (!wahl) {
      router.push({
        name: EXAMPLE_ROUTES_NOTFOUND,
      });
    }
  }
);

function onSendenClicked() {
  // to be implemented
}
function onKorrigierenClicked() {
  // to be implemented
}
function onDruckenClicked() {
  // to be implemented
}
</script>
