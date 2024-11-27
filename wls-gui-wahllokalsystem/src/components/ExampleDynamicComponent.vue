<template>
  <v-col class="text-center">
<template>
  <v-col class="text-center">
    <p>
      This is a specific site for an element with ID: {{ wahlId }}
    </p>
  </v-col>
</template>

<script setup lang="ts">
import { VCol } from "vuetify/components";
import { computed } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();
const wahlId = computed(() => route.params.wahlid as string);
</script>
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
