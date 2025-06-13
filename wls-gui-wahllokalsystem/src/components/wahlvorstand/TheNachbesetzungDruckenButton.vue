<template>
  <v-btn
    v-if="currentUserWahlbezirksArt === 'BWB'"
    prepend-icon="$printer"
    @click="onNachbesetzungDruckenClicked"
  >
    Nachbesetzung drucken
  </v-btn>
</template>

<script setup lang="ts">
import type { NachbesetzungsDruckInput } from "@/types/wahlvorstand/NachbesetzungsDruckInput.ts";

import { storeToRefs } from "pinia";
import { VBtn } from "vuetify/components";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useWahlvorstandNachbesetzungsDruck } from "@/composables/wahlvorstand/wahlvorstandNachbesetzungsDruck.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlvorstandStore } from "@/stores/wahlvorstandStore.ts";

const { loadWahlvorstand, sendWahlvorstand } = useWahlvorstandStore();
const { buildTemplateFromData } = useWahlvorstandNachbesetzungsDruck();
const { toHhMm } = useDateTimeFormatter();

const { currentUserWahlbezirkNummer, currentUserWahlbezirksArt } =
  storeToRefs(useUserStore());
const { wahlvorstand } = storeToRefs(useWahlvorstandStore());

async function onNachbesetzungDruckenClicked() {
  await sendWahlvorstand();
  await loadWahlvorstand(); //(vorher noch in klammern notification false gewesen)
  _openPrintDialog();
}

function _openPrintDialog() {
  const data: NachbesetzungsDruckInput = {
    wahlbezirknummer: currentUserWahlbezirkNummer.value || "",
    wahlvorstaende: wahlvorstand.value.wahlvorstandsmitglieder,
    druckZeitpunkt: toHhMm(new Date()),
  };

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
</script>
