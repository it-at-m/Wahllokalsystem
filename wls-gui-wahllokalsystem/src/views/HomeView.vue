<template>
  <v-container max-width="800px">
    <base-feedback-card
      v-if="!isOfflineCacheReady"
      title="Service-Worker deaktiviert"
      type="warning"
    >
      <div>
        Sie können aktuell nicht Offline arbeiten. Bitte kontaktieren Sie das
        Wahlamt.
      </div>
    </base-feedback-card>
    <base-offline-loading />
  </v-container>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";

import BaseFeedbackCard from "@/components/common/cards/BaseFeedbackCard.vue";
import BaseOfflineLoading from "@/components/wlsComponents/BaseOfflineLoading.vue";
import { useOnlineOfflineStore } from "@/stores/onlineOfflineStore.ts";

const { isOfflineCacheReady } = storeToRefs(useOnlineOfflineStore());
</script>
