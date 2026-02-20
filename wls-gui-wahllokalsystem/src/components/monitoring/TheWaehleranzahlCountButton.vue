<template>
  <v-btn
    class="bg-white mr-5"
    color="primary"
    prepend-icon="$addCircle"
    min-width="180"
    @keydown.enter.prevent
    @keyup.enter="onWaehleranzahlClicked"
    @click="onWaehleranzahlClicked"
  >
    {{ waehler }} Wähler*innen
  </v-btn>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { onMounted, onUnmounted } from "vue";

import { useMonitoringCronjobService } from "@/composables/monitoring/monitoringCronjobService.ts";
import { useMonitoringStore } from "@/stores/monitoringStore.ts";

const { waehler } = storeToRefs(useMonitoringStore());
const { increaseWaehlerByOne } = useMonitoringStore();
const { startWahlbeteiligungInterval, stopWahlbeteiligungInterval } =
  useMonitoringCronjobService();

onMounted(() => {
  document.addEventListener("keyup", onKeyupEvent);
  startWahlbeteiligungInterval();
});

onUnmounted(() => {
  document.removeEventListener("keyup", onKeyupEvent);
  stopWahlbeteiligungInterval();
});

function onWaehleranzahlClicked() {
  increaseWaehlerByOne();
}

function onKeyupEvent(event: KeyboardEvent) {
  if (event.key === "+" && !isInputElement(event.target)) {
    increaseWaehlerByOne();
  }
}

function isInputElement(target: EventTarget | null): boolean {
  if (!target || !(target instanceof Element)) return false;
  const tagName = (target as Element).tagName?.toLowerCase();
  return (
    tagName === "input" ||
    tagName === "textarea" ||
    (target as Element).getAttribute("contenteditable") === "true"
  );
}
</script>
