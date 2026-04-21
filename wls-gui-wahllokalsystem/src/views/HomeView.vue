<template>
  <v-container max-width="800px">
    <base-feedback-card
      v-if="!swActive"
      title="Offline-Fähigkeit nicht verfügbar"
      type="warning"
    >
      <div>
        Sie können aktuell nicht Offline arbeiten. Versuchen Sie eine der
        folgenden Lösungen:
        <ul>
          <!--          <template
            v-for="item in warnings"
            :key="item.key"
          >
            <li>{{ item }}</li>
          </template>-->
        </ul>
        Aktualisieren Sie danach diese Seite.
      </div>
    </base-feedback-card>
    <base-offline-loading />
  </v-container>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";

import BaseFeedbackCard from "@/components/common/cards/BaseFeedbackCard.vue";
import BaseOfflineLoading from "@/components/wlsComponents/BaseOfflineLoading.vue";
import { useServiceWorkerUtils } from "@/composables/serviceWorker/serviceWorkerUtils.ts";

const { isServiceWorkerActive } = useServiceWorkerUtils();

const swActive = ref(false);

onMounted(async () => {
  setInterval(() => {
    swActive.value = isServiceWorkerActive();
  }, 1000);
});
</script>
