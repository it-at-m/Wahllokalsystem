<template>
  <v-container>
    <v-col class="text-center">
      <h2>You found a new View!</h2>

      <p>
        Route back
        <router-link :to="{ name: ROUTES_HOME }"> Home</router-link>
      </p>
      <p>
        What about a
        <router-link to="/nonexistant-route">
          route that does not exist
        </router-link>
        ?
      </p>
      <p>
        Back
        <a
          href="#"
          @click.prevent="previousPage"
        >
          to previous Site</a
        >
      </p>
      <p>
        Route to external Link
        <a
          href="https://it-at-m.github.io/Wahllokalsystem/"
          target="_blank"
          rel="noopener noreferrer"
          >WLS-Doku</a
        >
      </p>
    </v-col>
  </v-container>
</template>

<script setup lang="ts">
import { VCol, VContainer } from "vuetify/components";

import { ROUTES_HOME } from "@/constants";
import router from "@/plugins/router";

function previousPage() {
  router.go(-1);
}
</script>
