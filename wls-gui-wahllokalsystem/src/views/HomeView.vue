<template>
  <v-container max-width="800px">
    <base-offline-loading />
  </v-container>
</template>

<script setup lang="ts">
import { useRegisterSW } from "virtual:pwa-register/vue";
import { VContainer } from "vuetify/components";

import BaseOfflineLoading from "@/components/wlsComponents/BaseOfflineLoading.vue";

useRegisterSW({
  immediate: true,
  onNeedRefresh: () => console.log("onNeedRefresh"),
  onOfflineReady: () => console.log("onOfflineReady"),
  onRegisteredSW: (_, registration) => {
    console.log("onRegisteredSW");
  },
  onRegisterError: () => console.log("onRegisterError"),
});
</script>
