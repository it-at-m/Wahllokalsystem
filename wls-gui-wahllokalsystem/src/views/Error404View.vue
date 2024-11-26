<template>
  <v-container>
    <v-row class="text-center">
      <h1>error 404</h1>
      <h1>page not found</h1>
    </v-row>
    <v-row class="text-center">
      <p>
        Back
        <router-link :to="{ name: ROUTES_HOME }"> Home </router-link>
      </p>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ROUTES_HOME } from "@/constants";
</script>
