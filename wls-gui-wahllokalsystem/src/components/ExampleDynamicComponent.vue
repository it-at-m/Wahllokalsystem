<template>
  <v-col class="text-center">
    <p>This is a specific site for an element with ID: {{ wahlId }}</p>
    <p>
      <v-btn
        @click="goBack"
        class="mt-2"
        color="primary"
      >
        Go Back and try another one
      </v-btn>
    </p>
  </v-col>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { VBtn, VCol } from "vuetify/components";

import { ROUTES_HOME } from "@/constants";

const route = useRoute();
const router = useRouter();
const wahlId = computed(() => route.params.wahlid as string);
const goBack = () => {
  if (window.history.length > 1) {
    router.go(-1);
  } else {
    router.push(ROUTES_HOME); // Fallback to home route
  }
};
</script>
