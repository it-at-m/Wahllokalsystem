<template>
  <v-container max-width="800px">
    <base-feedback-card
      v-if="!isOfflineCacheReady"
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
import { storeToRefs } from "pinia";
import { onMounted, onUnmounted, ref } from "vue";

import BaseFeedbackCard from "@/components/common/cards/BaseFeedbackCard.vue";
import BaseOfflineLoading from "@/components/wlsComponents/BaseOfflineLoading.vue";
import { useOnlineOfflineStore } from "@/stores/onlineOfflineStore.ts";

const { isOfflineCacheReady } = storeToRefs(useOnlineOfflineStore());

const warnings = ref<string[]>([]);

let checkWarningsInterval: ReturnType<typeof setInterval> | undefined;

onMounted(async () => {
  checkWarningsInterval = setInterval(() => {
    warnings.value = [];

    if (!navigator.serviceWorker) {
      // Service Worker wurde in about:config deaktiviert
      warnings.value.push(
        'Aktivieren Sie den Service-Worker unter "about:config" mit dem Flag "dom.serviceWorkers.enabled".'
      );
    } else if (!isOfflineCacheReady.value) {
      // Seite wurde mit Shift-Reload aufgerufen, oder Nutzer befindet sich auf unsicherer (HTTP) Seite.
      warnings.value.push(
        'Aktivieren Sie unter "about:config" "devtools.serviceWorkers.testing.enabled", wenn Sie sich auf einer Testumgebung befinden.'
      );
      warnings.value.push(
        'Stellen Sie sicher, dass unter Einstellungen > "Datenschutz & Sicherheit" > "Cookies und Websitedaten" der Punkt "Behalten, bis" auf "sie nicht mehr gültig sind" gesetzt ist.'
      );
      warnings.value.push(
        'Halten Sie beim Aktualisieren der Seite nicht die "Umschalttaste" gedrückt.'
      );
    }
  }, 100);
});

onUnmounted(() => {
  if (checkWarningsInterval) clearInterval(checkWarningsInterval);
});
</script>
