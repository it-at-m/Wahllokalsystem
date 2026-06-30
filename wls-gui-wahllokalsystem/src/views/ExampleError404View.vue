<template>
  <v-container>
    <v-col class="text-center">
      <h1>error 404</h1>
      <h1>page not found</h1>
    </v-col>
    <v-col class="text-center">
      <p>
        Back
        <router-link :to="routeWithName(ROUTES_HOME)"> Home</router-link>
      </p>
    </v-col>
  </v-container>
</template>

<script setup lang="ts">
import { ROUTES_HOME } from "@/constants";
import { useNavigationService } from "../composables/navigation/navigationService.ts";

const { routeWithName } = useNavigationService();
</script>
