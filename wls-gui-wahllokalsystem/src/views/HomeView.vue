<template>
  <v-container max-width="800px">
    <base-feedback-card
      v-if="!isOfflineCacheReady"
      title="Service-Worker deaktiviert"
      type="warning"
    >
      <div>
        Sie können aktuell nicht Offline arbeiten. {{ CONTACT_SUPPORT }}
      </div>
    </base-feedback-card>
    <base-offline-loading />
  </v-container>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";

import BaseFeedbackCard from "@/components/common/cards/BaseFeedbackCard.vue";
import BaseOfflineLoading from "@/components/wlsComponents/BaseOfflineLoading.vue";
import { CONTACT_SUPPORT } from "@/constants.ts";
import { useOnlineOfflineStore } from "@/stores/onlineOfflineStore.ts";

const { isOfflineCacheReady } = storeToRefs(useOnlineOfflineStore());
</script>
