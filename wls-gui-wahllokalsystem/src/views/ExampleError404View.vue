<template>
  <v-container>
    <v-col class="text-center">
      <h1>error 404</h1>
      <h1>page not found</h1>
    </v-col>
    <v-col class="text-center">
      <p>
        Back
        <router-link :to="{ name: ROUTES_HOME }"> Home</router-link>
      </p>
    </v-col>
  </v-container>
</template>

<script setup lang="ts">
import { VCol, VContainer } from "vuetify/components";

import { ROUTES_HOME } from "@/constants";
</script>
