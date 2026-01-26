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
        :loading="isDruckenLoading"
        @click="onDruckenClicked"
      />
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import type { ErgebnismeldungDruckInput } from "@/types/ergebnismeldung/MBW/ErgebnismeldungDruckInput.ts";

import { ref } from "vue";
import { useRoute, useRouter } from "vue-router";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import TheMBWGueltigeStimmenAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWGueltigeStimmenAnzeigenCard.vue";
import TheMBWWaehlerAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWaehlerAnzeigenCard.vue";
import TheMBWWahlberechtigteAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelAB/TheMBWWahlberechtigteAnzeigenCard.vue";
import TheMBWUngueltigeStimmenAnzeigenCard from "@/components/ergebnismeldung/MBW/stapelC/TheMBWUngueltigeStimmenAnzeigenCard.vue";
import { useErgebnismeldungDruck } from "@/composables/ergebnismeldung/MBW/ergebnismeldungDruck.ts";
import { useMbwUtils } from "@/composables/ergebnismeldung/MBW/mbwUtils.ts";
import { ROUTE_NOTFOUND } from "@/constants.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const route = useRoute();
const router = useRouter();

const wahlbezirkID = route.params.wahlbezirkId as string;
const wahlID = route.params.wahlId as string;

const { wahlenActions } = useWahlenStore();
const {
  isSendingSchnellmeldung,
  sendSchnellmeldung,
  prepareDataForErgebnismeldungDruck,
} = useMbwUtils(wahlID, wahlbezirkID);
const { buildTemplateFromData } = useErgebnismeldungDruck();

// button logic to be implemented
const isKorrigierenValid = ref<null | boolean>();
const isDruckenValid = ref<null | boolean>(true);
const isDruckenLoading = ref<boolean>(false);

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
  _openPrintDialog();
}

async function _openPrintDialog() {
  isDruckenLoading.value = true;
  try {
    if (wahl) {
      const data: ErgebnismeldungDruckInput =
        await prepareDataForErgebnismeldungDruck(wahl);

      const printWindow = window.open(
        "",
        "",
        "left=0,top=0,width=800,height=900,toolbar=0,scrollbars=0,status=0"
      );

      if (printWindow) {
        printWindow.document.body.innerHTML = buildTemplateFromData(data);
        printWindow.print();
        printWindow.close();
      }
    }
  } catch (e) {
    // todo: toasty
    console.log("fehler: ", e);
  } finally {
    isDruckenLoading.value = false;
  }
}
</script>
