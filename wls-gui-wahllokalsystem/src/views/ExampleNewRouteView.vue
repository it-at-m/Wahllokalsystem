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
          @click="previousPage"
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
      <v-card
        class="mx-auto mt-5"
        title="Dynamic Routing"
        width="300"
      >
        <v-col>
          <v-text-field
            clearable
            label="ID"
            v-model="textinput"
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
import { VCard, VCol, VContainer, VTextField } from "vuetify/components";

import { EXAMPLE_ROUTES_DYNAMIC, ROUTES_HOME } from "@/constants";
import router from "@/plugins/router";

let textinput = ref("defaultId");

function previousPage() {
  router.go(-1);
}
</script>
