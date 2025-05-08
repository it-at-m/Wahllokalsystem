<template>
  <v-container class="d-flex flex-column align-center">
    <p>This view shows how the different Toast-Notifications look and work.</p>
    <v-container
      class="d-flex flex-column align-center"
      min-height="200"
      min-width="300"
    >
      <v-btn
        id="success-button"
        class="mb-5"
        @click="toastErfolg"
        >Erfolg
      </v-btn>
      <v-btn
        id="warning-button"
        class="mb-5"
        @click="toastWarnung"
        >WARNUNG
      </v-btn>
      <v-btn
        id="error-button"
        @click="toastError"
        >FEHLER
      </v-btn>
    </v-container>
  </v-container>
</template>
<script setup lang="ts">
import { VBtn, VContainer } from "vuetify/components";

import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

const userNotificationService = useUserNotificationService();

function toastErfolg(): void {
  userNotificationService.addNotification(
    "Erfolg",
    UserNotificationCategoryEnum.SUCCESS
  );
}

function toastWarnung(): void {
  userNotificationService.addNotification(
    "Warnung",
    UserNotificationCategoryEnum.WARNING
  );
}

function toastError(): void {
  userNotificationService.addNotification(
    "Fehler",
    UserNotificationCategoryEnum.ERROR
  );
}
</script>
