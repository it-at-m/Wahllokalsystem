<template>
  <base-button-save
    v-if="isBWB"
    prepend-icon="$printer"
    save-text="Nachbesetzung drucken"
    :active="false"
    @click="onNachbesetzungDruckenClicked"
  />
</template>

<script setup lang="ts">
import type { NachbesetzungsDruckInput } from "@/types/wahlvorstand/NachbesetzungsDruckInput.ts";

import { storeToRefs } from "pinia";

import BaseButtonSave from "@/components/common/buttons/BaseButtonSave.vue";
import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useWahlvorstandNachbesetzungsDruck } from "@/composables/wahlvorstand/wahlvorstandNachbesetzungsDruck.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { useWahlvorstandStore } from "@/stores/wahlvorstandStore.ts";

const { loadWahlvorstand, sendWahlvorstand } = useWahlvorstandStore();
const { buildTemplateFromData } = useWahlvorstandNachbesetzungsDruck();
const { toTimeWithHoursAndOptionalMinutes, toGermanDateWithLongMonth } =
  useDateTimeFormatter();

const { currentUserWahlbezirkNummer, isBWB, currentUserHauptWahlID } =
  storeToRefs(useUserStore());
const { wahlvorstand } = storeToRefs(useWahlvorstandStore());
const { wahlenActions } = useWahlenStore();

async function onNachbesetzungDruckenClicked() {
  await sendWahlvorstand();
  await loadWahlvorstand();
  _openPrintDialog();
}

function _openPrintDialog() {
  const data: NachbesetzungsDruckInput = {
    wahlName: wahlenActions.getWahlNameOrBlankStringById(
      currentUserHauptWahlID.value
    ),
    wahlTag:
      toGermanDateWithLongMonth(
        wahlenActions.getWahlTagOrBlankStringById(currentUserHauptWahlID.value)
      ) || "",
    wahlbezirknummer: currentUserWahlbezirkNummer.value || "",
    wahlvorstaende: wahlvorstand.value.wahlvorstandsmitglieder,
    druckZeitpunkt: toTimeWithHoursAndOptionalMinutes(new Date()),
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
