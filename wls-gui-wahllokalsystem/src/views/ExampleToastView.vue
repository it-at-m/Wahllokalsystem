<template>
  <v-container class="d-flex flex-column align-center">
    <p>This view shows how the different Toast-Notifications look and work.</p>
    <v-container
      class="d-flex flex-column align-center"
      min-height="200"
      min-width="300"
    >
      <v-btn
        class="mb-5"
        @click="toastErfolg"
        >Erfolg
      </v-btn>
      <v-btn
        class="mb-5"
        @click="toastWarnung"
        >WARNUNG
      </v-btn>
      <v-btn @click="toastError">FEHLER</v-btn>
    </v-container>
  </v-container>
</template>
<script setup lang="ts">
import { VBtn, VContainer } from "vuetify/components";

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
