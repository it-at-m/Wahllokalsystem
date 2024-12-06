<template>
  <v-container>
    <v-col class="text-center">
      <h2>You found a new View!</h2>
      <p>
        Route back Home
        <router-link :to="{ name: ROUTES_HOME }">via text</router-link>
        or
        <router-link :to="{ name: ROUTES_HOME }">
          <v-btn
            color="primary"
            density="compact"
          >
            via Button
          </v-btn>
        </router-link>
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
        <v-btn
          class="text-none px-1"
          density="compact"
          color="primary"
          @click="previousPage"
          >to previous Site
        </v-btn>
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
      <v-card
        class="mx-auto mt-5"
        title="Dynamic Routing"
        width="300"
      >
        <v-col>
          <v-text-field
            v-model="textinput"
            clearable
            label="ID"
          ></v-text-field>
          <p>
            Dynamic Route
            <router-link
              :to="{
                name: EXAMPLE_ROUTES_DYNAMIC,
                params: { wahlid: textinput },
              }"
              >to element with specific ID
            </router-link>
          </p>
        </v-col>
      </v-card>
    </v-col>
  </v-container>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { VBtn, VCard, VCol, VContainer, VTextField } from "vuetify/components";

import { EXAMPLE_ROUTES_DYNAMIC, ROUTES_HOME } from "@/constants";
import router from "@/plugins/router";

let textinput = ref("defaultId");

function previousPage() {
  if (window.history.length > 1) {
    router.go(-1);
  } else {
    router.push(ROUTES_HOME); // Fallback to home route
  }
}
</script>
