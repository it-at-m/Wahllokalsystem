<template>
  <v-card>
    <v-card-title class="font-weight-bold">Schnellmeldung</v-card-title>
    <v-card-subtitle class="font-weight-bold mb-10"
      >Kontrolle, Übermittlung und Druck der Schnellmeldung</v-card-subtitle
    >
    <the-m-b-w-wahlberechtigte-anzeigen-card
      class="ma-5"
      :wahlbezirk-id="wahlbezirkID"
      :wahl-id="wahlID"
    />
    <the-m-b-w-waehler-anzeigen-card
      class="ma-5"
      :wahlbezirk-id="wahlbezirkID"
      :wahl-id="wahlID"
    />
    <the-m-b-w-ungueltige-stimmen-anzeigen-card
      class="ma-5"
      :wahlbezirk-id="wahlbezirkID"
      :wahl-id="wahlID"
    />
    <the-m-b-w-gueltige-stimmen-anzeigen-card
      class="ma-5"
      :wahlbezirk-id="wahlbezirkID"
      :wahl-id="wahlID"
    />
    <v-card-actions>
      <base-button-save
        save-text="Schnellmeldung senden"
        prepend-icon="$cloudUpload"
        :loading="isSendingSchnellmeldung"
        @click="onSendenClicked"
      />
      <base-button-save
        save-text="Schnellmeldung korrigieren"
        prepend-icon="$edit"
        :disabled="!isKorrigierenValid"
        @click="onKorrigierenClicked"
      />
      <base-button-save
        save-text="Schnellmeldung drucken"
        prepend-icon="$printer"
        :disabled="!isDruckenValid"
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
import { useMbwUtils } from "@/composables/ergebnisermittlung/mbwUtils.ts";
import { ROUTE_NOTFOUND } from "@/constants.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const route = useRoute();
const router = useRouter();

const wahlbezirkID = route.params.wahlbezirkId as string;
const wahlID = route.params.wahlId as string;

const { wahlenActions } = useWahlenStore();
const { isSendingSchnellmeldung, sendSchnellmeldung } = useMbwUtils(
  wahlID,
  wahlbezirkID
);

// button logic to be implemented
const isKorrigierenValid = ref<null | boolean>();
const isDruckenValid = ref<null | boolean>(true);

const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);
if (!wahl) {
  router.push({
    name: ROUTE_NOTFOUND,
  });
}

function onSendenClicked() {
  sendSchnellmeldung();
}
function onKorrigierenClicked() {
  // to be implemented
}
function onDruckenClicked() {
  // to be implemented
}
</script>
