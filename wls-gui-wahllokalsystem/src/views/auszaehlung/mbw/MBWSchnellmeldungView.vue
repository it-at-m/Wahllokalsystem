<template>
  <v-card>
    <v-card-title>Schnellmeldung</v-card-title>
    <v-card-subtitle
      >Kontrolle, Übermittlung und Druck der Schnellmeldung</v-card-subtitle
    >
    <v-card-text>
      <the-m-b-w-wahlberechtigte-anzeigen-card
        :wahlbezirk-id="currentUserWahlbezirkID"
        :wahl-id="wahlID"
      />
      <the-m-b-w-waehler-anzeigen-card
        :wahlbezirk-id="currentUserWahlbezirkID"
        :wahl-id="wahlID"
      />
      <the-m-b-w-ungueltige-stimmen-anzeigen-card
        :wahlbezirk-id="currentUserWahlbezirkID"
        :wahl-id="wahlID"
      />
      <the-m-b-w-gueltige-stimmen-anzeigen-card
        :wahlbezirk-id="currentUserWahlbezirkID"
        :wahl-id="wahlID"
      />
    </v-card-text>
    <v-card-actions>
      <base-button-save
        save-text="Schnellmeldung senden"
        prepend-icon="$cloudUpload"
        :disabled="!isFormValid"
        @click="onSendenClicked"
      />
      <base-button-save
        save-text="Schnellmeldung korrigieren"
        prepend-icon="$edit"
        :disabled="!isFormValid"
        @click="onKorrigierenClicked"
      />
      <base-button-save
        save-text="Schnellmeldung drucken"
        prepend-icon="$printer"
        :disabled="!isFormValid"
        @click="onDruckenClicked"
      />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRoute, useRouter } from "vue-router";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import TheMBWGueltigeStimmenAnzeigenCard from "@/components/ergebnisermittlung/MBW/stapelAB/TheMBWGueltigeStimmenAnzeigenCard.vue";
import TheMBWWaehlerAnzeigenCard from "@/components/ergebnisermittlung/MBW/stapelAB/TheMBWWaehlerAnzeigenCard.vue";
import TheMBWWahlberechtigteAnzeigenCard from "@/components/ergebnisermittlung/MBW/stapelAB/TheMBWWahlberechtigteAnzeigenCard.vue";
import TheMBWUngueltigeStimmenAnzeigenCard from "@/components/ergebnisermittlung/MBW/stapelC/TheMBWUngueltigeStimmenAnzeigenCard.vue";
import { EXAMPLE_ROUTES_NOTFOUND } from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const route = useRoute();
const router = useRouter();
const { wahlenActions } = useWahlenStore();
const { currentUserWahlbezirkID } = useUserStore();

const isFormValid = ref<null | boolean>(null);

const wahlID = route.params.wahlId as string;
const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);
if (!wahl) {
  router.push({
    name: EXAMPLE_ROUTES_NOTFOUND,
  });
}

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
