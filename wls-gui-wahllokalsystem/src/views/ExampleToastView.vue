<template>
  <div>
    <v-btn @click="toastErfolg">Erfolg</v-btn>
    <v-btn @click="toastWarnung">WARNUNG</v-btn>
    <v-btn @click="toastError">FEHLER</v-btn>
  </div>
</template>
<script setup lang="ts">
import { VBtn } from "vuetify/components";

import { useUserNotificationStore } from "@/stores/useUserNotificationStore.ts";
import { UserNotificationCategoryEnum } from "@/types/wlsTypes/UserNotificationCategoryEnum.ts";

const userNotificationStore = useUserNotificationStore();

function toastErfolg(): void {
  userNotificationStore.addNotification(
    "Erfolg",
    UserNotificationCategoryEnum.ERFOLG
  );
}

function toastWarnung(): void {
  userNotificationStore.addNotification(
    "Warnung",
    UserNotificationCategoryEnum.WARNUNG
  );
}

function toastError(): void {
  userNotificationStore.addNotification(
    "Fehler",
    UserNotificationCategoryEnum.FEHLER
  );
}
</script>
<style scoped></style>
