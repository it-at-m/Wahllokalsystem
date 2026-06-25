<template>
  <v-container class="d-flex flex-column align-center ga-5">
    <div class="text-h4">
      <span v-if="isViewForLogoutByInactivity"
        >Sie wurden wegen Inaktivität abgemeldet.</span
      >
      <span v-else>Sie haben sich erfolgreich abgemeldet</span>
    </div>
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
import { computed } from "vue";
import { useRoute } from "vue-router";

import { useLogoutService } from "@/composables/user/logoutService.ts";
import { LOGOUT_BY_INACTIVITY_QUERY_PARAMETER } from "@/constants.ts";

const { forwardToLoginPage } = useLogoutService();
const route = useRoute();

const isViewForLogoutByInactivity = computed(
  () => route.query[LOGOUT_BY_INACTIVITY_QUERY_PARAMETER] !== undefined
);
</script>
