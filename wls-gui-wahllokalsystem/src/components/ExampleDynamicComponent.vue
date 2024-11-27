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
      <v-btn
        @click="goBack"
        variant="text"
      >
        Go Back and try another one
      </v-btn>
    </p>

<script setup lang="ts">
import { VCol } from "vuetify/components";
import { useRouter } from 'vue-router';

const router = useRouter();
const goBack = () => {
  if (window.history.length > 1) {
    router.go(-1);
  } else {
    router.push('/'); // Fallback to home route
  }
};
</script>
  </v-col>
</template>

<script setup lang="ts">
import { VCol } from "vuetify/components";
</script>
