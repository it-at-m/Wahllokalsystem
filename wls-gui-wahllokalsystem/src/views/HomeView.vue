<template>
  <v-container max-width="800px">
    <base-feedback-card
      v-if="!swActive"
      title="Service-Worker deaktiviert"
      type="warning"
    >
      <div>
        Sie können aktuell nicht Offline arbeiten. Versuchen Sie eine der
        folgenden Lösungen:
        <ul class="ml-7 my-3">
          <template
            v-for="item in warnings"
            :key="item"
          >
            <li>{{ item }}</li>
          </template>
        </ul>
        Aktualisieren Sie danach diese Seite.
      </div>
    </base-feedback-card>
    <base-offline-loading />
  </v-container>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from "vue";

import BaseFeedbackCard from "@/components/common/cards/BaseFeedbackCard.vue";
import BaseOfflineLoading from "@/components/wlsComponents/BaseOfflineLoading.vue";
import { useServiceWorkerUtils } from "@/composables/serviceWorker/serviceWorkerUtils.ts";

const { isServiceWorkerActive, isServiceWorkerEnabled } =
  useServiceWorkerUtils();

const swEnabled = ref(isServiceWorkerEnabled());
const swActive = ref(swEnabled.value && isServiceWorkerActive());
const warnings = ref<string[]>([]);

let swActiveInterval: ReturnType<typeof setInterval> | undefined;
let swEnabledInterval: ReturnType<typeof setInterval> | undefined;
let checkWarningsInterval: ReturnType<typeof setInterval> | undefined;

onMounted(async () => {
  swActiveInterval = setInterval(() => {
    swActive.value = isServiceWorkerActive();
    if (swActive.value === true) {
      clearInterval(swActiveInterval);
    }
  }, 500);

  swEnabledInterval = setInterval(() => {
    swEnabled.value = isServiceWorkerEnabled();
    if (swEnabled.value === true) {
      clearInterval(swEnabledInterval);
    }
  }, 500);

  checkWarningsInterval = setInterval(() => {
    warnings.value = [];

    if (!swEnabled.value) {
      // Service Worker wurde in about:config deaktiviert
      warnings.value.push(
        'Aktivieren Sie den Service-Worker unter "about:config" mit dem Flag "dom.serviceWorkers.enabled".'
      );
    } else if (!swActive.value) {
      // Seite wurde mit Shift-Reload aufgerufen, oder Nutzer befindet sich auf unsicherer (HTTP) Seite.
      warnings.value.push(
        'Aktivieren Sie unter "about:config" "devtools.serviceWorkers.testing.enabled", wenn Sie sich auf der C1, C2 oder K1-Umgebung befinden.'
      );
      warnings.value.push(
        'Stellen Sie sicher, dass unter Einstellungen > "Datenschutz & Sicherheit" > "Cookies und Websitedaten" der Punkt "Behalten, bis" auf "sie nicht mehr gültig sind" gesetzt ist.'
      );
      warnings.value.push(
        'Halten Sie beim Aktualisieren der Seite nicht die "Umschalttaste" gedrückt.'
      );
    }

    if (warnings.value.length === 0) {
      clearInterval(checkWarningsInterval);
    }
  }, 100);
});

onUnmounted(() => {
  if (swActiveInterval) clearInterval(swActiveInterval);
  if (swEnabledInterval) clearInterval(swEnabledInterval);
  if (checkWarningsInterval) clearInterval(checkWarningsInterval);
});
</script>
