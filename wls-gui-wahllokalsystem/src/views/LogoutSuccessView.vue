<template>
  <v-container class="d-flex flex-column align-center ga-5">
    <div class="text-h4">Sie haben sich erfolgreich abgemeldet</div>
    <v-icon
      icon="$locked"
      size="150"
      color="grey-darken-1"
    />
    <v-btn
      color="primary"
      text="Zur Login-Maske"
      @click="forwardToLoginPage"
    />
  </v-container>
</template>
<script setup lang="ts">
import { useLogoutService } from "@/composables/user/logoutService.ts";

const { forwardToLoginPage } = useLogoutService();
</script>
