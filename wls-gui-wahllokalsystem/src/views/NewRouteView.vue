<template>
  <v-container>
    <v-row class="text-center">
      <h2>You found a new View!</h2>

      <p>Route back <router-link :to="{ name: ROUTES_HOME }"> Home </router-link></p>
      <p>What about a <router-link to="/nonexistant-route"> route that does not exist</router-link>?</p>
      <p>Route to external Link
        <a
            href="https://it-at-m.github.io/Wahllokalsystem/"
            target="_blank"
            rel="noopener noreferrer"
        >WLS-Doku</a
        >
      </p>
    </v-row>


  </v-container>
</template>

<script setup lang="ts">
import {VContainer} from "vuetify/components";
import {ROUTES_HOME} from "@/constants";
</script>