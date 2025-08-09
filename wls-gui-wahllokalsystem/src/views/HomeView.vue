<template>
  <v-container max-width="800px">
    <base-offline-loading />
  </v-container>
</template>

<script setup lang="ts">
import { useRegisterSW } from "virtual:pwa-register/vue";
import { VContainer } from "vuetify/components";

import BaseOfflineLoading from "@/components/wlsComponents/BaseOfflineLoading.vue";
import { useLogging } from "@/composables/common/logging.ts";
import HomeView from "@/views/HomeView.vue";

const { log } = useLogging(HomeView.__name ?? "");

useRegisterSW({
  immediate: true,
  onNeedRefresh: () => log("onNeedRefresh"),
  onOfflineReady: () => log("onOfflineReady"),
  onRegisteredSW: () => {
    log("onRegisteredSW");
  },
  onRegisterError: () => log("onRegisterError"),
});
</script>
