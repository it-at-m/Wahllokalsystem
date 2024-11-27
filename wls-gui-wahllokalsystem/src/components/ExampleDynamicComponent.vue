<template>
  <v-col class="text-center">
    <p>
      This is a specific site for an element with ID: {{ $route.params.wahlid }}
    </p>
    <p>
      <a
        href="#"
        @click="$router.go(-1)"
      >
        Go Back and try another one
      </a>
    </p>
  </v-col>
</template>

<script setup lang="ts">
import { VCol } from "vuetify/components";
</script>
